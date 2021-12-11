package lime.ui.clickgui.frame2.components.impl;

import lime.features.setting.Setting;
import lime.features.setting.impl.*;
import lime.management.FontManager;
import lime.ui.clickgui.frame2.components.Component;
import lime.ui.clickgui.frame2.components.FrameModule;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static lime.ui.clickgui.frame2.Priority.defaultWidth;

public class SubOptionSetting extends Component {

    public SubOptionSetting(int x, int y, FrameModule owner, Setting setting) {
        super(x, y, owner, setting);
        components = new ArrayList<>();

        ((SubOptionProperty) getSetting()).getSettings().forEach(s -> {
            if(s instanceof TextProperty) {
                TextSetting e = new TextSetting(0, 0, owner, s);
                e.setSubList(true);
                this.components.add(e);
            } else if(s instanceof EnumProperty) {
                EnumSetting e = new EnumSetting(0, 0, owner, s);
                e.setSubList(true);
                this.components.add(e);
            } else if(s instanceof NumberProperty) {
                NumberSetting e = new NumberSetting(0, 0, owner, s);
                e.setSubList(true);
                this.components.add(e);
            } else if(s instanceof BooleanProperty) {
                BoolSetting e = new BoolSetting(0, 0, owner, s);
                e.setSubList(true);
                this.components.add(e);
            }
        });
    }

    private final ArrayList<Component> components;
    private final SubOptionProperty setting = (SubOptionProperty) getSetting();

    @Override
    public void initGui() {
        if(setting.isOpened()) {
            components.forEach(Component::initGui);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Gui.drawRect(x, y, x + defaultWidth, y + 15, new Color(25, 25, 25).getRGB());
        FontManager.ProductSans20.getFont().drawString(setting.getSettingName(), x + 3, y + (15 / 2F - (FontManager.ProductSans20.getFont().getFontHeight() / 2F)), -1);
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RenderUtils.drawImage(setting.isOpened() ? new ResourceLocation("lime/clickgui/frame/expand.png") : new ResourceLocation("lime/clickgui/frame/collapse.png"), (x + defaultWidth - 12) * 2, (y + 5) * 2, 16, 10, true);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();

        if(setting.isOpened()) {
            AtomicInteger offset = new AtomicInteger(15);
            components.forEach(c -> {
                c.getSetting().constantCheck();
                if(!c.getSetting().isHide()) {
                    c.setX(x);
                    c.setY(y + offset.get());
                    c.drawScreen(mouseX, mouseY);
                    offset.addAndGet(c.getOffset());
                }
            });
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, 15) && mouseButton == 1) {
            setting.setOpened(!setting.isOpened());
            return true;
        }
        if(setting.isOpened()) {
            for (Component component : components) {
                if(component.getSetting().isHide()) continue;
                if(component.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {
        if(setting.isOpened()) {
            components.forEach(c -> {
                if(!c.getSetting().isHide()) {
                    c.onGuiClosed(mouseX, mouseY, mouseButton);
                }
            });
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(setting.isOpened()) {

            components.forEach(c -> {
                if(!c.getSetting().isHide()) {
                    c.keyTyped(typedChar, keyCode);
                }
            });
        }
    }

    @Override
    public int getOffset() {
        AtomicInteger offset = new AtomicInteger(15);
        if(setting.isOpened()) {
            components.forEach(c -> {
                if(!c.getSetting().isHide()) {
                    offset.addAndGet(c.getOffset());
                }
            });
        }
        return offset.get();
    }
}
