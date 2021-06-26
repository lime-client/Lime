package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventRendererEntity;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleData(name = "Chams", category = Category.RENDER)
public class Chams extends Module {
    private enum Mode {
        COLORED
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.COLORED);

    @EventTarget
    public void onRendererLivingEntity(EventRendererEntity e) {
        e.setCanceled(true);
        if(e.isPre()) {
            switch(mode.getSelected().name().toLowerCase()) {
                case "colored":
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GlStateManager.disableTexture2D();
                    GlStateManager.disableDepth();
                    GL11.glDisable(GL11.GL_LIGHTING);
                    RenderUtils.glColor(new Color(255, 0, 0));
                    e.getModel().render(e.getEntity(), e.getX(), e.getY(), e.getZ(), e.getX2(), e.getY2(), e.getZ2());
                    GlStateManager.enableDepth();
                    RenderUtils.glColor(new Color(255, 0, 0));
                    e.getModel().render(e.getEntity(), e.getX(), e.getY(), e.getZ(), e.getX2(), e.getY2(), e.getZ2());
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GlStateManager.enableTexture2D();
                    GL11.glPopAttrib();
                    break;
            }
        }
    }
}
