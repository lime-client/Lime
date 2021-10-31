package lime.ui.clickgui.frame2.components.impl;

import lime.features.setting.Setting;
import lime.features.setting.impl.BooleanProperty;
import lime.management.FontManager;
import lime.ui.clickgui.frame2.components.Component;
import lime.ui.clickgui.frame2.components.FrameModule;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static lime.ui.clickgui.frame2.Priority.*;

public class BoolSetting extends Component {

    public BoolSetting(int x, int y, FrameModule owner, Setting setting)
    {
        super(x, y, owner, setting);
    }

    @Override
    public void initGui()
    {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        FontManager.ProductSans20.getFont().drawString(getSetting().getSettingName(), x + 5, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)), -1, true);
        //Gui.drawRect(x + defaultWidth - 15, y, x + defaultWidth - 5, y + 10, darkerMainColor);
        //RenderUtils.drawFilledCircle(x + defaultWidth - 10, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)) + 6.75f, 5, new Color(getDarkerMainColor()));

        /*if(((BooleanProperty) getSetting()).isEnabled() || animation.getValue() != 0)
        {
            RenderUtils.drawFilledCircle(x + defaultWidth - 10, y + (getOffset() / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)) + 6.75f, animation.getValue(), new Color(getEnabledColor()).brighter().brighter());
            GlStateManager.resetColor();
            GL11.glColor4f(1, 1, 1, 1);
        }*/

        Gui.drawRect(this.x + defaultWidth - 15, this.y+3, this.x + defaultWidth - 5, this.y + 13, new Color(25, 25, 25).getRGB());
        if(((BooleanProperty) getSetting()).isEnabled()) {
            GL11.glPushMatrix();
            GlStateManager.enableTexture2D();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glLineWidth(0.5f);
            GL11.glBegin(3);
            GL11.glVertex2f(this.x + defaultWidth - 14, this.y + 4);
            GL11.glVertex2f(this.x + defaultWidth - 6, this.y + 13);
            GL11.glEnd();
            GL11.glBegin(3);
            GL11.glVertex2f(this.x + defaultWidth - 14, this.y + 13);
            GL11.glVertex2f(this.x + defaultWidth - 6, this.y + 4);
            GL11.glEnd();
            GL11.glPopMatrix();
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, getOffset())) {
            BooleanProperty set = (BooleanProperty) getSetting();
            set.setEnabled(!set.isEnabled());
            return true;
        }
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton)
    {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public int getOffset()
    {
        return 15;
    }
}
