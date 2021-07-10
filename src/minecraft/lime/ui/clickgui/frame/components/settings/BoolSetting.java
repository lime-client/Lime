package lime.ui.clickgui.frame.components.settings;

import lime.managers.FontManager;
import lime.features.setting.SettingValue;
import lime.features.setting.impl.BoolValue;
import lime.ui.clickgui.frame.components.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BoolSetting extends Component {
    public BoolSetting(int x, int y, int width, int height, SettingValue setting) {
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
        FontManager.ProductSans20.getFont().drawString(setting.getSettingName(), x + 2, y + 4, -1);
        //FontManager.ProductSans20.getFont().drawString(((BoolValue) setting).isEnabled() + "", this.x + 127 - FontManager.ProductSans20.getFont().getStringWidth(((BoolValue) setting).isEnabled() + ""), this.y + 4, -1);
        Gui.drawRect(this.x + width - 15, this.y + 6, this.x + width - 5, this.y + 16, new Color(25, 25, 25).getRGB());
        if(((BoolValue) setting).isEnabled()) {
            GL11.glPushMatrix();
            GlStateManager.enableTexture2D();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glLineWidth(0.25f);
            GL11.glBegin(3);
            GL11.glVertex2f(this.x + width - 14, this.y + 7);
            GL11.glVertex2f(this.x + width - 6, this.y + 15);
            GL11.glEnd();
            GL11.glBegin(3);
            GL11.glVertex2f(this.x + width - 14, this.y + 15);
            GL11.glVertex2f(this.x + width - 6, this.y + 7);
            GL11.glEnd();
            GL11.glPopMatrix();
        }
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
