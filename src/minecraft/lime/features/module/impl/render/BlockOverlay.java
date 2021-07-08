package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.render.RenderUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

import java.awt.Color;

@ModuleData(name = "Block Overlay", category = Category.RENDER)
public class BlockOverlay extends Module {
    @EventTarget
    public void on3D(Event3D e) {
        if(mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            if(!pos.getBlock().isFullBlock()) return;
            RenderUtils.drawBox(pos.getX(), pos.getY(), pos.getZ(), 1, HUD.getColor(0), true, false);
        }
    }
}
