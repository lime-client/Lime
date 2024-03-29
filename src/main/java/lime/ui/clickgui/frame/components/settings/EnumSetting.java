package lime.ui.clickgui.frame.components.settings;

import lime.features.setting.Setting;
import lime.features.setting.impl.EnumProperty;
import lime.management.FontManager;
import lime.ui.clickgui.frame.components.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class EnumSetting extends Component {


    public EnumSetting(int x, int y, int width, int height, Setting setting) {
        super(x, y, width, height, setting);
    }

    @Override
    public void onKeyTyped(char typedChar, int key) {

    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        if(GuiScreen.hover(x, y + 4, mouseX, mouseY, width, 16)) {
            Gui.drawRect(x - 3, y + 3, x - 3 + width, y + 19, new Color(25, 25, 25, 150).getRGB());
        }
        FontManager.ProductSans20.getFont().drawString(this.setting.getSettingName(), this.x + 2, this.y + 4f, -1);

        EnumProperty enumValue = (EnumProperty) setting;
        FontManager.ProductSans20.getFont().drawString(enumValue.getSelected().replaceAll("_", " "), this.x + (width - 8) - FontManager.ProductSans20.getFont().getStringWidth(enumValue.getSelected().replaceAll("_", " ")), this.y + 4, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        EnumProperty enumValue = (EnumProperty) setting;

        int enumIndex = 0;
        for(String str : enumValue.getModes()) {
            if(str.equalsIgnoreCase(enumValue.getSelected())) break;
            ++enumIndex;
        }

        if(mouseButton == 1) {
            if(enumIndex - 1 >= 0) {
                enumValue.setSelected(enumValue.getModes()[enumIndex - 1]);
            } else {
                enumValue.setSelected(enumValue.getModes()[enumValue.getModes().length - 1]);
            }
        }

        if(mouseButton == 0) {
            if(enumIndex + 1 < enumValue.getModes().length) {
                enumValue.setSelected(enumValue.getModes()[enumIndex + 1]);
            } else {
                enumValue.setSelected(enumValue.getModes()[0]);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void onGuiClosed() {

    }
}
