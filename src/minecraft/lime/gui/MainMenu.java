package lime.gui;

import lime.Lime;
import lime.altmanager.GuiAltManager;
import lime.ui.GuiNewButton;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class MainMenu extends GuiScreen {
    public MainMenu(){

    }

    @Override
    public void initGui() {
        this.buttonList.add((GuiButton) new GuiNewButton(0, this.width / 2 - 100, this.height / 2 - 22 - 22 -22, 200, 20, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add((GuiButton) new GuiNewButton(1, this.width / 2 - 100, this.height / 2 - 22 - 22, 200, 20, I18n.format("menu.multiplayer", new Object[0])));
        this.buttonList.add((GuiButton) new GuiNewButton(2, this.width / 2 - 100, this.height / 2 - 22, 200, 20, I18n.format("menu.options", new Object[0]).replace("...", "")));
        this.buttonList.add((GuiButton) new GuiNewButton(3, this.width / 2 - 100, this.height / 2, 200, 20, "Alt Manager"));
        this.buttonList.add((GuiButton) new GuiNewButton(4, this.width / 2 - 100, this.height / 2 + 22, 200, 20, I18n.format("menu.quit", null)));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Util2D.drawImage(new ResourceLocation("lime/wp.jpg"), 0, 0, this.width, this.height);
        Util2D.drawImage(new ResourceLocation("lime/discord.png"), this.width - 42, 10, 36, 36);
        Util2D.drawImage(new ResourceLocation("lime/changelog.png"), this.width - 42 - 48, 10, 36, 36);
        GlStateManager.color(1f, 1f, 1f);
        Lime.fontManager.roboto_sense.drawString("Discord", this.width - 42 + 2.5f, 10 + 36, hover(this.width - 42, 10, mouseX, mouseY, 36, 42) ? new Color(200, 200, 200).getRGB() : -1);
        Lime.fontManager.roboto_sense.drawString("Changelog", this.width - 42 - 54 + 2.5f, 10 + 36, hover(this.width - 42 - 54, 10, mouseX, mouseY, 36, 42) ? new Color(200, 200, 200).getRGB() : -1);
        GL11.glPushMatrix();
        GlStateManager.scale(2, 2, 2);
        this.fontRendererObj.drawString("Meincampf™", (this.width / 2 - this.fontRendererObj.getStringWidth("Meincampf™")) / 2, (50 - this.fontRendererObj.FONT_HEIGHT) / 2, -1);
        GL11.glPopMatrix();
        Util2D.drawRoundedRect(this.width / 2 - 105, this.height / 2 - 70, 205, 110, 10, new Color(30, 30, 30).getRGB(), false);
        super.drawScreen(mouseX, mouseY, partialTicks);

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id){
            case 0:
                mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 1:
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 2:
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case 3:
                mc.displayGuiScreen(new GuiAltManager());
                break;
            case 4:
                System.exit(0);
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(hover(this.width - 42, 10, mouseX, mouseY, 36, 42) && mouseButton == 0){
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)){
                try{
                    desktop.browse(new URL("https://discord.gg/zaZRfHAac6").toURI());
                } catch (Exception ignored){}
            }
        }
        if(hover(this.width - 42 - 54, 10, mouseX, mouseY, 36, 42) && mouseButton == 0){
            mc.displayGuiScreen(new ChangelogManager(this));
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean hover(double x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
