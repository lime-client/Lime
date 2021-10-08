package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Tracers extends Module {

    public Tracers() {
        super("Tracers", Category.VISUALS);
    }

    @EventTarget
    public void on3D(Event3D e) {
        for(Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(entity instanceof EntityPlayer && entity != mc.thePlayer) {
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY + entity.getEyeHeight();
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

                GL11.glPushMatrix();

                GL11.glLoadIdentity();
                mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks); // Lignes pas stable sinon (à cause du view bobbing sûrement)
                GlStateManager.disableTexture2D();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GlStateManager.disableDepth();
                GlStateManager.enableBlend();

                RenderUtils.glColor(HUD.getColor(0));
                GL11.glLineWidth(1.5f);

                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
                GL11.glVertex3d(x, y, z);
                GL11.glEnd();

                GlStateManager.enableDepth();
                GlStateManager.disableBlend();
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GlStateManager.enableTexture2D();

                GL11.glPopMatrix();

            }
        }
    }
}
