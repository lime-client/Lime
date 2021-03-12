package lime.gui;

import lime.Lime;
import lime.ui.GuiNewButton;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ChangelogManager extends GuiScreen {
    public GuiScreen parentScreen;
    public ChangelogManager(GuiScreen p){
        parentScreen = p;
    }

    ArrayList<String> ver = new ArrayList<>();
    int indexVer = 0;

    @Override
    public void initGui() {
        this.buttonList.add(new GuiNewButton(1, this.width - 105, this.height - 32, 100, 20, "Back"));
        //this.buttonList.add(new GuiButton(2, this.width / 2 - 40, 45, 20, 20, "-"));
        //this.buttonList.add(new GuiButton(3, this.width / 2 + 40, 45, 20, 20, "+"));
        ver.add("b1");
        super.initGui();
    }
    int offset = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scroll();
        ArrayList<String> file = new ArrayList<String>(Arrays.asList(loadFile(getClass().getResourceAsStream("changelogs/b1.txt")).split("\n")));
        Collections.sort(file,new Comparator<String>(){
            public int compare(String m1, String m2) {
                if (Lime.fontManager.roboto_sense.getStringWidth(m1) > Lime.fontManager.roboto_sense.getStringWidth(m2)) {
                    return -1;
                }
                if (Lime.fontManager.roboto_sense.getStringWidth(m1) < Lime.fontManager.roboto_sense.getStringWidth(m2)) {
                    return 1;
                }
                return 0;
            }
        });
        Util2D.drawImage(new ResourceLocation("lime/wp.jpg"), 0, 0, this.width, this.height);
        Gui.drawRect(0, 0, this.width, 30, new Color(100, 100, 100, 20).getRGB());
        Lime.fontManager.roboto_sense.drawCenteredString("Changelog (b1)", this.width / 2, 15 - Lime.fontManager.roboto_sense.FONT_HEIGHT, -1);
        int yCount = 0;
        for(String str : file){
            GlStateManager.color(1f, 1f, 1f);
            Lime.fontManager.roboto_sense.drawString(str.replace("[+]", "§7[§a+§7]§f"), 1, yCount * 12 + 30, -1);
            yCount++;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    public void scroll(){
        if(Mouse.hasWheel()){
            int wheel = Mouse.getDWheel();
            if(wheel < 0)
                offset += 10;
            else
                offset -= 10;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 1){
            mc.displayGuiScreen(parentScreen);
        }
        super.actionPerformed(button);
    }

    public String loadFile(InputStream inputStream){
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (Exception ignored) {

        }
        return resultStringBuilder.toString();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1){
            mc.displayGuiScreen(parentScreen);
        }
        super.keyTyped(typedChar, keyCode);
    }
}
