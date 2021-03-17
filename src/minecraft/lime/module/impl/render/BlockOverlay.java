package lime.module.impl.render;

import lime.Lime;
import lime.events.EventTarget;
import lime.events.impl.Event3D;
import lime.module.Module;
import lime.module.impl.combat.KillAura;
import lime.module.impl.player.ClickTP;
import lime.utils.render.UtilGL;
import net.minecraft.block.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.awt.*;

public class BlockOverlay extends Module {
    public BlockOverlay(){
        super("BlockOverlay", 0, Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void on3D(Event3D e){
        if(mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
            BlockPos b = mc.objectMouseOver.getBlockPos();
            if(b.getBlock() instanceof BlockAir || b.getBlock() instanceof BlockRedstoneComparator || b.getBlock() instanceof BlockRedstoneLight || b.getBlock() instanceof BlockRedstoneDiode || b.getBlock() instanceof BlockRedstoneWire || !(new BlockPos(b.getX(), b.getY() + 1, b.getZ()).getBlock() instanceof BlockAir)) return;
            if(!(b.getBlock().isFullBlock())) return;
            HUD hud = (HUD) Lime.moduleManager.getModuleByName("HUD");
            UtilGL.drawBox(new Vec3(b.getX(), b.getY() + 0.9999, b.getZ()), new Vec3(1, 0.0001, 1), KillAura.setAlpha(hud.getColor(0), 50));
        }
    }
}
