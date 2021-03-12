package lime.cgui.cgui2;

import lime.Lime;
import lime.cgui.cgui2.component.Component;
import lime.cgui.cgui2.component.components.Checkbox;
import lime.cgui.cgui2.component.components.Combo;
import lime.cgui.settings.Setting;
import lime.module.Module;
import lime.utils.Timer;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui2 extends GuiScreen {
    boolean drag;
    public int x = 0;
    public int y = 0;
    public int rendered = 0;
    public int width = 500;
    public int height = 336;
    public int xDrag;
    public int yDrag;
    public Module.Category currentCat;
    public Module modBinding;
    public Module currentMod;
    public ArrayList<Component> components = new ArrayList<>();
    public boolean wasClosed = true;
    public Timer timer = new Timer();
    public ClickGui2(){}

    @Override
    public void initGui() {

        if(wasClosed){
            for(Setting set: Lime.setmgr.getSettings()){
                if(set.isCheck())
                    components.add(new Checkbox(set));
                if(set.isCombo())
                    components.add(new Combo(set));
            }
            wasClosed =false;
        }

        if(!mc.gameSettings.ofFastRender &&  mc.getRenderViewEntity() instanceof EntityPlayer && OpenGlHelper.shadersSupported && Lime.setmgr.getSettingByName("Blur").getValBoolean()){
            if (mc.entityRenderer.theShaderGroup != null) {
                mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            }
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
        drag = false;
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        if (mc.entityRenderer.theShaderGroup != null) {
            mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            mc.entityRenderer.theShaderGroup = null;
        }
        wasClosed = true;
        components.clear();
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragAnimation(mouseX, mouseY);
        drawGui(x, y, mouseX, mouseY);
        componentsCallRender(x, y, width, height, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawGui(int x, int y, int mouseX, int mouseY){
        rendered = 0;
        // GuiDraw
        Gui.drawRect(x, y, x + width, y + height, new Color(20, 20, 20).getRGB());
        Gui.drawRect(x, y + 14, x + width, y + 15, new Color(25, 25, 25).getRGB());
        Gui.drawRect(x + 64, y + 15, x + 65, y + height, new Color(25, 25, 25).getRGB());
        Gui.drawRect(x + 248, y + 15, x + 249, y + height, new Color(25, 25, 25).getRGB());
        Lime.fontManager.comfortaa_hud.drawString("Lime Hackerino Mode $$$.exe", x + 3, y + 5, -1);
        //Image Category Draw
        int i = 0;
        for(Module.Category cat : Module.Category.values()){
            fix();
            Gui.drawRect(x, y + 15 + (i * 64) - 1, x + 65, y + 16 + (i * 64) - 1, new Color(25, 25, 25).getRGB());
            boolean flag = hover(x, y + 15 + (i * 64), mouseX, mouseY, 64, 64);
            if(flag)
                Gui.drawRect(x, y + 15 + (i * 64), x + 65, y + 15 + (i * 64) + 64, new Color(25, 25,25).getRGB());
            if(cat != Module.Category.MISC){
                Util2D.drawImage(new ResourceLocation("textures/icons/" + cat.name().toLowerCase() + ".png"), x, y + 15 + (i * 64), 64, 64);
            } else {
                Util2D.drawImage(new ResourceLocation("textures/icons/world.png"), x, y + 15 + (i * 64), 64, 64);
            }
            i++;
        }

        // Module Draw String

        if(currentCat != null){
            int g = 1;
            for(Module module : Lime.moduleManager.getModulesByCategory(currentCat)){
                boolean flag = hover(x + 65, y + (g * 28) - 14, mouseX, mouseY, 35 + 150, 28);
                fix();
                if(flag)
                    Gui.drawRect(x + 65, y + (g * 28) - 14, x + 70 + 179, y + (g * 28) + 14, new Color(25, 25, 25).getRGB());
                fix();
                Lime.fontManager.comfortaa_hud.drawString(module.binding ? "Press a key..." : module.getName(), x + 70, y + (g * 28) - 3, -1);
                fix();
                Gui.drawRect(x + 65, y + (g * 28) + Lime.fontManager.comfortaa_hud.FONT_HEIGHT + 5, x + 70 + 179, y + (g * 28) + Lime.fontManager.comfortaa_hud.FONT_HEIGHT + 6, new Color(25, 25, 25).getRGB());
                Util2D.drawdCircle(x + 70 + 164, y + (g * 28) + 1, 5, 5, module.isToggled() ? new Color(0, 150, 0).getRGB() : new Color(150, 0, 0).getRGB());
                g++;
            }
        }


        // Croix
        Util2D.DrawCroix(x + width - 15, y + 2, 10, new Color(25, 25, 25).getRGB());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        dragAnimationClick(mouseX, mouseY, mouseButton);
        componentsCallMouseClicked(mouseX, mouseY, mouseButton);
        closeGuiDetection(mouseX, mouseY, mouseButton);

        // Click Category Detection
        int i = 0;
        for(Module.Category cat : Module.Category.values()){
            if(hover(x, y + 15 + (i * 64), mouseX, mouseY, 64, 64)){
                currentCat = cat;
            }
            i++;
        }



        // Click Module Detection
        int g = 1;
        for(Module mod : Lime.moduleManager.getModulesByCategory(currentCat)){
            boolean flag = hover(x + 65, y + (g * 28) - 14, mouseX, mouseY, 35 + 150, 28);
            if(flag && mouseButton == 0) mod.toggle();
            if(flag && mouseButton == 1) currentMod = mod;
            if(flag && mouseButton == 2){
                mod.binding = true;
                modBinding = mod;
            }
            g++;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        drag = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean dontEscape = false;
        if(keyCode == 1){
            if(modBinding != null && modBinding.binding){
                dontEscape = true;
                modBinding.binding = false;
                modBinding = null;
            }
        }
        if(modBinding != null && modBinding.binding && keyCode != 1){
            modBinding.setKey(keyCode);
            modBinding.binding = false;
            modBinding = null;
        }
        if(keyCode == 1 && !dontEscape){
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
        super.keyTyped(typedChar, keyCode);
    }


    public boolean hover(int x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void fix(){
        Gui.drawRect(0, 0, 0, 0, 0);
    }

    public void closeGuiDetection(int mouseX, int mouseY, int mouseButton){
        if(hover(x + width - 15, y + 2, mouseX, mouseY, 10, 10) && mouseButton == 0){
            this.mc.displayGuiScreen((GuiScreen)null);
            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
            // Call MouseClicked to Components
            for(Component component : components){
                component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void componentsCallRender(int x, int y, int width, int height, int mouseX, int mouseY){
        if(currentCat != null && currentMod != null){
            for(Component component : components){
                if(component.set.getParentMod() == currentMod){
                    component.render(x, y, width, height, mouseX, mouseY);
                    if(component.set.isCombo()) rendered += 28;
                    if(component.set.isSlider()) rendered += 18;
                    if(component.set.isCheck()) rendered += 16;
                    component.rendered = this.rendered;
                }


            }
        }
    }
    public void componentsCallMouseClicked(int mouseX, int mouseY, int mouseButton){
        if(currentCat != null && currentMod != null){
            for(Component component : components){
                if(component.set.getParentMod() == currentMod)
                    component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }


    }

    public void dragAnimation(int mouseX, int mouseY){
        if(drag) {
            x = xDrag + mouseX;
            y = yDrag + mouseY;
        }
    }

    public void dragAnimationClick(int mouseX, int mouseY, int mouseButton){
        if(hover(x, y, mouseX, mouseY, width, 15) && mouseButton == 0) {
            xDrag = x - mouseX;
            yDrag = y - mouseY;
            drag = true;
        } else {
            drag = false;
        }
    }
}
