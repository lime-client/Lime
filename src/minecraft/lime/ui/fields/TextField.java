package lime.ui.fields;

import lime.utils.render.fontRenderer.GlyphPageFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class TextField {
    private double x, y, width, height;
    private final GlyphPageFontRenderer fontRenderer;
    private final String name;

    private String text = "";
    private boolean focused, hovered;

    public TextField(GlyphPageFontRenderer fontRenderer, String name, double x, double y, double width, double height) {
        this.fontRenderer = fontRenderer;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        hovered = false;
        focused = false;
    }

    public void drawTextField(int mouseX, int mouseY) {
        hovered = GuiScreen.hover((int) x, (int) y, mouseX, mouseY, (int) width, (int) height);
        Gui.drawRect(x, y + height - 1, x + width, y + height, -1);
        fontRenderer.drawStringWithShadow(getText(), (float) x, (float) y + ((float) height - 8) / 2 - 2, -1);

        if(this.getText().isEmpty() && !isFocused()) {
            fontRenderer.drawStringWithShadow("§7" + this.name, (float) x, (float) y + ((float) height - 8) / 2 - 2, -1);
        }

        if(isFocused() && (System.currentTimeMillis() / 500 % 2 == 0)) {
            fontRenderer.drawStringWithShadow("_", (float) x + fontRenderer.getStringWidth(getText()), (float) y + ((float) height - 8) / 2 - 2, -1);
        }
    }

    public void mouseClicked() {
        this.focused = isHovered();
    }

    public void keyTyped(char key, int keyCode) {
        if(this.isFocused()) {
            if(keyCode == 14) {
                this.text = StringUtils.chop(text);
            } else {
                if(String.valueOf(key).matches("^[a-zA-Z0-9]*$") || key == ' ')
                    this.text += key;
            }
        }
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean isFocused() {
        return focused;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
