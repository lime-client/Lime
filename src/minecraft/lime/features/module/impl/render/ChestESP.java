package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.utils.render.RenderUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;

import java.awt.*;

@ModuleData(name = "Chest ESP", category = Category.RENDER)
public class ChestESP extends Module {

    private enum Mode {
        OUTLINE, FILL
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.OUTLINE);

    @EventTarget
    public void on3D(Event3D e) {
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if(tileEntity instanceof TileEntityChest) {
                BlockPos pos = tileEntity.getPos();

                RenderUtils.drawBox(pos.getX(), pos.getY(), pos.getZ(), 1, HUD.getColor(0), true, mode.is("fill"));
            }
        }
    }
}
