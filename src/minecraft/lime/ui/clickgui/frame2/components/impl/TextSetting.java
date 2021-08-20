package lime.ui.clickgui.frame2.components.impl;

import lime.features.setting.SettingValue;
import lime.features.setting.impl.TextValue;
import lime.managers.FontManager;
import lime.ui.clickgui.frame2.components.Component;
import lime.ui.clickgui.frame2.components.FrameModule;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

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
        GL11.glPushMatrix();
        GL11.glScalef(0.9f, 0.9f, 0);
        FontManager.ProductSans18.getFont().drawStringWithShadow(getSetting().getSettingName(), (x + 5) / 0.9f, y / 0.9F, -1);
        GL11.glPopMatrix();

        Gui.drawRect(x + 6, y + 18, x + defaultWidth - 3, y + 19, -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow(text + (focused ? System.currentTimeMillis() / 500 % 2 == 0 ? "_" : "" : ""), (x + 5), y + 6, -1);

        ((TextValue) getSetting()).setText(text);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0) {
            focused = GuiScreen.hover(x + 5, y, mouseX, mouseY, defaultWidth - 3, getOffset());
        }
        return focused;
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
        return 20;
    }

    private boolean isAscii(char c) {
        char[] allowedChars = "abcdefghijklmnopqrstuvwxyz1234567890&~#'{([-|`_\\ç^@)]=},;:!?./§".toCharArray();
        for (char allowedChar : allowedChars) {
            if(c == allowedChar)
                return true;
        }
        return false;
    }
}
