package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventRendererEntity;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.KillAura;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.ColorProperty;
import lime.features.setting.impl.EnumProperty;
import lime.utils.render.RenderUtils;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Chams extends Module {

    public Chams() {
        super("Chams", Category.VISUALS);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Colored", "Colored");
    private final ColorProperty color = new ColorProperty("Color", this, new Color(200, 0, 0).getRGB());
    private final BooleanProperty onlyTargets = new BooleanProperty("Only Targets", this, false);
    private final BooleanProperty disableLightning = new BooleanProperty("Disable Lightning", this, false);

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
                    Color color = new Color(this.color.getColor());
                    GL11.glPushAttrib(1048575);
                    GL11.glDisable(3008);
                    GL11.glDisable(3553);
                    if(disableLightning.isEnabled()) {
                        GL11.glDisable(2896);
                    }
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glLineWidth(1.5f);
                    GL11.glEnable(2960);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);
                    GL11.glEnable(10754);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
                    RenderUtils.glColor(color);
                    e.getModel().render(e.getEntity(), e.getX(), e.getY(), e.getZ(), e.getX2(), e.getY2(), e.getZ2());
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(true);
                    RenderUtils.glColor(color);
                    e.getModel().render(e.getEntity(), e.getX(), e.getY(), e.getZ(), e.getX2(), e.getY2(), e.getZ2());
                    GL11.glEnable(3042);
                    if(disableLightning.isEnabled()) {
                        GL11.glEnable(2896);
                    }
                    GL11.glEnable(3553);
                    GL11.glEnable(3008);
                    GL11.glPopAttrib();
                    break;
            }
        }
    }
}
