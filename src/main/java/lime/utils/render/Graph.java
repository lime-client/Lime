package lime.utils.render;

import lime.utils.other.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Graph {
    private final String name;
    private final ArrayList<Double> datas;
    public Graph(String name) {
        this.name = name;
        datas = new ArrayList<>();
    }
    public void drawGraph(float x, float y, float width, float height) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        double max = 0;
        double average = 0;
        if (datas.size() > width) datas.remove(0);
        for (double data : datas) {
            if (data > max) max = MathUtils.snapToStep(data, 0.01);
            average += Math.min(data, height);
        }
        average = MathUtils.snapToStep(average/datas.size(), 0.01);
        GL11.glLineWidth(2);
        RenderUtils.drawLine(x, y, x + width, y, new Color(0xAAAAAA));
        RenderUtils.drawLine(x, y, x, y + height, new Color(0xAAAAAA));
        RenderUtils.drawLine(x + width, y, x + width, y + height, new Color(0xAAAAAA));
        RenderUtils.drawLine(x, y + height, x + width, y + height, new Color(0xAAAAAA));
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.prepareScissorBox(x, y, x + width, y + height);
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2);
        RenderUtils.glColor(-1);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        int i = 0;
        for (double data : datas) {
            GL11.glVertex2d(x + i, y + height - Math.min(data, height));
            ++i;
        }
        GL11.glEnd();
        RenderUtils.drawLine(x, y + height - (float) max, x + width, y + height - (float) max, Color.RED);
        RenderUtils.drawLine(x, y + height - (float) average, x + width, y + height - (float) average, Color.GREEN);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.color(1,1,1);
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        fr.drawStringWithShadow(name, x + 4, y + 4, -1);
        fr.drawStringWithShadow(average + " avg", x + width - 4 - fr.getStringWidth(average + " avg"), y + 4, -1);
        fr.drawStringWithShadow(max + " max", x + width - 4 - fr.getStringWidth(max + " max"), y + 14, -1);
    }
    public void update(double data) { datas.add(data); }
    public void clear() { datas.clear(); }
    public ArrayList<Double> getDatas() { return datas; }
    public String getName() { return name; }
}