package lime.ui.clickgui.frame.components.settings;

import lime.managers.FontManager;
import lime.features.setting.SettingValue;
import lime.features.setting.impl.ColorValue;
import lime.ui.clickgui.frame.components.Component;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.IntBuffer;

public class ColorSetting extends Component {

    public ColorSetting(int x, int y, int width, int height, int color, SettingValue settingValue) {
        super(x, y, width, height, settingValue);
        this.currentRGB = new Color(color);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private Robot robot;
    private Color currentRGB;
    private Color color;

    private Point minecraftColorPoint;
    private Point minecraftRGBPoint;

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        renderColorPicker(x + 2.5f, y + 4, 110, 100, currentRGB.getRGB());

        //GuiScreen.hover(x + 30, y + 4, mouseX, mouseY, 100, 100)
        if(GuiScreen.hover(x + 2, y + 114, mouseX, mouseY, 110, 15) && Mouse.isButtonDown(0)) {
            minecraftRGBPoint = new Point(mouseX, mouseY);
            this.currentRGB = new Color(getColorUnderMouse());
        }

        if(GuiScreen.hover(x + 2, y + 4, mouseX, mouseY, 110, 100) && Mouse.isButtonDown(0)) {
            minecraftColorPoint = new Point(mouseX, mouseY);
            this.color = new Color(getColorUnderMouse());
            ((ColorValue) setting).setColor(color.getRGB());
        }

        if(minecraftRGBPoint != null) {
            RenderUtils.drawCircle(minecraftRGBPoint.x, y + 114, 3, 83886080, Color.black.getRGB());
        }
        if(minecraftColorPoint != null) {
            RenderUtils.drawCircle(minecraftColorPoint.x, minecraftColorPoint.y, 3, 83886080, Color.black.getRGB());
        }

        if(GuiScreen.hover(x + 2, y + 4, mouseX, mouseY, 110, 125)) {
            Gui.drawRect(mouseX + 5, mouseY - 3, mouseX + 9 + FontManager.ProductSans20.getFont().getStringWidth(this.setting.getSettingName()), mouseY - 3 + FontManager.ProductSans20.getFont().getFontHeight(), new Color(25, 25, 25).getRGB());
            FontManager.ProductSans20.getFont().drawString(this.setting.getSettingName(), mouseX + 6, mouseY - 3, -1);
        }

    }

    public int getColorUnderMouse() {
        final IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
        final int[] ints = { 0 };
        GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, 32993, 33639, intbuffer);
        intbuffer.get(ints);
        return ints[0];
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void onGuiClosed() {

    }

    @Override
    public void onKeyTyped(char typedChar, int key) {

    }

    public final void renderColorPicker(float x, float y, float width, float height, int currentRGB) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableCull();
        GL11.glBegin(7);
        RenderUtils.glColor(Color.WHITE.getRGB());
        GL11.glVertex2f(x, y);
        RenderUtils.glColor(currentRGB);
        GL11.glVertex2f(x + width, y);
        RenderUtils.glColor(Color.BLACK.getRGB());
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        Color[][] a = {{Color.RED, Color.ORANGE}, {Color.ORANGE, Color.YELLOW}, {Color.YELLOW, Color.GREEN}, {Color.GREEN, Color.CYAN}, {Color.CYAN, Color.BLUE}, {Color.BLUE, Color.MAGENTA}, {Color.MAGENTA, Color.PINK}, {Color.PINK, Color.RED}};
        Color[][] arrayOfColor = {{Color.DARK_GRAY, Color.MAGENTA}, {Color.MAGENTA, Color.BLUE}, {Color.BLUE, Color.GREEN}, {Color.GREEN, Color.RED}, {Color.RED, Color.ORANGE}, {Color.ORANGE, Color.YELLOW}};
        float f = width / a.length;
        for (int i = 0; i < a.length; i++) {
            drawGradientRect(x + i * f, y + height + 10.0F, f, 15.0F, a[i][0].getRGB(), a[i][1].getRGB());
        }
    }

    private void drawGradientRect(float x, float y, float width, float height, int color, int color1) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableCull();
        GL11.glBegin(7);
        RenderUtils.glColor(color);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        RenderUtils.glColor(color1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
