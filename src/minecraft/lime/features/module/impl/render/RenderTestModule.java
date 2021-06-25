package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.combat.CombatUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@ModuleData(name = "Render Test", category = Category.RENDER)
public class RenderTestModule extends Module {
    @EventTarget
    public void onRender(Event3D e) {
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(entity instanceof EntityLivingBase && entity != mc.thePlayer) {
                GL11.glPushMatrix();
                GL11.glTranslated(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX - 0.5,
                        entity.lastTickPosY + entity.getEyeHeight() + 0.5 + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                        entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ - 0.5);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                //GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glRotatef(-180, 1, 0, 0);

                RenderUtils.drawImage(new ResourceLocation("lime/clickgui/frame/combat.png"), 0, 0, 1, 1);

                GL11.glDisable(GL11.GL_BLEND);
                //GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);

                GL11.glPopMatrix();
            }
        }
    }
}
