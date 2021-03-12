package lime.module.impl.player;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import lime.module.impl.combat.KillAura;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ChestAura extends Module {
    public ChestAura() {
        super("ChestAura", 0, Category.PLAYER);
        Lime.setmgr.rSetting(new Setting("Reach", this, 4, 0.2, 6, false));
    }

    @Override
    public void onEnable() {
        alreadyOpened.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    public ArrayList<TileEntityChest> alreadyOpened = new ArrayList<>();
    @EventTarget
    public void onMotion(EventMotion e){
        if(!mc.thePlayer.onGround || (mc.currentScreen instanceof GuiChest)) return;
        for (Entity et : mc.theWorld.loadedEntityList){
            if(et == mc.thePlayer) continue;
            if(et.getDistanceToEntity(mc.thePlayer) < 6) return;
        }
        for(TileEntity te : mc.theWorld.loadedTileEntityList){
            if(te instanceof TileEntityChest){
                TileEntityChest chest = (TileEntityChest) te;
                float x = chest.getPos().getX();
                float y = chest.getPos().getY();
                float z = chest.getPos().getZ();
                if(mc.thePlayer.getDistance(x, y, z) < getSettingByName("Reach").getValDouble() && mc.currentScreen == null && !isIn(chest)){
                    float[] rot = KillAura.getRotationFromPosition2(x, y, z);
                    e.setYaw(rot[0]);
                    e.setPitch(rot[1]);
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(chest.getPos(), getFacingDirection(chest.getPos()).getIndex(), mc.thePlayer.getCurrentEquippedItem(), x, y, z));
                    alreadyOpened.add(chest);
                }
            }
        }
    }
    public boolean isIn(TileEntityChest tileEntityChest){
        for(TileEntityChest tec : alreadyOpened){
            if(tec == tileEntityChest)
                return true;
        }
        return false;
    }
    private EnumFacing getFacingDirection(final BlockPos pos) {
        EnumFacing direction = null;
        if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isFullBlock()) {
            direction = EnumFacing.UP;
        }
        final MovingObjectPosition rayResult = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null) {
            return rayResult.sideHit;
        }
        return direction;
    }


}
