package lime.ui.clickgui.frame2.components.impl;

import lime.features.setting.SettingValue;
import lime.managers.FontManager;
import lime.ui.clickgui.frame2.components.Component;
import lime.ui.clickgui.frame2.components.FrameModule;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.StringUtils;

import static lime.ui.clickgui.frame2.Priority.*;

public class TextSetting extends Component {
    public TextSetting(int x, int y, FrameModule owner, SettingValue setting) {
        super(x, y, owner, setting);
        text = "";
    }

    private String text;
    private boolean focused;

    @Override
    public void initGui() {
        focused = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {

        return focused = GuiScreen.hover(x + 5, y, mouseX, mouseY, defaultWidth - 10, getOffset());
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(!focused) return;
        if(isAscii(typedChar)) {
            text += typedChar;
        }
        if(keyCode == 14) {
            text = StringUtils.chop(text);
        }
    }

    @Override
    public int getOffset() {
        return ((int) (FontManager.ProductSans20.getFont().getFontHeight() / 2F) + 15);
    }

    private boolean isAscii(char c) {
        return c < 128;
    }
}
