package lime.ui.clickgui.frame2.components.impl;

import lime.features.setting.Setting;
import lime.features.setting.impl.EnumProperty;
import lime.management.FontManager;
import lime.ui.clickgui.frame2.components.Component;
import lime.ui.clickgui.frame2.components.FrameModule;
import net.minecraft.client.gui.GuiScreen;

import static lime.ui.clickgui.frame2.Priority.defaultWidth;

public class EnumSetting extends Component {
    public EnumSetting(int x, int y, FrameModule owner, Setting setting) {
        super(x, y, owner, setting);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontManager.ProductSans20.getFont().drawString(getSetting().getSettingName(), x + 5, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)), -1, true);
        FontManager.ProductSans20.getFont().drawString(((EnumProperty) getSetting()).getSelected().toUpperCase(), x + defaultWidth - FontManager.ProductSans20.getFont().getStringWidth(((EnumProperty) getSetting()).getSelected().toUpperCase()) - 5, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)), -1, true);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()))
        {
            EnumProperty enumValue = (EnumProperty) getSetting();

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
            return true;
        }
        return false;
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
