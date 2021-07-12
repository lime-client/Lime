package lime.ui.clickgui.frame.components;

import lime.core.Lime;
import lime.managers.FontManager;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.SettingValue;
import lime.features.setting.impl.*;
import lime.ui.clickgui.frame.components.settings.*;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FrameCategory {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Category category;
    private int x, y, height, xDrag, yDrag;
    private final int width;
    private boolean drag;
    private final ArrayList<Module> mods = new ArrayList<>(Lime.getInstance().getModuleManager().getModules());
    private final ArrayList<Module> openedModules = new ArrayList<>();
    private final ArrayList<Component> components = new ArrayList<>();
    private Module bindingModule;

    private boolean isOpened;
    private boolean endedAnimation;

    private final Animate animation = new Animate();

    public FrameCategory(Category category, int baseX, int baseY, int width) {
        this.category = category;
        this.x = baseX;
        this.y = baseY;
        this.width = width;
        this.height = 15;
        this.drag = false;
        this.xDrag = 0;
        this.yDrag = 0;
        animation.setMin(0);
        animation.setSpeed(125);
        animation.setEase(Easing.CUBIC_OUT);
        animation.setReversed(false);

        mods.sort((m1, m2) -> {
            String s1 = m1.getName();
            String s2 = m2.getName();
            return s1.compareToIgnoreCase(s2);
        });

        for(SettingValue set : Lime.getInstance().getSettingsManager().getSettings()) {
            if(set instanceof EnumValue) {
                this.components.add(new EnumSetting(0, 0, 0, 0, set));
            }
            if(set instanceof BoolValue) {
                this.components.add(new BoolSetting(0, 0, 0, 0, set));
            }
            if(set instanceof SlideValue) {
                this.components.add(new SlideSetting(0, 0, 0, 0, set));
            }
            if(set instanceof TextValue) {
                this.components.add(new TextSetting(0, 0, 0, 0, ((TextValue) set).getText(), set));
            }
            if(set instanceof ColorValue) {
                this.components.add(new ColorSetting(0, 0, 0, 0, ((ColorValue) set).getColor(), set));
            }
        }
        isOpened = false;
    }

    public void initGui() {
        animation.setMax(y);
        endedAnimation = false;
        y = 0;
        animation.reset();
    }

    public void drawFrame(int mouseX, int mouseY) {
        if(y < 0 && endedAnimation) {
            y = 0;
        }

        animation.update();
        if(y == (int) animation.getMax()) {
            endedAnimation = true;
        } else if(!endedAnimation) {
            y = (int) animation.getValue();
        }
        if(drag) {
            this.x = this.xDrag + mouseX;
            this.y = this.yDrag + mouseY;
        }

        openedModules.sort((m1, m2) -> {
            String s1 = m1.getName();
            String s2 = m2.getName();
            return s1.compareToIgnoreCase(s2);
        });

        for(Component component : this.components) {
            if(openedModules.isEmpty() || !openedModules.contains(component.setting.getParentModule())) {
                component.setX(-1);
            }
        }

        Gui.drawRect(x, y, x + width, y + height, new Color(41, 41, 41).getRGB());
        Gui.drawRect(x, y, x + width, y + 15, new Color(25, 25, 25).getRGB());
        //arround
        Gui.drawRect(x, y, x + 1, y + height, new Color(25, 25, 25).getRGB());
        Gui.drawRect(x + width - 1, y, x + width, y + height, new Color(25, 25, 25).getRGB());
        Gui.drawRect(x, y + height - 1, x + width, y + height, new Color(25, 25, 25).getRGB());
        GL11.glPushMatrix();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
        FontManager.ProductSans18.getFont().drawString(StringUtils.capitalize(category.name().toLowerCase()), x + 3, y + 1.5f, -1, true);

        mc.getTextureManager().bindTexture(new ResourceLocation("lime/clickgui/frame/" + category.name().toLowerCase() + ".png"));
        Gui.drawModalRectWithCustomSizedTexture(x + width - 12, y + 3, 0, 0, 8, 8, 8, 8);

        if(!isOpened) {
            height = 15;
            return;
        }


        for (Component component : this.components) {
            component.setting.constantCheck();
            if(component instanceof TextSetting) {
                TextSetting textSetting = (TextSetting) component;
                if(!this.openedModules.contains(textSetting.setting.getParentModule()) && textSetting.isFocused) {
                    textSetting.setFocused(false);
                }
            }
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.prepareScissorBox(x, y, x + width, y + height);

        // Draw Modules
        int i = 0;
        for(Module module : mods.stream().filter(module -> module.getCategory() == category).collect(Collectors.toCollection(ArrayList::new))) {
            if(module.isToggled()) {
                Gui.drawRect(x, y + 15 + (i * 16), x + width, y + 15 + (i * 16) + 16, new Color(25, 25, 25, 150).getRGB());
            }
            if(hover(x, y + 15 + (i * 16), mouseX, mouseY, width, 16)) {
                Gui.drawRect(x, y + 15 + (i * 16), x + width, y + 15 + (i * 16) + 16, module.isToggled() ? new Color(25, 25, 25, 150).darker().getRGB() : new Color(25, 25, 25, 150).getRGB());
            }
            FontManager.ProductSans20.getFont().drawString(module.getName() + (module == bindingModule ? " [Binding...]" : "") + (Keyboard.isKeyDown(29) && Keyboard.isKeyDown(42) && module.getKey() != -1 ? " [" + Keyboard.getKeyName(module.getKey()) + "]" : ""), x + 3, y + 16 + (i * 16), module.isToggled() ? new Color(125, 125, 125).getRGB() : -1, true);
            if(module.hasSettings()) {
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 1);
                mc.getTextureManager().bindTexture(!openedModules.isEmpty() && openedModules.contains(module) ? new ResourceLocation("lime/clickgui/frame/expand.png") : new ResourceLocation("lime/clickgui/frame/collapse.png"));
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                Gui.drawModalRectWithCustomSizedTexture((x + width - 12) * 2, (y + 20 + (i * 16)) * 2, 0, 0, 16, 10, 16, 10);
                GL11.glPopMatrix();
                if(openedModules.contains(module)) {
                    ArrayList<Component> moduleComponents = this.components.stream().filter(component -> component.setting.getParentModule() == module).collect(Collectors.toCollection(ArrayList::new));

                    for(Component component : moduleComponents) {
                        if(component.setting.isHide()) continue;
                        ++i;
                        component.setX(x + 3);
                        component.setY(y + 20 + (i * 16) - 8);
                        component.setWidth(width);
                        component.drawComponent(mouseX, mouseY);
                        if(component instanceof ColorSetting) {
                            i += 7;
                        }
                    }
                }
            }
            ++i;
        }
        height = i * 16 + 16;

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        if(bindingModule != null) {
            if(keyCode == 1) {
                bindingModule.setKey(-1);
                bindingModule = null;
                return true;
            }
            bindingModule.setKey(keyCode);
            bindingModule = null;
            return false;
        }

        for (Component component : components) {
            component.onKeyTyped(typedChar, keyCode);
        }
        return false;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean dragFrame = true;
        if(isOpened) {
            int i = 0;
            for(Module module : mods.stream().filter(module -> module.getCategory() == category).collect(Collectors.toCollection(ArrayList::new))) {
                if(hover(x, y + 15 + (i * 16), mouseX, mouseY, width, 16)) {
                    if(mouseButton == 0 && !module.getName().equalsIgnoreCase("clickgui")) module.toggle();
                    if(mouseButton == 1) {
                        if(openedModules.contains(module))
                            openedModules.remove(module);
                        else
                        if(module.hasSettings())
                            openedModules.add(module);
                    }
                    if(mouseButton == 2) {
                        bindingModule = module;
                    }
                    return true;
                }
                if(openedModules.contains(module)) {
                    ArrayList<Component> components = this.components.stream().filter(component -> !component.setting.isHide()).filter(component -> component.setting.getParentModule() == module).collect(Collectors.toCollection(ArrayList::new));
                    i += components.size() + components.stream().filter(component -> component instanceof ColorSetting).collect(Collectors.toCollection(ArrayList::new)).size() * 7;
                }
                ++i;
            }
            if(!openedModules.isEmpty()) {
                int settingIndex = 0;
                for(Module openedModule : openedModules) {
                    for(SettingValue setting : Lime.getInstance().getSettingsManager().getSettingsFromModule(openedModule)) {
                        if(setting.isHide()) continue;
                        settingIndex++;
                        // Getting I;
                        int moduleIndex = 0;
                        for(Module module : mods.stream().filter(module -> module.getCategory() == category).collect(Collectors.toCollection(ArrayList::new))) {
                            if(openedModule != module) {
                                ++moduleIndex;
                            }
                            else
                                break;
                        }

                        moduleIndex = y + 16 + moduleIndex * 16;

                        if(hover(x, moduleIndex + (settingIndex * 16), mouseX, mouseY, width, setting instanceof ColorValue ? 125  : 13)) {
                            dragFrame = false;
                            for(Component component : this.components.stream().filter(component -> component.setting == setting).collect(Collectors.toCollection(ArrayList::new))) {
                                component.mouseClicked(mouseX, mouseY, mouseButton);
                                return true;
                            }
                        }

                        if(setting instanceof ColorValue) settingIndex += 7;
                    }
                }
            }
        }

        if(hover(x, y, mouseX, mouseY, width, height) && mouseButton == 1) {
            isOpened = !isOpened;
        }

        if(hover(x, y, mouseX, mouseY, width, height) && dragFrame && mouseButton == 0) {
            this.drag = true;
            this.xDrag = this.x - mouseX;
            this.yDrag = this.y - mouseY;
            return true;
        } else
            drag = false;

        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.drag = false;
        for(Component component : components) {
            component.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void onGuiClosed() {
        for (Component component : this.components) {
            component.onGuiClosed();
        }
        this.drag = false;
    }

    public boolean hover(int x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
