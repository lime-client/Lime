package lime.ui.clickgui.frame2.components;

import lime.core.Lime;
import lime.features.module.Module;
import lime.features.setting.impl.*;
import lime.managers.FontManager;
import lime.ui.clickgui.frame2.Priority;
import lime.ui.clickgui.frame2.components.impl.*;
import lime.utils.render.ColorUtils;
import lime.utils.render.animation.easings.*;
import net.minecraft.client.gui.GuiScreen;

import java.awt.Color;
import java.util.ArrayList;

public class FrameModule implements Priority {
    private final Module module;
    private final ArrayList<Component> components;

    private final FrameCategory owner;

    private final Animate moduleAnimation;

    private int x, y;
    private int offset;

    private boolean opened;

    public FrameModule(Module module, FrameCategory owner, int x, int y)
    {
        this.module = module;
        this.components = new ArrayList<>();
        this.owner = owner;
        this.moduleAnimation = new Animate();
        moduleAnimation.setMin(0).setMax(255).setReversed(!module.isToggled()).setEase(Easing.LINEAR);
        this.opened = false;

        this.x = x;
        this.y = y;

        if(module.hasSettings())
        {
            Lime.getInstance().getSettingsManager().getSettingsFromModule(module).forEach(setting ->
            {
                if(setting instanceof BoolValue)
                {
                    this.components.add(new BoolSetting(0, 0, this, setting));
                }
                if(setting instanceof EnumValue)
                {
                    this.components.add(new EnumSetting(0, 0, this, setting));
                }
                if(setting instanceof SlideValue)
                {
                    this.components.add(new SlideSetting(0, 0, this, setting));
                }
            });
        }
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        moduleAnimation.setReversed(!module.isToggled());
        moduleAnimation.setSpeed(1000).update();

        if(GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && hoveredColor) {
            GuiScreen.drawRect(x,y, x + defaultWidth, y + moduleHeight, darkerMainColor);
        }

        if(module.isToggled() || (moduleAnimation.isReversed() && moduleAnimation.getValue() != 0)) {
            GuiScreen.drawRect(x,y, x + defaultWidth, y + moduleHeight, ColorUtils.setAlpha(new Color(enabledColor), (int) moduleAnimation.getValue()).getRGB());
        }

        FontManager.ProductSans20.getFont().drawString(module.getName(), x+3, y + (moduleHeight / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)), stringColor, true);

        int offset = 0;

        if(opened) {
            for (Component component : this.components) { // using for loop because continue isn't supported on foreach
                component.getSetting().constantCheck();
                if(component.getSetting().isHide()) continue;

                component.setX(x);
                component.setY(y + moduleHeight + offset);

                component.drawScreen(mouseX, mouseY);

                offset += component.getOffset();
            }
        }

        this.setOffset(moduleHeight + offset);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && GuiScreen.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight()))
        {
            switch(mouseButton)
            {
                case 0:
                    module.toggle();
                    break;
                case 1:
                    opened = !opened;
                    break;
                case 2:
                    //TODO: Bind
                    break;
            }
            return true;
        }

        if(GuiScreen.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight()) && opened) {
            for (Component component : this.components) {
                if(!component.getSetting().isHide() && component.mouseClicked(mouseX, mouseY, mouseButton))
                    return true;
            }
        }

        return false;
    }

    public int getOffset() {
        offset = 0;
        if(opened) {
            for (Component component : this.components) { // using for loop because continue isn't supported on foreach
                component.getSetting().constantCheck();
                if(component.getSetting().isHide()) continue;

                offset += component.getOffset();
            }
        }

        this.setOffset(moduleHeight + offset);
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
