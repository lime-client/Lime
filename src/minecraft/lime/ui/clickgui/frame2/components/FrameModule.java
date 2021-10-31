package lime.ui.clickgui.frame2.components;

import lime.core.Lime;
import lime.features.module.Module;
import lime.features.setting.impl.*;
import lime.management.FontManager;
import lime.ui.clickgui.frame2.Priority;
import lime.ui.clickgui.frame2.components.impl.*;
import lime.utils.render.ColorUtils;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static lime.ui.clickgui.frame2.Priority.*;

public class FrameModule {
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
                if(setting instanceof SubOptionProperty) {
                    this.components.add(new SubOptionSetting(0, 0, this, setting));
                }
            });
            Lime.getInstance().getSettingsManager().getSettingsFromModule(module).forEach(setting ->
            {
                if(!setting.isOwned()) {
                    if(setting instanceof BooleanProperty)
                    {
                        this.components.add(new BoolSetting(0, 0, this, setting));
                    }
                    if(setting instanceof EnumProperty)
                    {
                        this.components.add(new EnumSetting(0, 0, this, setting));
                    }
                    if(setting instanceof NumberProperty)
                    {
                        this.components.add(new NumberSetting(0, 0, this, setting));
                    }
                    if(setting instanceof TextProperty)
                    {
                        this.components.add(new TextSetting(0, 0, this, setting));
                    }
                }
            });
        }
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        moduleAnimation.setReversed(!module.isToggled());
        moduleAnimation.setSpeed(1000).update();

        if(module.isToggled() || (moduleAnimation.isReversed() && moduleAnimation.getValue() != 0)) {
            Gui.drawRect(x,y, x + defaultWidth, y + moduleHeight, ColorUtils.setAlpha(new Color(getEnabledColor()), (int) moduleAnimation.getValue()).getRGB());
        }
        if(GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && Priority.hoveredColor) {
            Gui.drawRect(x,y, x + defaultWidth, y + moduleHeight, getDarkerMainColor());
        }

        if(module.hasSettings()) {
            if(Lime.getInstance().getSettingsManager().getSettingsFromModule(module).size() != 1 && !Lime.getInstance().getSettingsManager().getSettingsFromModule(module).get(0).getSettingName().equalsIgnoreCase("show")) {
                GL11.glPushMatrix();
                GlStateManager.enableBlend();
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                RenderUtils.drawImage(opened ? new ResourceLocation("lime/clickgui/frame/expand.png") : new ResourceLocation("lime/clickgui/frame/collapse.png"), (x + defaultWidth - 12) * 2, (y + 5) * 2, 16, 10, true);
                GL11.glColor4f(1, 1, 1, 1);
                GlStateManager.resetColor();
                GlStateManager.disableBlend();
                GL11.glPopMatrix();
            }
        }

        FontManager.ProductSans20.getFont().drawString(module.getName(), x+3, y + (moduleHeight / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)), module.isToggled() ? new Color(stringColor).darker().darker().getRGB() : stringColor, true);

        int offset = 0;

        if(opened) {
            for (Component component : this.components) { // using for loop because continue isn't supported on foreach
                if(!component.isSubList()) {
                    component.getSetting().constantCheck();
                    if(component.getSetting().isHide()) continue;

                    component.setX(x);
                    component.setY(y + moduleHeight + offset);

                    component.drawScreen(mouseX, mouseY);

                    offset += component.getOffset();
                }
            }
        }

        this.setOffset(moduleHeight + offset);

        Gui.drawRect(0, 0, 0, 0, 0);
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

        if(opened) {
            for (Component component : this.components) {
                if(!component.isSubList()) {
                    if((GuiScreen.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight()) || component instanceof TextSetting) && !component.getSetting().isHide() && component.mouseClicked(mouseX, mouseY, mouseButton))
                        return true;
                }
            }
        }

        return false;
    }

    public void keyTyped(char typedChar, int keyCode) {
        if(opened) {
            this.components.forEach(component -> component.keyTyped(typedChar, keyCode));
        }
    }

    public int getOffset() {
        offset = 0;
        if(opened) {
            for (Component component : this.components) { // using for loop because continue isn't supported on foreach
                if(!component.isSubList()) {
                    component.getSetting().constantCheck();
                    if(component.getSetting().isHide()) continue;
                    offset += component.getOffset();
                }
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
