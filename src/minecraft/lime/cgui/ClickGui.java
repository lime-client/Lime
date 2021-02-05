package lime.cgui;

import lime.Lime;
import lime.cgui.component.Component;
import lime.cgui.component.button.Checkbox;
import lime.cgui.component.button.Combo;
import lime.cgui.component.button.Slider;
import lime.cgui.settings.Setting;
import lime.module.Module;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {
    public int x = 0;
    public int y = 0;
    public int width = 250;
    public int height = 300;
    public int xDrag, yDrag;
    public boolean drag;
    public Module.Category currentCat;
    public Module currentMod;
    public int rendered;
    public ArrayList<Component> components = new ArrayList<Component>();
    public Module modBinding;
    private boolean wasClosed = true;
    public ClickGui(){

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
    public void initGui() {
        if(!mc.gameSettings.ofFastRender &&  mc.getRenderViewEntity() instanceof EntityPlayer && OpenGlHelper.shadersSupported && Lime.setmgr.getSettingByName("Blur").getValBoolean()){
            if (mc.entityRenderer.theShaderGroup != null) {
                mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            }
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
        if(wasClosed){
            for(Setting set: Lime.setmgr.getSettings()){
                if(set.isCombo())
                    components.add(new Combo(set));
                if(set.isSlider())
                    components.add(new Slider(set));
                if(set.isCheck())
                    components.add(new Checkbox(set));

            }
            wasClosed =false;
        }
        drag = false;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(drag) {
            x = xDrag + mouseX;
            y = yDrag + mouseY;
        }
        drawPanel(x, y, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    void drawPanel(int xPos, int yPos, int mouseX, int mouseY) {
        rendered = 0;
        //if(!components.isEmpty() && currentCat != null && currentMod != null && components.get(0).set.getParentMod() != currentMod) components.clear();
        Util2D.drawRoundedRect(xPos - 1, yPos - 1, xPos + width + 1, yPos + height + 1, new Color(50, 50, 50).getRGB(), new Color(50, 50, 50).getRGB());
        Util2D.drawRoundedRect(xPos, yPos, xPos + width, yPos + height, new Color(25, 25, 25).getRGB(), new Color(25, 25, 25).getRGB());
        Lime.fontManager.comfortaa_hud.drawString(Lime.clientName, x + (width / 2) - Lime.fontManager.comfortaa_hud.getStringWidth(Lime.clientName) + 10, y + 4, -1);
        Gui.drawRect(xPos, y + 14, x + width, yPos + 15, new Color(50, 50, 50).getRGB());
        Gui.drawRect(xPos, yPos + 54, x + width, yPos + 55, new Color(50, 50, 50).getRGB());
        Gui.drawRect(xPos + (width / 2), yPos + 54, xPos + (width / 2) + 1, y + height, new Color(50, 50, 50).getRGB());
        Util2D.DrawCroix(x + width - 15, y +1, 10, new Color(50, 50, 50).getRGB());
        int i = 0;
        for(Module.Category c : Module.Category.values()){
            if(!c.name().equalsIgnoreCase("misc")){
                Gui.drawRect(0, 0, 0, 0, 0);
                boolean flag = hover(x + (i * 50), y + 15, mouseX, mouseY, 50, 40);
                if(flag){
                    Gui.drawRect(x + (i * 50) , y + 15, x + (i * 50) + 10 + 40, y + 19 + 35, new Color(75, 75, 75).getRGB());
                }
                Util2D.drawImage(new ResourceLocation("textures/icons/" + c.name().toLowerCase() + ".png"), x + (i * 50) + 10, y + 19, 32, 32);
                Gui.drawRect(x + (i * 50) + 50, y + 15, x + (i * 50) + 51, y + 55, new Color(50, 50, 50).getRGB());
            } else {
                boolean flag = hover(x + (i * 50) + 10, y + 19, mouseX, mouseY, 32, 32);
                if(flag)
                    Gui.drawRect(x + (i * 50) , y + 15, x + (i * 50) + 10 + 40, y + 19 + 35, new Color(75, 75, 75).getRGB());
                Util2D.drawImage(new ResourceLocation("textures/icons/world.png"), x + 200 + 10, y + 19, 32, 32);
            }
            i++;
        }
        for(Module m : Lime.moduleManager.getModulesByCategory(currentCat)){
            boolean flag = hover(x, yPos - 80 + (i * 30) - 14, mouseX, mouseY, width / 2, 27);
            if(flag){
                Gui.drawRect(x, yPos - 80 + (i * 30) + 15, x + width / 2, yPos - 80 + (i * 30) - 15, new Color(75, 75, 75).getRGB());
            }
            Lime.fontManager.comfortaa_hud.drawString(m.binding ? "Set a key" : m.name, x + 5, yPos - 80 + (i * 30) - 2, -1);
            Util2D.drawFullCircle(x + (width / 2) - 20, yPos - 80 + (i * 30) + 1, 5, 5, m.toggled ? new Color(0, 150, 0).getRGB() : new Color(75, 0, 0).getRGB());
            Gui.drawRect(0, 0, 0, 0, 0);
            Gui.drawRect(x, yPos - 80 + (i * 30) + 15, x + (width / 2), yPos - 80 + (i * 30) + 16, new Color(50, 50, 50).getRGB());
            i++;
        }
        if(currentCat != null && currentMod != null && currentMod.hasSettings()){
            for(Component component : components){
                if(component.set.getParentMod() == currentMod){

                    component.render(x, y, width, height, mouseX, mouseY);
                    rendered++;
                }
            }
        }
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(hover(x + width - 15, y +1, mouseX, mouseY, 10, 10) && mouseButton == 0) {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
        if(hover(x, y, mouseX, mouseY, width, 15) && mouseButton == 0) {
            xDrag = x - mouseX;
            yDrag = y - mouseY;
            drag = true;
        } else {
            drag = false;
        }
        if(hover(x, y + 15, mouseX, mouseY, 50, 40) && mouseButton == 0){
            if(currentCat != Module.Category.COMBAT){
                currentMod = null;
            }
            currentCat = Module.Category.COMBAT;
        }
        if(hover(x + 50, y + 15, mouseX, mouseY, 50, 40) && mouseButton == 0){
            if(currentCat != Module.Category.MOVEMENT){
                currentMod = null;
            }
            currentCat = Module.Category.MOVEMENT;
        }
        if(hover(x + (2 * 50), y + 15, mouseX, mouseY, 50, 40) && mouseButton == 0){
            if(currentCat != Module.Category.PLAYER){
                currentMod = null;
            }
            currentCat = Module.Category.PLAYER;
        }
        if(hover(x + (3 * 50), y + 15, mouseX, mouseY, 50, 40) && mouseButton == 0){
            if(currentCat != Module.Category.RENDER){
                currentMod = null;
            }
            currentCat = Module.Category.RENDER;
        }
        if(hover(x + (4 * 50), y + 15, mouseX, mouseY, 50, 40) && mouseButton == 0){
            if(currentCat != Module.Category.MISC){
                currentMod = null;
            }
            currentCat = Module.Category.MISC;
        }
        int i = 5;
        if(mouseButton == 0){
            for(Module ignored :  Lime.moduleManager.getModulesByCategory(currentCat)){
                boolean flag = hover(x, y - 80 + (i * 30) - 14, mouseX, mouseY, width / 2, 27);
                if(flag){
                    ArrayList<Module> moduleArrayList = Lime.moduleManager.getModulesByCategory(currentCat);
                    Module mod = moduleArrayList.get(i - 5);
                    if(!mod.name.equalsIgnoreCase("clickgui")) {
                        mod.toggle();
                    }
                }

                i++;
            }
        }
        if(mouseButton == 1){
            for(Module ignored :  Lime.moduleManager.getModulesByCategory(currentCat)){
                boolean flag = hover(x, y - 80 + (i * 30) - 14, mouseX, mouseY, width / 2, 27);
                if(flag){
                    ArrayList<Module> moduleArrayList = Lime.moduleManager.getModulesByCategory(currentCat);
                    Module mod = moduleArrayList.get(i - 5);
                    if(mod.hasSettings()) currentMod = mod;
                }

                i++;
            }
        }
        if(mouseButton == 2){
            for(Module ignored :  Lime.moduleManager.getModulesByCategory(currentCat)){
                boolean flag = hover(x, y - 80 + (i * 30) - 14, mouseX, mouseY, width / 2, 27);
                if(flag){
                    ArrayList<Module> moduleArrayList = Lime.moduleManager.getModulesByCategory(currentCat);
                    Module mod = moduleArrayList.get(i - 5);
                    mod.binding = true;
                    modBinding = mod;


                }

                i++;
            }
        }
        if(currentCat != null && currentMod != null && currentMod.hasSettings()){
            for(Component component : components){
                if(component.set.getParentMod() == currentMod)
                    component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        drag = false;
        if(currentCat != null && currentMod != null && currentMod.hasSettings()){
            for(Component component : components){
                if(component.set.getParentMod() == currentMod)
                    component.mouseReleased(mouseX, mouseY, state);
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean dontEscape = false;
        if(this.modBinding != null && this.modBinding.binding){
            if(keyCode == 1) {
                dontEscape = true;
                this.modBinding.binding = false;
                this.modBinding = null;
            } else {
                this.modBinding.setKey(keyCode);
                this.modBinding.binding = false;
                this.modBinding = null;
            }

        }
        if (keyCode == 1 && !dontEscape)
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
    }

    public boolean hover(int x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
