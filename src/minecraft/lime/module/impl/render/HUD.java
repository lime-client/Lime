package lime.module.impl.render;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.Event2D;
import lime.module.Module;
import lime.utils.render.RainbowUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HUD extends Module {


    public HUD() {
        super("HUD", 0, Category.RENDER);
        Lime.setmgr.rSetting(new Setting("HUD Mode", this, "Simple", "Simple", "Sidebar", "Outline", "ZeroDay"));
        Lime.setmgr.rSetting(new Setting("HUD Color", this, "Dynamic", "Basic", "Dynamic", "Rainbow", "Chroma", "RandomRainbow", "Category"));
        Lime.setmgr.rSetting(new Setting("Rect Alpha", this, 0, 0, 255, true));
        Lime.setmgr.rSetting(new Setting("ArrayList", this, true));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


    @EventTarget
    public void on2D(Event2D e) {
        if(!mc.gameSettings.showDebugInfo) {
            int index = 0;
            int yCount = 0;
            int sexy = 0;
            Lime.moduleManager.filteredLengthModules.sort(new Comparator<Module>() {
                public int compare(Module m1, Module m2) {
                    if (Lime.fontManager.comfortaa_hud.getStringWidth(m1.getName()) > Lime.fontManager.comfortaa_hud.getStringWidth(m2.getName())) {
                        return -1;
                    }
                    if (Lime.fontManager.comfortaa_hud.getStringWidth(m1.getName()) < Lime.fontManager.comfortaa_hud.getStringWidth(m2.getName())) {
                        return 1;
                    }
                    return 0;
                }
            });
            int increment = Lime.fontManager.comfortaa_hud.FONT_HEIGHT + 5;
            ScaledResolution sr = new ScaledResolution(this.mc);
            for (Module m : Lime.moduleManager.getFilteredLengthModules()) {
                if (m.getAnim() != -1) {
                    Color color = new Color(255, 255, 255, 255);
                    switch(Lime.setmgr.getSettingByName("HUD Color").getValString().toUpperCase()){
                        case "DYNAMIC":
                            color = RainbowUtil.blend2colors(new Color(255, 0, 0, 255), new Color(100, 0, 0, 255), (System.nanoTime() + (index + index*100000000L * 2)) / 1.0E09F % 2.0F);
                            break;
                        case "BASIC":
                            color = new Color(100, 0, 0, 255);
                            break;
                        case "RAINBOW":
                            color = RainbowUtil.rainbow(1, 0.7F, 1);
                            break;
                        case "CHROMA":
                            color = RainbowUtil.rainbow(index + index * 70000000L, 0.7F, 1);
                            break;
                        case "RANDOMRAINBOW":
                            color = getRandomColor();
                            break;
                        case "CATEGORY":
                            color = getColorByCategory(m.getCat());
                    }
                    fix();
                    Gui.drawRect(sr.getScaledWidth() - m.getAnim() - 5, yCount, sr.getScaledWidth(), yCount + increment, new Color(0, 0, 0, (int) Lime.setmgr.getSettingByName("Rect Alpha").getValDouble()).getRGB());
                    switch(Lime.setmgr.getSettingByName("HUD Mode").getValString().toUpperCase()){
                        case "ZERODAY":
                            Gui.drawRect(sr.getScaledWidth() - m.getAnim() - 3, yCount, sr.getScaledWidth() - m.getAnim() - 5, yCount + increment, color.getRGB());
                            break;
                        case "SIMPLE":
                            break;
                        case "SIDEBAR":
                            Gui.drawRect(sr.getScaledWidth() - 1, yCount, sr.getScaledWidth(), yCount + increment, color.getRGB());
                            break;
                        case "OUTLINE":
                            Gui.drawRect(sr.getScaledWidth() - m.getAnim() - 3, yCount - 2, sr.getScaledWidth() - m.getAnim() - 5, yCount + increment, color.getRGB());
                            if(sexy != 0){
                                Gui.drawRect(sexy, yCount-2, sr.getScaledWidth() - m.getAnim() - 3, yCount, color.getRGB());
                            }
                            sexy = sr.getScaledWidth() - m.getAnim() - 3;
                            break;
                    }
                    Lime.fontManager.comfortaa_hud.drawString(m.getName(), sr.getScaledWidth() - m.getAnim() - 2, yCount + 4, color.getRGB());
                    index++;
                }

                for(int i = 0; i < 5; i++) {
                    if(m.isToggled()) {
                        if(m.getAnim() < Lime.fontManager.comfortaa_hud.getStringWidth(m.getName()) + 2) { m.setAnim(m.getAnim() + 1); }
                        if(m.getAnim() > Lime.fontManager.comfortaa_hud.getStringWidth(m.getName()) + 3) { m.setAnim((int)Lime.fontManager.comfortaa_hud.getStringWidth(m.getName())); }
                    }else {
                        if(m.getAnim() > -1) { m.setAnim(m.getAnim() - 1); }
                    }
                }
                yCount += Math.min(increment, m.getAnim() + 1);
            }
        }
    }




    public static double getSpeed() {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3 lastPos = new Vec3(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ);
        Vec3 pos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        return Math.abs(lastPos.distanceTo(pos) * 20d);
    }
    public Color getRandomColor(){
        return new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), 255);
    }
    public Color getColorByCategory(Category category){
        if(category == Category.COMBAT){
            return new Color(255, 0, 0, 255);
        }
        if(category == Category.MOVEMENT){
            return new Color(22, 222, 166, 255);
        }
        if(category == Category.PLAYER){
            return new Color(10, 68, 160, 255);
        }
        if(category == Category.RENDER){
            return new Color(174, 14, 231, 255);
        }
        if(category == Category.MISC){
            return new Color(221, 175, 29, 255);
        }
        return null;
    }








    public static void fix() {
        Gui.drawRect(0, 0, 0, 0, -1); //lol
    }
}
