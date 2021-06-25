package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

@ModuleData(name = "Click Teleport", category = Category.PLAYER)
public class ClickTeleport extends Module {

    private int delay = 0;

    @Override
    public void onDisable() {
        delay = 0;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(mc.thePlayer.getCurrentEquippedItem() != null && (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) || !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood)) && mc.thePlayer.isSneaking()) {
            if(e.isPre()) {
                if(delay == 0 && Mouse.isButtonDown(2)) {
                    teleportToBlockPos(getBlinkBlock().getBlockPos().add(0, 1, 0));
                    delay = 5;
                }

                if(delay > 0)
                    delay--;
            }
        }
    }

    private MovingObjectPosition getBlinkBlock() {
        Vec3 eyeEight = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        Vec3 lookVec = mc.thePlayer.getLook(mc.timer.renderPartialTicks);
        Vec3 vec = eyeEight.addVector(lookVec.xCoord * 70, lookVec.yCoord * 70, lookVec.zCoord * 70);
        return mc.thePlayer.worldObj.rayTraceBlocks(eyeEight, vec, false, false, true);
    }

    private void teleportToBlockPos(BlockPos blockPos) {
        double distance = Math.sqrt(mc.thePlayer.getDistanceSq(blockPos));
        double distancePackets = 4;

        if(distance > distancePackets) {
            double packetsNumber = Math.round(distance / distancePackets + 0.5) - 1;
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            for(int i = 1; i < packetsNumber; ++i) {
                x += (blockPos.getX() - mc.thePlayer.posX) / (packetsNumber);
                y += (blockPos.getY() - mc.thePlayer.posY) / (packetsNumber);
                z += (blockPos.getZ() - mc.thePlayer.posZ) / (packetsNumber);

                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
            }

            mc.thePlayer.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        } else
            mc.thePlayer.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
