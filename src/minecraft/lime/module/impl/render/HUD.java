package lime.module.impl.render;

import lime.Lime;
import lime.settings.impl.BooleanValue;
import lime.settings.impl.ColorPicker;
import lime.settings.impl.ListValue;
import lime.settings.impl.SlideValue;
import lime.events.EventTarget;
import lime.events.impl.Event2D;
import lime.events.impl.EventScoreboard;
import lime.module.Module;
import lime.utils.render.RainbowUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HUD extends Module {
    ListValue mode = new ListValue("Mode", this, "Basic", "Basic", "Moon");
    ListValue watermark = new ListValue("Watermark", this, "Lime", "None", "Lime", "Novoline");
    ListValue sortBy = new ListValue("Sort By", this, "Length", "Length", "Alphabetic");
    ListValue sidebar = new ListValue("Sidebar", this, "None", "None", "Left", "Right");
    ListValue color = new ListValue("Color", this, "Gradient", "Basic", "Dynamic", "Gradient", "Rainbow", "Raiinnbow");
    public ListValue targetHudMode = new ListValue("TargetHUD", this, "Astolfo", "None", "Astolfo");
    public SlideValue targetHudX = new SlideValue("TargetHUD X", this, 50, 0, 100, true);
    public SlideValue targetHudY = new SlideValue("TargetHUD Y", this, 50, 0, 100, true);
    BooleanValue arrayList = new BooleanValue("ArrayList", this, true);
    BooleanValue arrayListScoreboardFix = new BooleanValue("Scoreboard Fix", this, true);
    BooleanValue arrayListSuffix = new BooleanValue("ArrayList Suffix", this, false);
    BooleanValue keystrokes = new BooleanValue("KeyStrokes", this, false);
    BooleanValue customFont = new BooleanValue("Custom Font", this, true);
    BooleanValue stringShadow = new BooleanValue("String Shadow", this, true);
    ColorPicker colorPicker = new ColorPicker("Color 1", this, new Color(100, 0, 0).getRGB());
    ColorPicker colorPicker2 = new ColorPicker("Color 2", this, new Color(255, 0, 0).getRGB());
    ColorPicker colorPicker3 = new ColorPicker("Color 3", this, new Color(200, 0, 0).getRGB());

    public HUD(){
        super("HUD", 0, Category.RENDER);
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
    public void on2D(Event2D e){
        hud(e.getWidth(), e.getHeight());
    }

    public void hud(float width, float height){
        if(mc.gameSettings.showDebugInfo) return;
        if(watermark.getValue().equalsIgnoreCase("Lime")){
            String ip = mc.getIntegratedServer() != null ? "Singleplayer" : mc.getCurrentServerData().serverIP;
            if(customFont.getValue())
                Lime.fontManager.roboto_sense.drawString("Lime | b1 | " + ip, 3, 3, getColor(0).getRGB(), false);
            else
                mc.fontRendererObj.drawString("Lime | b1 | " + ip, 3, 3, getColor(0).getRGB(), stringShadow.getValue());
        } else if(watermark.getValue().equalsIgnoreCase("Novoline")){
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("HH:mm");
            if(customFont.getValue())
                Lime.fontManager.roboto_sense.drawString("L" + EnumChatFormatting.WHITE + "ime " + EnumChatFormatting.GRAY + "(" + EnumChatFormatting.WHITE + df.format(date) + EnumChatFormatting.GRAY + ")", 3, 3, getColor(0).getRGB(), false);
            else
                mc.fontRendererObj.drawString("L" + EnumChatFormatting.WHITE + "ime " + EnumChatFormatting.GRAY + "(" + EnumChatFormatting.WHITE + df.format(date) + EnumChatFormatting.GRAY + ")", 3, 3, getColor(0).getRGB(), false);
        }

        if(arrayList.getValue()){
            ArrayList<Module> mods = new ArrayList<>(Lime.moduleManager.getModules());
            switch(sortBy.getValue().toLowerCase()){
                case "alphabetic":
                    Collections.sort(mods, new Comparator<Module>() {
                        @Override
                        public int compare(Module m1, Module m2) {
                            String s1 = m1.getName();
                            String s2 = m2.getName();
                            return s1.compareToIgnoreCase(s2);
                        }
                    });
                    break;
                case "length":
                    Collections.sort(mods, new Comparator<Module>() {
                        @Override
                        public int compare(Module m1, Module m2) {
                            if(customFont.getValue()){
                                if (Lime.fontManager.roboto_sense.getStringWidth(m1.getDisplayName()) > Lime.fontManager.roboto_sense.getStringWidth(m2.getDisplayName()))
                                    return -1;
                                if (Lime.fontManager.roboto_sense.getStringWidth(m1.getDisplayName()) < Lime.fontManager.roboto_sense.getStringWidth(m2.getDisplayName()))
                                    return 1;
                            } else {
                                if (mc.fontRendererObj.getStringWidth(m1.getDisplayName()) > mc.fontRendererObj.getStringWidth(m2.getDisplayName()))
                                    return -1;
                                if (mc.fontRendererObj.getStringWidth(m1.getDisplayName()) < mc.fontRendererObj.getStringWidth(m2.getDisplayName()))
                                    return 1;
                            }
                            return 0;
                        }
                    });
                    break;
            }
            int yCount = 0;
            int index = 0;
            GL11.glPushMatrix();
            if(mode.getValue().equalsIgnoreCase("Moon")){
                GlStateManager.translate(-10, 11, 0);
                Gui.drawRect(width - (customFont.getValue() ? Lime.fontManager.roboto_sense.getStringWidth(mods.get(0).getDisplayName()) : mc.fontRendererObj.getStringWidth(mods.get(0).getDisplayName())) - 3, -2, width, -1, getColor(0).getRGB());
                Gui.drawRect(width - (customFont.getValue() ? Lime.fontManager.roboto_sense.getStringWidth(mods.get(0).getDisplayName()) : mc.fontRendererObj.getStringWidth(mods.get(0).getDisplayName())) - 3, -2, width, -1, getColor(0).getRGB());
            } else
                GlStateManager.translate(-2, 3, 0);
            for(Module m : mods){
                if(m.getName().equalsIgnoreCase("hud")) continue;
                if(m.isToggled()){
                    m.setDisplayName(m.getName());
                    if(!m.suffix.equalsIgnoreCase("")) m.setDisplayName(m.getName() + " §7- " + m.suffix);
                    if(sidebar.getValue().equalsIgnoreCase("Right")){
                        Gui.drawRect(width, yCount * 12 - 2, width + 1, yCount * 12 + 10, getColor(index).getRGB());
                        Gui.drawRect(width, yCount * 12 - 2, width + 1, yCount * 12 + 10, getColor(index).getRGB());
                    }
                    if(customFont.getValue())
                        Lime.fontManager.roboto_sense.drawString(m.getDisplayName(), width - Lime.fontManager.roboto_sense.getStringWidth(m.getDisplayName()) - 2, yCount * 12 + (float) 0.5, getColor(index).getRGB());
                    else
                        mc.fontRendererObj.drawString(m.getDisplayName(), width - mc.fontRendererObj.getStringWidth(m.getDisplayName()) - 2, yCount * 12 + (float) 0.5, getColor(index).getRGB(), stringShadow.getValue());
                    yCount += 1;
                    index++;
                }
            }
            GL11.glPopMatrix();
        }
    }


    public Color getColor(int index){
        Color coloro = null;
        switch(color.getValue().toLowerCase()){
            case "basic":
                coloro = new Color(colorPicker.getValue());
                break;
            case "dynamic":
                coloro = RainbowUtil.blend2colors(new Color(colorPicker.getValue()), new Color(colorPicker2.getValue()), (System.nanoTime() + (index + index * 100000000L * 2)) / 1.0E09F % 2.0F);
                break;
            case "gradient":
                coloro = RainbowUtil.getGradient((float) (System.nanoTime() + (index + index + index*100000000L * 1)) / 1.0E09F % 1.0F, colorPicker.getColorValue(), colorPicker2.getColorValue(), colorPicker3.getColorValue());
                break;
            case "raiinnbow":
                coloro = new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255));
                break;
            case "rainbow":
                coloro =  RainbowUtil.rainbow(index + index * 70000000L, 0.7F, 1);
                break;
        }
        return coloro;
    }


    @EventTarget
    public void onScoreboard(EventScoreboard e){
        if(arrayListScoreboardFix.getValue()){
            int size = (int) Lime.moduleManager.getModules().stream().filter(module -> module.isToggled()).count();
            e.setY(size * 4);
        }
    }
}
