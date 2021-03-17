package lime.cguinew;

import lime.Lime;
import lime.cguinew.components.Component;
import lime.cguinew.components.buttons.Checkbox;
import lime.cguinew.components.buttons.Combo;
import lime.module.Module;
import lime.settings.Setting;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {

    // Values
    int x = 0, y = 30;
    int width = 500, height = 350;
    Module.Category currentCategory;
    Module currentModule, modBinding;
    boolean drag;
    int xDrag, yDrag;
    int offset = 2, offsetSet = 0;
    int rendered = 0;
    ArrayList<Component> components = new ArrayList<>();

    public ClickGui(){
        for (Setting set : Lime.setmgr.getSettings()){
            if(set.isCheck()) components.add(new Checkbox(set));
            if(set.isCombo()) components.add(new Combo(set));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(drag && xDrag + mouseX > 0 && yDrag + mouseY - 30 > 0) {
            x = xDrag + mouseX;
            y = yDrag + mouseY;
        }

        if(Mouse.hasWheel() && currentCategory != null && hover(x + 40, y, mouseX, mouseY, 160, height)){
            int maxOffset = -1;
            if(Lime.moduleManager.getModulesByCategory(currentCategory).size() > 10){
                int mods = Lime.moduleManager.getModulesByCategory(currentCategory).size() - 10;
                maxOffset = mods * 10;
            }
            int wheel = Mouse.getDWheel();
            if(wheel > 0 && offset - 10 >= 0){
                offset -= 10;
            }
            if(wheel < 0 && maxOffset + 10 > offset + 10){
                offset += 10;
            }
        }

        if(Mouse.hasWheel() && currentModule != null && hover(x + 200, y, mouseX, mouseY, width - 200, height)){
            int wheel = Mouse.getDWheel();

            if(wheel > 0 && offsetSet - 9 > 0){
                offsetSet -= 10;
            }
            if(wheel < 0){
                offsetSet += 10;
            }
        }


        drawPanel(x, y, width, height, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawPanel(int x, int y, int width, int height, int mouseX, int mouseY){
        Util2D.drawRoundedRect(x, y - 30, width, 35, 5, new Color(24, 22, 22, 255).getRGB(), false);
        Gui.drawRect(x, y - 1, x + width, y, new Color(34, 32, 32, 255).getRGB());
        Util2D.drawRoundedRect(x, y, width, height, 5, new Color(24, 22, 22, 255).getRGB(), false);
        Gui.drawRect(x + 40, y, x + 200, y + height, new Color(15, 15, 15, 255).getRGB());

        Lime.fontManager.roboto_sense.drawString("Lime", x + 10, y - 15 - Lime.fontManager.roboto_sense.FONT_HEIGHT + 5, -1);


        if(currentCategory != null)
            Lime.fontManager.roboto_sense.drawString(StringUtils.capitalize(currentCategory.name().toLowerCase()) + " | " + Lime.moduleManager.getModulesByCategory(currentCategory).size() + " modules", x + 45, y - 10 - Lime.fontManager.roboto_sense.FONT_HEIGHT, -1);
        if(currentModule != null)
            Lime.fontManager.roboto_sense.drawString(StringUtils.capitalize(currentCategory.name().toLowerCase()) + "/" + currentModule.name + ".exe", x + 205, y - 10 - Lime.fontManager.roboto_sense.FONT_HEIGHT, -1);
        int category = 1;
        for(Module.Category cat : Module.Category.values()){
            boolean flag = hover(x, y - 48 + (category * 48), mouseX, mouseY, 40, 47);
            if(flag){
                Gui.drawRect(x, y - 48 + (category * 48), x + 40, y - 48 + (category * 48) + 47, new Color(30, 30, 30, 255).getRGB());
            }
            Util2D.drawImage(new ResourceLocation("textures/icons/" + cat.name().toLowerCase() + ".png"), x + 4, y - 42 + (category * 48) + 3, 32, 32);
            Gui.drawRect(x, y - 48 + (category * 48) + 47, x + 40, y - 48 + (category * 48) + 48, new Color(34, 32, 32, 255).getRGB());
            category++;
        }
        if(currentCategory != null){
            int module = 1;
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            prepareScissorBox(x + 40, y + 1, x + 200, y + height);
            for(Module mod : Lime.moduleManager.getModulesByCategory(currentCategory)){
                boolean flag = hover(x + 40, y - 16 + (module * 30) - 10 - offset, mouseX, mouseY, 160,29);
                if(flag && mouseY > y && mouseY < y + height){
                    Gui.drawRect(x + 40, y - 16 + (module * 30) - 12 - offset, x + 200, y - 18 + (module * 30) - 12 - offset + 31, new Color(24, 22, 22, 255).getRGB());
                }
                Util2D.drawdCircle(x + 48, y - 16 + (module * 30) + Lime.fontManager.roboto_sense.FONT_HEIGHT - 4 - offset - 1, 2, 2, mod.isToggled() ? new Color(0, 150, 0).getRGB() : new Color(150, 0, 0).getRGB());
                Lime.fontManager.roboto_sense.drawString(mod.getName() + (mod.binding ? ": Bind a key" : ""), x + 64, y - 16 + (module * 30) - offset, -1);
                Gui.drawRect(x + 40, y - 5 + (module * 30) + 6 - offset, x + 200, y - 5 + (module * 30) + 7 - offset, new Color(24, 22, 22, 255).getRGB());
                module++;
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        }

        if(currentModule != null){
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            prepareScissorBox(x + 200, y + 5, x + 200 + (width - 200), y + height);
            for(Component comp : components){
                if(comp.set.getParentMod() == currentModule){
                    comp.draw(x + 205, y + 5 + rendered - offsetSet, width, height, mouseX, mouseY);
                    if(comp instanceof Checkbox) rendered += 15;
                    if(comp instanceof Combo) rendered += 30 + comp.rendered;
                }
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
            rendered = 0;
        }
        Gui.drawRect(x + 199, y - 30, x + 200, y + height, new Color(34, 32, 32, 255).getRGB());
        Gui.drawRect(x + 39, y - 30, x + 40, y + height, new Color(34, 32, 32, 255).getRGB());

    }
    public void prepareScissorBox(float x2, float y2, float x22, float y22) {
        ScaledResolution scale = new ScaledResolution(this.mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x2 * (float)factor), (int)(((float)scale.getScaledHeight() - y22) * (float)factor), (int)((x22 - x2) * (float)factor), (int)((y22 - y2) * (float)factor));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // ClickGui Drag
        if(hover(x, y - 30, mouseX, mouseY, width, 30) && mouseButton == 0) {
            xDrag = x - mouseX;
            yDrag = y - mouseY;
            drag = true;
        } else {
            drag = false;
        }

        for(Component comp : components){
            comp.mouseClicked(mouseX, mouseY, mouseButton);
        }

        // Category Detection
        int category = 1;
        for(Module.Category cat : Module.Category.values()){
            if(hover(x, y - 48 + (category * 48), mouseX, mouseY, 40, 47)){
                if(mouseButton == 0){
                    if(currentCategory != cat){
                        currentCategory = cat;
                        currentModule = null;
                        offset = 2;
                    }
                }
            }
            category++;
        }
        // Module Detection
        int module = 1;
        for(Module mod : Lime.moduleManager.getModulesByCategory(currentCategory)){
            boolean flag = hover(x + 40, y - 16 + (module * 30) - 10 - offset, mouseX, mouseY, 160,29);
            if(flag && mouseY > y && mouseY < y + height){
                if(mouseButton == 0 && !mod.name.equalsIgnoreCase("clickgui")) mod.toggle();
                if(mouseButton == 1) currentModule = mod;
                if(mouseButton == 2){
                    mod.binding = true;
                    modBinding = mod;
                }
            }
            module++;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for(Component comp : components){
            comp.mouseReleased(mouseX, mouseY, state);
        }
        drag = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for(Component comp : components){
            comp.keyTyped(typedChar, keyCode);
        }
        boolean dontEscape = false;
        if(this.modBinding != null && this.modBinding.binding){
            if(keyCode == 1){
                dontEscape = true;
            } else {
                modBinding.setKey(keyCode);
            }
            modBinding.binding = false;
            modBinding = null;
        }
        if(!dontEscape && keyCode == 1){
            mc.displayGuiScreen(null);
            if(mc.currentScreen == null)
                mc.setIngameFocus();
        }


    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public boolean hover(int x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
