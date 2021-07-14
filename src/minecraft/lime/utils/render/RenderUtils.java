package lime.utils.render;

import lime.core.Lime;
import lime.features.module.impl.render.HUD;
import lime.utils.IUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vector3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils implements IUtil {

    private static final Frustum frustrum = new Frustum();
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);

    public static void drawRadius(Entity entity, double rad) {
        int sides = 6;
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(6.0f);
        glBegin(GL_LINE_STRIP);

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.elapsedPartialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.elapsedPartialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.elapsedPartialTicks - mc.getRenderManager().viewerPosZ;

        double pix2 = Math.PI * 2.0D;
        for (int i = 0; i <= 90; ++i) {
            glColor(HUD.getColor(i));
            glVertex3d(x + rad * Math.cos(i * pix2 / sides), y, z + rad * Math.sin(i * pix2 / sides));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(2.0f);
        glBegin(GL_LINE_STRIP);

        float r1 = ((float) 1 / 255) * Color.black.getRed();
        float g1 = ((float) 1 / 255) * Color.black.getGreen();
        float b1 = ((float) 1 / 255) * Color.black.getBlue();

        for (int i = 0; i <= 90; ++i) {
            glColor3f(r1, g1, b1);
            glVertex3d(x + (rad + 0.01) * Math.cos(i * pix2 / sides), y, z + (rad + 0.01) * Math.sin(i * pix2 / sides));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(2.0f);
        glBegin(GL_LINE_STRIP);


        for (int i = 0; i <= 90; ++i) {
            glColor3f(r1, g1, b1);
            glVertex3d(x + (rad - 0.01) * Math.cos(i * pix2 / sides), y, z + (rad - 0.01) * Math.sin(i * pix2 / sides));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);

        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.resetColor();
    }

    public static void enable(final boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
    }

    public static void disable(final boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void startSmooth() {
        GL11.glEnable(2848);
        GL11.glEnable(2881);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
    }

    public static void endSmooth() {
        GL11.glDisable(2848);
        GL11.glDisable(2881);
        GL11.glEnable(2832);
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static final void drawCircle(float x, float y, float radius, int inside, int outside) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        glColor(inside);
        GL11.glBegin(9);
        for (int i = 0; i < 360; i++) {
            GL11.glVertex2d(x - Math.sin(Math.toRadians(i)) * radius, y + Math.cos(Math.toRadians(i)) * radius);
        }
        GL11.glEnd();
        glColor(outside);
        GL11.glBegin(2);
        for (int i = 0; i < 360; i++) {
            GL11.glVertex2d(x - Math.sin(Math.toRadians(i)) * radius, y + Math.cos(Math.toRadians(i)) * radius);
        }
        GL11.glEnd();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static Vector3d project(double x, double y, double z) {
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(2982, modelview);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        if (GLU.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, vector)) {
            return new Vector3d(vector.get(0) / (float)new ScaledResolution(mc).getScaleFactor(), ((float) Display.getHeight() - vector.get(1)) / (float)new ScaledResolution(mc).getScaleFactor(), vector.get(2));
        }
        return null;
    }

    public static void drawBox(double x, double y, double z, double yOffset, Color color, boolean depth, boolean fill) {
        GL11.glPushMatrix();
        if(depth)
            GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL_LINE_SMOOTH);

        double radius = 1;

        x -= mc.getRenderManager().viewerPosX;
        y -= mc.getRenderManager().viewerPosY;
        z -= mc.getRenderManager().viewerPosZ;

        double min_x = x;
        double min_y = y;
        double min_z = z;
        double max_x = x + 1;
        double max_y = y + yOffset;
        double max_z = z + 1;

        glColor(color);

        if(fill) {
            glBegin(GL_QUADS);
            glVertex3d(max_x, max_y, min_z);
            glVertex3d(min_x, max_y, min_z);
            glVertex3d(min_x, max_y, max_z);
            glVertex3d(max_x, max_y, max_z);
            glVertex3d(max_x, min_y, max_z);
            glVertex3d(min_x, min_y, max_z);
            glVertex3d(min_x, min_y, min_z);
            glVertex3d(max_x, min_y, min_z);
            glVertex3d(max_x, max_y, max_z);
            glVertex3d(min_x, max_y, max_z);
            glVertex3d(min_x, min_y, max_z);
            glVertex3d(max_x, min_y, max_z);
            glVertex3d(max_x, min_y, min_z);
            glVertex3d(min_x, min_y, min_z);
            glVertex3d(min_x, max_y, min_z);
            glVertex3d(max_x, max_y, min_z);
            glVertex3d(min_x, max_y, max_z);
            glVertex3d(min_x, max_y, min_z);
            glVertex3d(min_x, min_y, min_z);
            glVertex3d(min_x, min_y, max_z);
            glVertex3d(max_x, max_y, min_z);
            glVertex3d(max_x, max_y, max_z);
            glVertex3d(max_x, min_y, max_z);
            glVertex3d(max_x, min_y, min_z);
            glEnd();
        } else {
            GL11.glBegin(3);

            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x + radius, y, z);
            GL11.glVertex3d(x + radius, y, z + radius);
            GL11.glVertex3d(x, y, z + radius);
            GL11.glVertex3d(x, y, z);

            GL11.glEnd();

            GL11.glBegin(3);

            GL11.glVertex3d(x, y + yOffset, z);
            GL11.glVertex3d(x + radius, y + yOffset, z);
            GL11.glVertex3d(x + radius, y + yOffset, z + radius);
            GL11.glVertex3d(x, y + yOffset, z + radius);
            GL11.glVertex3d(x, y + yOffset, z);

            GL11.glEnd();

            GL11.glBegin(3);

            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y + yOffset, z);

            GL11.glEnd();

            GL11.glBegin(3);

            GL11.glVertex3d(x + radius, y, z);
            GL11.glVertex3d(x + radius, y + yOffset, z);

            GL11.glEnd();

            GL11.glBegin(3);

            GL11.glVertex3d(x + radius, y, z + radius);
            GL11.glVertex3d(x + radius, y + yOffset, z + radius);

            GL11.glEnd();

            GL11.glBegin(3);

            GL11.glVertex3d(x, y, z + radius);
            GL11.glVertex3d(x, y + yOffset, z + radius);

            GL11.glEnd();
        }

        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        if(depth)
            GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.resetColor();
        GL11.glPopMatrix();
    }

    public static void drawBluredRect(double x, double y, double x2, double y2, final int c, final int blue) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.3, 0.3, 1.0);
        final float f = c >> 24 & 0xFF;
        final float f2 = c >> 16 & 0xFF;
        final float f3 = c >> 8 & 0xFF;
        final float f4 = c & 0xFF;
        int a = 100;
        x *= 3.3333333333333335;
        x2 *= 3.3333333333333335;
        y *= 3.3333333333333335;
        y2 *= 3.3333333333333335;
        for (int i = 0; i < blue; ++i) {
            final Color color = new Color((int)f2, (int)f3, (int)f4, a);
            if (a > 100 / blue) {
                a -= 100 / blue;
            }
            Gui.drawRect(x - i, y - i, x2 + i, y2 + i, color.getRGB());
        }
        GlStateManager.popMatrix();
    }

    public static void prepareScissorBox(float x2, float y2, float x22, float y22) {
        ScaledResolution scale = new ScaledResolution(mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x2 * (float)factor), (int)(((float)scale.getScaledHeight() - y22) * (float)factor), (int)((x22 - x2) * (float)factor), (int)((y22 - y2) * (float)factor));
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        GL11.glPushMatrix();
        glEnable(GL_BLEND);
        glDepthMask(false);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(GL_BLEND);
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glPopMatrix();
        GlStateManager.color(1, 1, 1);
    }

    public static void glColor(Color color) {

        GlStateManager.color((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color.getAlpha() / 255F);
    }

    public static void glColor(int color) {

        GlStateManager.color((float) (color >> 16 & 255) / 255F, (float) (color >> 8 & 255) / 255F, (float) (color & 255) / 255F, (float) (color >> 24 & 255) / 255F);
    }
}
