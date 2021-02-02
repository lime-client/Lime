package lime.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import static org.lwjgl.opengl.GL11.*;

public class Util2D {
    protected static Minecraft mc = Minecraft.getMinecraft();
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
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    private static int lastMouseX = 0;
    private static int lastMouseY = 0;

    public static void DrawRect(double x, double y, double width, double height, int color) {
        drawRect(x, y, x + width, y + height, color);
    }

    public static void drawRectRoundRect(float x, float y, float width, float height,final int color1, final int color2) {
        drawRoundedRect(x, y, x + width, y + height, color1, color2);
    }

    public static void DrawTriangle(int x, int y, int size, int color) {
        GL11.glColor4f(1F, 1F, 1F, 1F);

        //START 2D
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);

        //HEX Color
        float alpha = (float)(color >> 24 & 255) / 255.0f;
        float red = (float)(color >> 16 & 255) / 255.0f;
        float green = (float)(color >> 8 & 255) / 255.0f;
        float blue = (float)(color & 255) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);

        //Draw Triangle By LaVache
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(2);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(x, y + size / 2);
        GL11.glVertex2f(x + size, y + size);
        GL11.glVertex2f(x + size, y);
        GL11.glEnd();

        //END 2D
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);

        GL11.glColor4f(1F, 1F, 1F, 1F);

    }

    public static void DrawCroix(int x, int y, int size, int color) {
        GL11.glColor4f(1F, 1F, 1F, 1F);

        //START 2D
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);

        //HEX Color
        float alpha = (float)(color >> 24 & 255) / 255.0f;
        float red = (float)(color >> 16 & 255) / 255.0f;
        float green = (float)(color >> 8 & 255) / 255.0f;
        float blue = (float)(color & 255) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);

        //Draw Croix By LaVache
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(2);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x, y + size / 5 - 1);
        GL11.glVertex2f(x + size, y + size);
        GL11.glVertex2f(x, y + size);
        GL11.glVertex2f(x + size, y + size / 5 - 1);
        GL11.glEnd();

        //END 2D
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);

        GL11.glColor4f(1F, 1F, 1F, 1F);

    }

    public static void drawFullCircle(double x, int y, double width, double height, int c) {
        float f = (float)(c >> 24 & 255) / 255.0f;
        float f1 = (float)(c >> 16 & 255) / 255.0f;
        float f2 = (float)(c >> 8 & 255) / 255.0f;
        float f3 = (float)(c & 255) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glBegin((int)2);
        int i = 0;
        while (i <= 360) {
            double x2 = Math.sin((double)i * 3.141592653589793 / 180.0) * width;
            double y2 = Math.cos((double)i * 3.141592653589793 / 180.0) * height;
            GL11.glVertex2d((double)((double)x + x2), (double)((double)y + y2));
            ++i;
        }
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    public static void drawdCircle(double x, int y, double width, double height, int c) {
        float f = (float)(c >> 24 & 255) / 255.0f;
        float f1 = (float)(c >> 16 & 255) / 255.0f;
        float f2 = (float)(c >> 8 & 255) / 255.0f;
        float f3 = (float)(c & 255) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glBegin((int)6);
        int i = 0;
        while (i <= 360) {
            double x2 = Math.sin((double)i * 3.141592653589793 / 180.0) * width;
            double y2 = Math.cos((double)i * 3.141592653589793 / 180.0) * height;
            GL11.glVertex2d((double)((double)x + x2), (double)((double)y + y2));
            ++i;
        }
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    public static void enableGL2D() {
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
    }

    public static void disableGL2D() {
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
    }

    public static void drawIcon(int x, int y, int width, int height, ResourceLocation image) {
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
    }

    public static void drawGradientRect(final double x, final double y, final double x2, final double y2, final int col1, final int col2) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        glColor(col1);
        GL11.glVertex2d(x + x2, y);
        GL11.glVertex2d(x, y);
        glColor(col2);
        GL11.glVertex2d(x, y + y2);
        GL11.glVertex2d(x + x2, y + y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void DrawGradiant(final double x, final double y, final double x2, final double y2, final int col1, final int col2) {
        drawGradientRect(x, y, x + x2, y + y2, col1, col2);
    }

    //drawGradientRect(x, x1, y + 1.0f, x1 + 1.0f, y1, y2);

    public static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 255) / 255.0f;
        float red = (float)(hex >> 16 & 255) / 255.0f;
        float green = (float)(hex >> 8 & 255) / 255.0f;
        float blue = (float)(hex & 255) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void drawLineH(float x, float y, final float width, final int height) {
        drawHLine(x, y, x + width, (int) (y + height));
    }

    public static void drawHLine(float x, float y, final float x1, final int y1) {
        if (y < x) {
            final float var5 = x;
            x = y;
            y = var5;
        }
        drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
    }

    public static void drawVLine(final float x, float y, float x1, final int y1) {
        if (x1 < y) {
            final float var5 = y;
            y = x1;
            x1 = var5;
        }
        drawRect(x, y + 1.0f, x + 1.0f, x1, y1);
    }

    public static void drawRoundedRect(float x, float y, float x1, float y1, final int borderC, final int insideC) {
        enableGL2D();
        x *= 2.0f;
        y *= 2.0f;
        x1 *= 2.0f;
        y1 *= 2.0f;
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawVLine(x, y + 1.0f, y1 - 2.0f, borderC);
        drawVLine(x1 - 1.0f, y + 1.0f, y1 - 2.0f, borderC);
        drawHLine(x + 2.0f, x1 - 3.0f, y, borderC);
        drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
        drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
        drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC);
        drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        disableGL2D();
    }

    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        final int factor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
    }
    public static void drawRoundedRect(final float x, final float y, final float width, final float height,
                                       final float radius, final int color) {
        float x2 = x + ((radius / 2f) + 0.5f);
        float y2 = y + ((radius / 2f) + 0.5f);
        float calcWidth = (width - ((radius / 2f) + 0.5f));
        float calcHeight = (height - ((radius / 2f) + 0.5f));
        // top (pink)
        relativeRect(x2 + radius / 2f, y2 - radius / 2f - 0.5f, x2 + calcWidth - radius / 2f, y + calcHeight - radius / 2f,
                color);
        // bottom (yellow)
        relativeRect(x2 + radius / 2f, y2, x2 + calcWidth - radius / 2f, y2 + calcHeight + radius / 2f + 0.5f, color);
        // left (red)
        relativeRect((x2 - radius / 2f - 0.5f), y2 + radius / 2f, x2 + calcWidth, y2 + calcHeight - radius / 2f, color);
        // right (green)
        relativeRect(x2, y2 + radius / 2f + 0.5f, x2 + calcWidth + radius / 2f + 0.5f, y2 + calcHeight - radius / 2f,
                color);

        // left top circle
        polygonCircle(x, y - 0.15, radius * 2, 360, color);
        // right top circle
        polygonCircle(x + calcWidth - radius + 1.0, y - 0.15, radius * 2, 360, color);
        // left bottom circle
        polygonCircle(x, y + calcHeight - radius + 1, radius * 2, 360, color);
        // right bottom circle
        polygonCircle(x + calcWidth - radius + 1, y + calcHeight - radius + 1, radius * 2, 360, color);
    }
    public static void relativeRect(final float left, final float top, final float right, final float bottom,
                                    final int color) {

        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        glColor(color);

        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0).endVertex();
        worldRenderer.pos(right, bottom, 0).endVertex();
        worldRenderer.pos(right, top, 0).endVertex();
        worldRenderer.pos(left, top, 0).endVertex();

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static final void polygonCircle(final double x, final double y, double sideLength, final double degree,
                                           final int color) {
        sideLength *= 0.5;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GlStateManager.disableAlpha();

        glColor(color);

        GL11.glLineWidth(1);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        // since its filled, otherwise GL_LINE_STRIP
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        for (double i = 0; i <= degree; i++) {
            final double angle = i * (Math.PI * 2) / degree;

            GL11.glVertex2d(x + (sideLength * Math.cos(angle)) + sideLength,
                    y + (sideLength * Math.sin(angle)) + sideLength);
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GlStateManager.enableAlpha();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D); // azy
        GL11.glDisable(GL11.GL_BLEND);
    }


}
