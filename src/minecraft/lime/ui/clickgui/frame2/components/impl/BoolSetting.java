package lime.ui.clickgui.frame2.components.impl;

import lime.features.setting.SettingValue;
import lime.features.setting.impl.BoolValue;
import lime.managers.FontManager;
import lime.ui.clickgui.frame2.Priority;
import lime.ui.clickgui.frame2.components.Component;
import lime.ui.clickgui.frame2.components.FrameModule;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BoolSetting extends Component implements Priority {
    private final Animate animation;

    public BoolSetting(int x, int y, FrameModule owner, SettingValue setting)
    {
        super(x, y, owner, setting);
        this.animation = new Animate().setMin(0).setMax(5).setSpeed(15).setEase(Easing.LINEAR).setReversed(!((BoolValue) setting).isEnabled());
    }

    @Override
    public void initGui()
    {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        animation.update();
        FontManager.ProductSans20.getFont().drawString(getSetting().getSettingName(), x + 5, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)), -1, true);
        //Gui.drawRect(x + defaultWidth - 15, y, x + defaultWidth - 5, y + 10, darkerMainColor);
        RenderUtils.drawFilledCircle(x + defaultWidth - 10, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)) + 6.75f, 5, new Color(darkerMainColor));

        if(((BoolValue) getSetting()).isEnabled() || animation.getValue() != 0)
        {
            RenderUtils.drawFilledCircle(x + defaultWidth - 10, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)) + 6.75f, animation.getValue(), new Color(enabledColor));
            GlStateManager.resetColor();
            GL11.glColor4f(1, 1, 1, 1);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, getOffset())) {
            BoolValue set = (BoolValue) getSetting();
            set.setEnabled(!set.isEnabled());
            animation.setReversed(!set.isEnabled());
            return true;
        }
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton)
    {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public int getOffset()
    {
        return 15;
    }
}
