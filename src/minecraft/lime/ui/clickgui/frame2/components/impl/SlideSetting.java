package lime.ui.clickgui.frame2.components.impl;

import lime.features.setting.SettingValue;
import lime.features.setting.impl.SlideValue;
import lime.management.FontManager;
import lime.ui.clickgui.frame2.components.Component;
import lime.ui.clickgui.frame2.components.FrameModule;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static lime.ui.clickgui.frame2.Priority.*;

public class SlideSetting extends Component {
    public SlideSetting(int x, int y, FrameModule owner, SettingValue setting) {
        super(x, y, owner, setting);
    }

    private boolean drag;

    @Override
    public void initGui() {
        drag = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if(!Mouse.isButtonDown(0)) drag = false;

        SlideValue slide = (SlideValue) getSetting();
        double min = slide.getMin();
        double max = slide.getMax();
        double diff = Math.min(defaultWidth + 5, Math.max(0, mouseX - (this.x)));
        double renderWidth = defaultWidth * (slide.getCurrent() - min) / (max - min);
        Gui.drawRect(x, y, x + (int) renderWidth, y + getOffset(), getDarkerMainColor());

        if(drag)
        {
            if(diff == 0)
                slide.setCurrentValue(min);
            else
            {
                double newValue = roundToPlace((diff / defaultWidth) * (max - min) + min, 2);
                if(newValue <= max)
                    this.setValue(newValue);
            }
        }

        FontManager.ProductSans20.getFont().drawString(getSetting().getSettingName() + ": " + roundToPlace(((SlideValue) getSetting()).getCurrent(), 2), x + 5, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)), stringColor, true);
    }

    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private double snapToStep(double value, double valueStep) {
        if (valueStep > 0.0F)
            value = valueStep * (double) Math.round(value / valueStep);

        return value;
    }

    private void setValue(double value) {
        final SlideValue set = (SlideValue) getSetting();
        set.setCurrentValue(MathHelper.clamp_double(snapToStep(value, set.getIncrement()), set.getMin(), set.getMax()));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return drag = GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && mouseButton == 0;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public int getOffset() {
        return 15;
    }
}
