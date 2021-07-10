package lime.ui.clickgui.frame.components.settings;

import lime.managers.FontManager;
import lime.features.setting.SettingValue;
import lime.features.setting.impl.SlideValue;
import lime.ui.clickgui.frame.components.Component;
import lime.utils.other.MathUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class SlideSetting extends Component {
    public SlideSetting(int x, int y, int width, int height, SettingValue setting) {
        super(x, y, width, height, setting);
    }
    private boolean dragging;

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        if(GuiScreen.hover(x - 3, y + 4, mouseX, mouseY, width, 16))
            Gui.drawRect(x - 3, y + 3, x - 3 + width, y + 19, new Color(25, 25, 25, 150).getRGB());
        else
            dragging = false;

        SlideValue slide = (SlideValue) setting;
        double diff = Math.min(width + 5, Math.max(0, mouseX - (this.x - 3)));
        double min = slide.getMin();
        double max = slide.getMax();
        double renderWidth = width * (slide.getCurrent() - min) / (max - min);
        Gui.drawRect(x - 3, y + 3, x - 3 + renderWidth, y + 19, new Color(25, 25, 25, 200).getRGB());
        if(dragging) {
            if(diff == 0) {
                slide.setCurrentValue(min);
            } else {
                double newValue = MathUtils.roundToPlace(((diff / width) * (max - min) + min), 2);
                if(newValue <= max){
                    this.setValue(newValue);
                }
            }
        }
        FontManager.ProductSans20.getFont().drawString(slide.getSettingName() + ": " + MathUtils.roundToPlace(slide.getCurrent(), 2),this.x + 2, this.y + 4f, -1);
    }

    private double snapToStep(double value, double valueStep) {
        if (valueStep > 0.0F)
            value = valueStep * (double) Math.round(value / valueStep);

        return value;
    }

    private void setValue(double value) {
        final SlideValue set = (SlideValue) setting;
        set.setCurrentValue(MathHelper.clamp_double(snapToStep(value, set.getIncrement()), set.getMin(), set.getMax()));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        dragging = GuiScreen.hover(x, y + 4, mouseX, mouseY, width, 16) && mouseButton == 0;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
    }

    @Override
    public void onGuiClosed() {

    }

    @Override
    public void onKeyTyped(char typedChar, int key) {

    }
}
