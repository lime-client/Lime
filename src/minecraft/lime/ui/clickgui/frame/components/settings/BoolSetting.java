package lime.ui.clickgui.frame.components.settings;

import lime.features.managers.FontManager;
import lime.features.setting.SettingValue;
import lime.features.setting.impl.BoolValue;
import lime.ui.clickgui.frame.components.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class BoolSetting extends Component {
    public BoolSetting(int x, int y, int width, int height, SettingValue setting) {
        super(x, y, width, height, setting);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        if(GuiScreen.hover(x, y + 4, mouseX, mouseY, 135, 16)) {
            Gui.drawRect(x - 3, y + 3, x - 3 + 135, y + 19, new Color(25, 25, 25, 150).getRGB());
        }
        FontManager.ProductSans20.getFont().drawString(setting.getSettingName(), x + 2, y + 4, -1);
        FontManager.ProductSans20.getFont().drawString(((BoolValue) setting).isEnabled() + "", this.x + 127 - FontManager.ProductSans20.getFont().getStringWidth(((BoolValue) setting).isEnabled() + ""), this.y + 4, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        ((BoolValue) setting).setEnabled(!((BoolValue) setting).isEnabled());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void onGuiClosed() {

    }
}
