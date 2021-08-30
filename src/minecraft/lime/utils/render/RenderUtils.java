package lime.utils.render;

import lime.utils.IUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
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

        public static void drawImage(ResourceLocation resourceLocation, double x, double y, int width, int height, boolean antialiasing) {
            GL11.glPushMatrix();
            glEnable(GL_BLEND);
            glDepthMask(false);
            GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(resourceLocation);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
            GL11.glDepthMask(true);
            GL11.glDisable(GL_BLEND);
            GL11.glEnable(GL_DEPTH_TEST);
            GL11.glPopMatrix();
            GlStateManager.color(1, 1, 1);
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

    public static void drawCircle(float x, float y, float radius, int inside, int outside) {
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

    public static void drawLine(float x, float y, float x2, float y2, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_LINES);
        glColor(color);
        glVertex2f(x, y);
        glVertex2f(x2, y2);
        glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
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

    public static void drawFace(int x, int y, int width, int height, AbstractClientPlayer target) {
        try {
            ResourceLocation skin = target.getLocationSkin();
            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
            //355, 190, 8.0f, 8.0f, 8, 8, 28, 28, 64.0f, 64.0f
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawFace(int x, int y, int width, int height, ResourceLocation resourceLocation) {
        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
            //355, 190, 8.0f, 8.0f, 8, 8, 28, 28, 64.0f, 64.0f
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawHorizontalLine(float x, float y, float x1, float thickness, int color) {
        Gui.drawRect(x, y, x1, y + thickness, color);
    }

    public static void drawVerticalLine(float x, float y, float y1, float thickness, int color) {
        Gui.drawRect(x, y, x + thickness, y1, color);
    }

    public static void drawHollowBox(float x, float y, float x1, float y1, float thickness, int color) {
        /* Top */
        drawHorizontalLine(x, y, x1, thickness, color);
        /* Bottom */
        drawHorizontalLine(x, y1, x1, thickness, color);
        /* Left */
        drawVerticalLine(x, y, y1, thickness, color);
        /* Right */
        drawVerticalLine(x1 - thickness, y, y1, thickness, color);
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
        double max_x = x + radius;
        double max_y = y + yOffset;
        double max_z = z + radius;

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

    public static void drawFilledCircle(final float xx, final float yy, final float radius, final Color color) {
        int sections = 50;
        double dAngle = 2 * Math.PI / sections;
        float x, y;

        glPushAttrib(GL_ENABLE_BIT);

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < sections; i++) {
            x = (float) (radius * Math.sin((i * dAngle)));
            y = (float) (radius * Math.cos((i * dAngle)));

            glColor(color);
            glVertex2f(xx + x, yy + y);
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        glEnd();

        glPopAttrib();
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
