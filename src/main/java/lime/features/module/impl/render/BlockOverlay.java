package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.render.RenderUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

public class BlockOverlay extends Module {

    public BlockOverlay() {
        super("Block Overlay", Category.VISUALS);
    }

    @EventTarget
    public void on3D(Event3D e) {
        if(mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            if(!pos.getBlock().isFullBlock()) return;
            GL11.glPushMatrix();
            GL11.glLineWidth(2.5f);
            RenderUtils.glColor(HUD.getColor(0).getRGB());
            RenderUtils.drawBox(pos.getX(), pos.getY(), pos.getZ(), 1, HUD.getColor(0), true, false);
            GL11.glLineWidth(1);
            GL11.glPopMatrix();
        }
    }
}
