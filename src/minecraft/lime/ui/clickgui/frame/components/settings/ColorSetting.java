package lime.ui.clickgui.frame.components.settings;

import lime.managers.FontManager;
import lime.features.setting.SettingValue;
import lime.features.setting.impl.ColorValue;
import lime.ui.clickgui.frame.components.Component;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

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

    private Point colorPoint;
    private Point rgbPoint;

    private Point minecraftColorPoint;
    private Point minecraftRGBPoint;

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        renderColorPicker(x + 2.5f, y + 4, 110, 100, currentRGB.getRGB());

        //GuiScreen.hover(x + 30, y + 4, mouseX, mouseY, 100, 100)
        if(GuiScreen.hover(x + 2, y + 114, mouseX, mouseY, 110, 15) && Mouse.isButtonDown(0)) {
            rgbPoint = MouseInfo.getPointerInfo().getLocation();
            minecraftRGBPoint = new Point(mouseX, mouseY);
            this.currentRGB = this.robot.getPixelColor(this.rgbPoint.x, this.rgbPoint.y);
        }

        if(GuiScreen.hover(x + 2, y + 4, mouseX, mouseY, 110, 100) && Mouse.isButtonDown(0)) {
            colorPoint = MouseInfo.getPointerInfo().getLocation();
            minecraftColorPoint = new Point(mouseX, mouseY);
            this.color = this.robot.getPixelColor(this.colorPoint.x, this.colorPoint.y);
            ((ColorValue) setting).setColor(color.getRGB());
        }

        if(rgbPoint != null) {
            RenderUtils.drawCircle(minecraftRGBPoint.x, y + 114, 3, 83886080, Color.black.getRGB());
        }
        if(colorPoint != null) {
            RenderUtils.drawCircle(minecraftColorPoint.x, minecraftColorPoint.y, 3, 83886080, Color.black.getRGB());
        }

        if(GuiScreen.hover(x + 2, y + 4, mouseX, mouseY, 110, 125)) {
            Gui.drawRect(mouseX + 5, mouseY - 3, mouseX + 9 + FontManager.ProductSans20.getFont().getStringWidth(this.setting.getSettingName()), mouseY - 3 + FontManager.ProductSans20.getFont().getFontHeight(), new Color(25, 25, 25).getRGB());
            FontManager.ProductSans20.getFont().drawString(this.setting.getSettingName(), mouseX + 6, mouseY - 3, -1);
        }

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
        Color[][] arrayOfColor = {{Color.DARK_GRAY, Color.MAGENTA}, {Color.MAGENTA, Color.BLUE}, {Color.BLUE, Color.GREEN}, {Color.GREEN, Color.RED}, {Color.RED, Color.ORANGE}, {Color.ORANGE, Color.YELLOW}};
        float f = width / arrayOfColor.length;
        for (int i = 0; i < arrayOfColor.length; i++) {
            drawGradientRect(x + i * f, y + height + 10.0F, f, 15.0F, arrayOfColor[i][0].getRGB(), arrayOfColor[i][1].getRGB());
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
