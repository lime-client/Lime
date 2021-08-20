package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.render.ColorUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ChinaHat extends Module {

    public ChinaHat() {
        super("China Hat", Category.RENDER);
    }

    @EventTarget
    public void on3D(Event3D e) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GL11.glLineWidth(1.5F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_CULL_FACE);

        double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
        double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
        double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        for(int i = 0; i < 361; ++i) {
            RenderUtils.glColor(ColorUtils.setAlpha(HUD.getColor(0), 150));
            GL11.glVertex3d(x, y + mc.thePlayer.height + 0.6, z);
            GL11.glVertex3d(x - Math.sin(Math.toRadians(i)), y + mc.thePlayer.height + 0.1, z + Math.cos(Math.toRadians(i)));

        }
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i = 0; i < 361; ++i) {
            RenderUtils.glColor(ColorUtils.setAlpha(HUD.getColor(0).darker(), 150));
            GL11.glVertex3d(x - Math.sin(Math.toRadians(i)), y + mc.thePlayer.height + 0.1, z + Math.cos(Math.toRadians(i)));

        }
        GL11.glEnd();
        GlStateManager.enableAlpha();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        GL11.glPopMatrix();
    }
}
