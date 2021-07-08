package lime.ui.clickgui.frame.components.settings;

import lime.features.managers.FontManager;
import lime.features.setting.SettingValue;
import lime.features.setting.impl.TextValue;
import lime.ui.clickgui.frame.components.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class TextSetting extends Component {

    public TextSetting(int x, int y, int width, int height, String text, SettingValue setting) {
        super(x, y, width, height, setting);
        this.text = text;
    }
    private String text;
    private boolean hovered;
    public boolean isFocused;


    @Override
    public void drawComponent(int mouseX, int mouseY) {
        hovered = (GuiScreen.hover(x - 3, y + 4, mouseX, mouseY, width, 16));

        if(!hovered && Mouse.isButtonDown(0))
            isFocused = false;
        else if(hovered && Mouse.isButtonDown(0))
            isFocused = true;

        Gui.drawRect(x, y + 16, x + width - 6, y + 17, -1);
        FontManager.ProductSans20.getFont().drawString(text + (System.currentTimeMillis() / 500 % 2 == 0 && isFocused ? "_" : ""), this.x + 1, this.y + 4f, -1);

        if(isFocused) {
            Gui.drawRect(mouseX + 5, mouseY - 3, mouseX + 9 + FontManager.ProductSans20.getFont().getStringWidth(this.setting.getSettingName()), mouseY - 3 + FontManager.ProductSans20.getFont().getFontHeight(), new Color(25, 25, 25).getRGB());
            FontManager.ProductSans20.getFont().drawString(this.setting.getSettingName(), mouseX + 6, mouseY - 3, -1);
        } else if(text.isEmpty() && !isFocused) {
            FontManager.ProductSans20.getFont().drawString(this.setting.getSettingName(), this.x + 1, this.y + 4f, -1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void onKeyTyped(char typedChar, int key) {
        if(text == null)
            text = "";
        if(isFocused) {
            if(key == 14) {
                this.text = StringUtils.chop(text);
            } else {
                if(String.valueOf(typedChar).matches("^[a-zA-Z0-9]*$") || typedChar == ' ' || typedChar == '§')
                    this.text += typedChar;
            }
            ((TextValue) setting).setText(text);
        }
    }

    @Override
    public void onGuiClosed() {
        isFocused = false;
        hovered = false;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }
}
