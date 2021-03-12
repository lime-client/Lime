package lime.module.impl.render;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.Event2D;
import lime.module.Module;
import lime.module.impl.combat.AutoPot;
import lime.utils.render.RainbowUtil;
import lime.utils.render.Util2D;
import lime.utils.render.UtilGL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;
import viamcp.utils.Util;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

public class HUD extends Module {
    public HUD(){
        super("HUD", 0, Category.RENDER);
        Lime.setmgr.rSetting(new Setting("Mode", this, "Simple", "Simple", "Sidebar", "Outline", "ZeroDay"));
        Lime.setmgr.rSetting(new Setting("Color", this, "Dynamic", "Basic", "Dynamic", "Gradient", "Rainbow", "Chroma", "RandomRainbow", "Category"));
        Lime.setmgr.rSetting(new Setting("String Shadow", this, true));
        Lime.setmgr.rSetting(new Setting("Custom Font", this, true));
        Lime.setmgr.rSetting(new Setting("Show Mode", this, true));

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
        hud();
    }

    public void hud(){
        if(mc.gameSettings.showDebugInfo) return;
        ScaledResolution sr = new ScaledResolution(this.mc);
        int index = 0;
        int increment = Lime.fontManager.comfortaa_hud.FONT_HEIGHT + 2;
        if(!getSettingByName("Custom Font").getValBoolean()) increment = mc.fontRendererObj.FONT_HEIGHT + 2;
        int yCount = 1;
        String ip = mc.getIntegratedServer() != null ? "Singleplayer" : mc.getCurrentServerData().serverIP;

        if(mc.currentScreen == null && !(mc.currentScreen instanceof GuiChat)) {
            if(getSettingByName("Custom Font").getValBoolean()){
                Lime.fontManager.roboto_sense.drawString("Lime | b1 | " + ip, 4, 4, getColor(null, 0).getRGB());
                Lime.fontManager.roboto_sense.drawString("FPS: " + EnumChatFormatting.GRAY + " " + Minecraft.getDebugFPS(), 4, sr.getScaledHeight() - Lime.fontManager.roboto_sense.FONT_HEIGHT - 2, new Color(255, 255, 255).getRGB());
                Lime.fontManager.roboto_sense.drawString("B/S: " + EnumChatFormatting.GRAY + " " + Math.round(getSpeed()), 4, sr.getScaledHeight() - (Lime.fontManager.roboto_sense.FONT_HEIGHT * 2) - 2, new Color(255, 255, 255).getRGB());
            } else {
                mc.fontRendererObj.drawString("Lime | b1 | " + ip, 4, 4, getColor(null, 0).getRGB());
                mc.fontRendererObj.drawString("FPS: " + EnumChatFormatting.GRAY + " " + Minecraft.getDebugFPS(), 4, sr.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2, new Color(255, 255, 255).getRGB());
                mc.fontRendererObj.drawString("B/S: " + EnumChatFormatting.GRAY + " " + Math.round(getSpeed()), 4, sr.getScaledHeight() - (mc.fontRendererObj.FONT_HEIGHT * 2) - 2, new Color(255, 255, 255).getRGB());
            }
        }
        if(getSettingByName("Custom Font").getValBoolean()){
            Collections.sort(Lime.moduleManager.getModules(),new Comparator<Module>() {
                public int compare(Module m1, Module m2) {
                    if (Lime.fontManager.roboto_sense.getStringWidth(m1.getDisplayName()) > Lime.fontManager.roboto_sense.getStringWidth(m2.getDisplayName())) {
                        return -1;
                    }
                    if (Lime.fontManager.roboto_sense.getStringWidth(m1.getDisplayName()) < Lime.fontManager.roboto_sense.getStringWidth(m2.getDisplayName())) {
                        return 1;
                    }
                    return 0;
                }
            });
        } else {
            Collections.sort(Lime.moduleManager.getModules(),new Comparator<Module>() {
                public int compare(Module m1, Module m2) {
                    if (mc.fontRendererObj.getStringWidth(m1.getDisplayName()) > mc.fontRendererObj.getStringWidth(m2.getDisplayName())) {
                        return -1;
                    }
                    if (mc.fontRendererObj.getStringWidth(m1.getDisplayName()) < mc.fontRendererObj.getStringWidth(m2.getDisplayName())) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
        GL11.glPushMatrix();
        GlStateManager.translate(0, 2, 0);
        for(Module mod : Lime.moduleManager.getModules()){
            if(mod.name.equals("HUD")) continue;
            if(getSettingByName("Show Mode").getValBoolean()){
                if(mod.hasSettings() && mod.getSettingByName("Mode") != null){
                    mod.setDisplayName(mod.getName() + " " + EnumChatFormatting.GRAY + "- " + mod.getSettingByName("Mode").getValString());
                } else if(mod.getSettingByName(mod.getName()) != null){
                    mod.setDisplayName(mod.getName() + " " + EnumChatFormatting.GRAY + "- " + mod.getSettingByName(mod.getName()).getValString());
                } else if(mod.getName().equalsIgnoreCase("autopot")){
                    mod.setDisplayName(mod.getName() + " " + EnumChatFormatting.GRAY + "- " + AutoPot.getCount());
                } else {
                    mod.setDisplayName(mod.getName());
                }
            } else {
                mod.setDisplayName(mod.getName());
            }
            Color color = getColor(mod, index);

            if(mod.isToggled()) {
                if(getSettingByName("Custom Font").getValBoolean())
                    Lime.fontManager.roboto_sense.drawString(mod.getDisplayName(), sr.getScaledWidth() - mod.getAnim(), yCount, color.getRGB(), false);
                else
                    mc.fontRendererObj.drawString(mod.getDisplayName(), sr.getScaledWidth() - mod.getAnim(), yCount, color.getRGB(), getSettingByName("String Shadow").getValBoolean());
                yCount += Math.min(increment, mod.getAnim() + 1);
                index++;
            }


            for(int i = 0; i < 5; i++) {
                if(mod.isToggled()) {
                    if(getSettingByName("Custom Font").getValBoolean()){
                        if(mod.getAnim() < Lime.fontManager.roboto_sense.getStringWidth(mod.getDisplayName()) + 2) { mod.setAnim(mod.getAnim() + 0.1f); }
                        if(mod.getAnim() > Lime.fontManager.roboto_sense.getStringWidth(mod.getDisplayName()) + 3) { mod.setAnim((int)Lime.fontManager.roboto_sense.getStringWidth(mod.getDisplayName())); }
                    } else {
                        if(mod.getAnim() < mc.fontRendererObj.getStringWidth(mod.getDisplayName()) + 2) { mod.setAnim(mod.getAnim() + 0.1f); }
                        if(mod.getAnim() > mc.fontRendererObj.getStringWidth(mod.getDisplayName()) + 3) { mod.setAnim(mc.fontRendererObj.getStringWidth(mod.getDisplayName())); }
                    }

                }else {
                    if(mod.getAnim() > -1) { mod.setAnim(mod.getAnim() - 0.1f); }
                }
            }



        }
        GL11.glPopMatrix();
    }

    public static double getSpeed() {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3 lastPos = new Vec3(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ);
        Vec3 pos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        return Math.abs(lastPos.distanceTo(pos) * 20d);
    }

    public Color getColor(Module m, int index){
        Color color = new Color(255, 255, 255);
        switch(getSettingByName("Color").getValString().toUpperCase()){
            case "DYNAMIC":
                color = RainbowUtil.blend2colors(new Color(255, 0, 0, 255), new Color(100, 0, 0, 255), (System.nanoTime() + (index + index*100000000L * 2)) / 1.0E09F % 2.0F);
                break;
            case "GRADIENT":
                int gradSpeed = 1;
                Color fc = new Color(235, 100, 210);
                Color sc = new Color(40, 117, 230);
                Color tc = new Color(200, 25, 165);
                color = RainbowUtil.getGradient((float) (System.nanoTime() + (index + index + index*100000000L * gradSpeed)) / 1.0E09F % 1.0F, fc, sc, tc);
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
        return color;
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
}
