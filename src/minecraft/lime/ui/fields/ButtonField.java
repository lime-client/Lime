package lime.ui.fields;

import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import lime.utils.render.fontRenderer.GlyphPageFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class ButtonField {
    private double x, y, width, height;
    private final GlyphPageFontRenderer fontRenderer;
    private final String buttonName;

    private final Color bgColor;
    private boolean hovered;

    private final Animate animation;

    private final IButtonAction iButtonAction;

    public ButtonField(GlyphPageFontRenderer fontRenderer, String buttonName, double x, double y, double width, double height, Color bgColor, IButtonAction iButtonAction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fontRenderer = fontRenderer;
        this.buttonName = buttonName;
        this.bgColor = bgColor;
        this.iButtonAction = iButtonAction;
        this.hovered = false;

        this.animation = new Animate();
        animation.setMin(0);
        animation.setEase(Easing.CUBIC_OUT);
        animation.setMax((float)y);
        animation.setSpeed(225);
    }

    public void drawButton(int mouseX, int mouseY) {
        animation.update();
        hovered = GuiScreen.hover((int) x, (int) animation.getValue(), mouseX, mouseY, (int) width, (int) height);
        Gui.drawRect(x, animation.getValue(), x + width, animation.getValue() + height, isHovered() ? bgColor.darker().getRGB() : bgColor.getRGB());

        fontRenderer.drawStringWithShadow(buttonName, (float)(x + (width / 2) - (fontRenderer.getStringWidth(buttonName) / 2)), (float) (this.animation.getValue() + (this.height - 8) / 2) - 3, -1);
    }

    public void mouseClicked() {
        if(hovered) {
            this.iButtonAction.onClick();
        }
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public interface IButtonAction {
        void onClick();
    }
}
