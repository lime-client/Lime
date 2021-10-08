package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.AntiBot;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class PointerESP extends Module {
    public PointerESP() {
        super("Pointer ESP", Category.VISUALS);
    }

    @EventTarget
    public void on2D(Event2D e) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int size = 100;
        double xOffset = scaledResolution.getScaledWidth() / 2F - 50.2;
        double yOffset = scaledResolution.getScaledHeight() / 2F - 49.5;
        double playerOffsetX = mc.thePlayer.posX;
        double playerOffSetZ = mc.thePlayer.posZ;

        AntiBot antiBot = (AntiBot) Lime.getInstance().getModuleManager().getModuleC(AntiBot.class);

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if(entity instanceof EntityPlayer && entity != mc.thePlayer && !antiBot.checkBot((EntityPlayer) entity)) {
                double pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - playerOffsetX) * 0.2);
                double pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - playerOffSetZ) * 0.2);
                double cos = Math.cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                double sin = Math.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                double rotY = -(pos2 * cos - pos1 * sin);
                double rotX = -(pos1 * cos + pos2 * sin);
                double var7 = -rotX;
                double var9 = -rotY;
                if(MathHelper.sqrt_double(var7 * var7 + var9 * var9) < size / 2F - 4) {
                    double angle = (Math.atan2(rotY, rotX) * 180 / Math.PI);
                    double x = ((size / 2F) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2F;
                    double y = ((size / 2F) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2F;
                    GL11.glPushMatrix();
                    GL11.glTranslated(x,y,0);
                    GL11.glRotatef((float) angle, 0, 0, 1);
                    GL11.glScaled(1.5, 1, 1);
                    drawTriAngle(0F, 0F, 2.2F, 3F, new Color(255, 255, 255));
                    drawTriAngle(0F, 0F, 1.5F, 3F, new Color(255, 255, 255));
                    drawTriAngle(0F, 0F, 1.0F, 3F, new Color(255, 255, 255));
                    drawTriAngle(0F, 0F, 0.5F, 3F, new Color(255, 255, 255));
                    drawTriAngle(0F, 0F, 2.2F, 3F, new Color(255, 255, 255));
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public static void drawTriAngle(float cx, float cy, float r, float n, Color color){
        GL11.glPushMatrix();
        cx *= 2.0;
        cy *= 2.0;
        double b = 6.2831852 / n;
        double p = Math.cos(b);
        double s = Math.sin(b);
        r *= 2.0;
        double x = r;
        double y = 0.0;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GlStateManager.color(0,0,0);
        GlStateManager.resetColor();
        RenderUtils.glColor(color);
        GL11.glBegin(2);
        int ii = 0;
        while (ii < n) {
            GL11.glVertex2d(x + cx, y + cy);
            double t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glPopMatrix();
    }
}
