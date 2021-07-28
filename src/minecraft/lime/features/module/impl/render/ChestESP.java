package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.utils.render.ColorUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleData(name = "Chest ESP", category = Category.RENDER)
public class ChestESP extends Module {

    private final EnumValue mode = new EnumValue("Mode", this, "Outline", "Outline", "Fill");

    @EventTarget
    public void on3D(Event3D e) {
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if(tileEntity instanceof TileEntityChest) {
                BlockPos pos = tileEntity.getPos();
                GL11.glPushMatrix();
                GL11.glLineWidth(2.5f);
                RenderUtils.drawBox(pos.getX(), pos.getY(), pos.getZ(), 1, ColorUtils.setAlpha(HUD.getColor(0), 200), true, mode.is("fill"));
                GL11.glPopMatrix();
            }
        }
    }
}
