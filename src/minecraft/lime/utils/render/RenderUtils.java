package lime.utils.render;

import lime.core.Lime;
import lime.features.module.impl.movement.TargetStrafe;
import lime.utils.IUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
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

    // From moon
    public static void drawRadius(EntityLivingBase target, int color) {
        TargetStrafe targetStrafe = (TargetStrafe) Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
        float rangeValue = (float) targetStrafe.distance.getCurrent();
        GL11.glPushMatrix();
        GL11.glTranslated(target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glLineWidth(3);
        RenderUtils.glColor(color);
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i <= 360; i += 60) { // You can change circle accuracy  (60 - accuracy)
            GL11.glVertex2f((float)Math.cos(i * Math.PI / 180) * rangeValue, (float)(Math.sin(i * Math.PI / 180) * rangeValue));
        }

        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
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
            glBegin(GL_QUADS); // start drawing a quad
            glVertex3d(max_x, max_y, min_z); // top right
            glVertex3d(min_x, max_y, min_z); // top left
            glVertex3d(min_x, max_y, max_z); // bottom left
            glVertex3d(max_x, max_y, max_z); // bottom right
            glVertex3d(max_x, min_y, max_z); // top right
            glVertex3d(min_x, min_y, max_z); // top left
            glVertex3d(min_x, min_y, min_z); // bottom left
            glVertex3d(max_x, min_y, min_z); // bottom right
            glVertex3d(max_x, max_y, max_z); // top right
            glVertex3d(min_x, max_y, max_z); // top left
            glVertex3d(min_x, min_y, max_z); // bottom left
            glVertex3d(max_x, min_y, max_z); // bottom right
            glVertex3d(max_x, min_y, min_z); // top right
            glVertex3d(min_x, min_y, min_z); // top left
            glVertex3d(min_x, max_y, min_z); // bottom left
            glVertex3d(max_x, max_y, min_z); // bottom right
            glVertex3d(min_x, max_y, max_z); // top right
            glVertex3d(min_x, max_y, min_z); // top left
            glVertex3d(min_x, min_y, min_z); // bottom left
            glVertex3d(min_x, min_y, max_z); // bottom right
            glVertex3d(max_x, max_y, min_z); // top right
            glVertex3d(max_x, max_y, max_z); // top left
            glVertex3d(max_x, min_y, max_z); // bottom left
            glVertex3d(max_x, min_y, min_z); // bottom right
            glEnd(); // finished drawing the quad
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
        GL11.glPopMatrix();
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
