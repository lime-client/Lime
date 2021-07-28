package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventRendererEntity;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.combat.KillAura;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleData(name = "Chams", category = Category.RENDER)
public class Chams extends Module {

    private final EnumValue mode = new EnumValue("Mode", this, "Colored", "Colored");
    private final BoolValue onlyTargets = new BoolValue("Only Targets", this, false);

    @EventTarget
    public void onRendererLivingEntity(EventRendererEntity e) {
        this.setSuffix(mode.getSelected());
        e.setCanceled(true);
        if(e.isPre()) {
            // Only colored
            // TODO: THROUGH
            switch(mode.getSelected().toLowerCase()) {
                case "colored":
                    if(!(e.getEntity() instanceof EntityPlayer) || (onlyTargets.isEnabled() && KillAura.getEntity() != e.getEntity())) {
                        e.setCanceled(false);
                        return;
                    }
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GlStateManager.disableTexture2D();
                    GlStateManager.disableDepth();
                    GL11.glDisable(GL11.GL_LIGHTING);
                    Color color = HUD.getColor(0);
                    if(e.getEntity().hurtTime > 0) color = color.darker();
                    RenderUtils.glColor(color);
                    e.getModel().render(e.getEntity(), e.getX(), e.getY(), e.getZ(), e.getX2(), e.getY2(), e.getZ2());
                    GlStateManager.enableDepth();
                    RenderUtils.glColor(color);
                    e.getModel().render(e.getEntity(), e.getX(), e.getY(), e.getZ(), e.getX2(), e.getY2(), e.getZ2());
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GlStateManager.enableTexture2D();
                    GL11.glPopAttrib();
                    break;
            }
        }
    }
}
