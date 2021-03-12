package lime.module.impl.combat;

import lime.Lime;
import lime.events.EventTarget;
import lime.events.impl.Event2D;
import lime.events.impl.Event3D;
import lime.events.impl.EventMotion;
import lime.events.impl.EventPacket;
import lime.module.Module;
import lime.cgui.settings.Setting;
import lime.module.impl.render.targethuds.AstolfoTargetHUD;
import lime.utils.*;
import lime.utils.movement.MovementUtil;
import lime.utils.render.RainbowUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;


public class KillAura extends Module {
    public KillAura(){
        super("KillAura", Keyboard.KEY_R, Category.COMBAT);
        ArrayList<String> type = new ArrayList<>();
        type.add("PRE");
        type.add("POST");
        ArrayList<String> rotations = new ArrayList<>();
        rotations.add("BRWServ");
        rotations.add("NCP");
        Lime.setmgr.rSetting(new Setting("Rotations", this, "NCP", rotations));
        Lime.setmgr.rSetting(new Setting("Type", this, "PRE", type));
        Lime.setmgr.rSetting(new Setting("Reach", this, 4.2, 2.5, 7, false));
        Lime.setmgr.rSetting(new Setting("CPS", this, 8, 1, 20, true));
        Lime.setmgr.rSetting(new Setting("Players", this, true));
        Lime.setmgr.rSetting(new Setting("Mobs", this, true));
        Lime.setmgr.rSetting(new Setting("Animals", this, false));
        Lime.setmgr.rSetting(new Setting("Villagers", this, false));
        Lime.setmgr.rSetting(new Setting("Invisibles", this, false));
        Lime.setmgr.rSetting(new Setting("Through Walls", this, true));
        Lime.setmgr.rSetting(new Setting("AutoBlock", this, false));
        Lime.setmgr.rSetting(new Setting("TargetInfo", this, true));
        Lime.setmgr.rSetting(new Setting("KeepSprint", this, true));
        Lime.setmgr.rSetting(new Setting("NoSwing", this, false));

    }
    float time;
    EntityLivingBase ent;
    private AstolfoTargetHUD astolfoTargetHUD = new AstolfoTargetHUD();
    Timer cps = new Timer();
    @Override
    public void onEnable() {
        astolfoTargetHUD.resetHealthAnimated();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    @EventTarget
    public void onMotion(EventMotion eventMotion){
        if(Lime.setmgr.getSettingByNameAndMod("Type", this).getValString().equalsIgnoreCase("PRE") && eventMotion.getState() == EventMotion.State.PRE){
            ;
        } else if(Lime.setmgr.getSettingByNameAndMod("Type", this).getValString().equalsIgnoreCase("POST") && eventMotion.getState() == EventMotion.State.POST){
            ;
        } else
            return;
        ent = getBestEntityByDistance();
        if(Lime.moduleManager.getModuleByName("AntiBot").isToggled()){
            AntiBot antiBot = (AntiBot) Lime.moduleManager.getModuleByName("AntiBot");
            antiBot.removeBot();
        }
        if(Lime.moduleManager.getModuleByName("AutoPot").isToggled() && (AutoPot.doPot || AutoPot.doSp)) return;
        if(ent == null || ent.ticksExisted < 30 || (!mc.thePlayer.canEntityBeSeen(ent) && !Lime.setmgr.getSettingByNameAndMod("Through Walls", this).getValBoolean()) || mc.thePlayer.getDistanceToEntity(ent) > Lime.setmgr.getSettingByNameAndMod("Reach", this).getValDouble())
            return;
        if(mc.getIntegratedServer() == null){
            if(mc.getCurrentServerData().serverName.toLowerCase().contains("cubecraft")){
                if(ent.posY > ent.posY + 3){
                    return;
                }
            }
        }

        float rots[] = null;
        switch(Lime.setmgr.getSettingByNameAndMod("Rotations", this).getValString()){
            case "NCP":
                rots = getRotations2(ent);
                break;
            case "BRWServ":
                rots = getRotationsEntity(ent);
                break;
        }
        eventMotion.setYaw(rots[0]);
        eventMotion.setPitch(rots[1]);
        if(mc.gameSettings.thirdPersonView != 0){
            mc.thePlayer.rotationYawHead = eventMotion.getYaw();
            mc.thePlayer.renderYawOffset = eventMotion.getYaw();
            mc.thePlayer.renderArmYaw = eventMotion.getYaw();
            mc.thePlayer.renderArmPitch = eventMotion.getPitch();
            mc.thePlayer.rotationPitchHead = eventMotion.getPitch();
        }
        if(Lime.setmgr.getSettingByNameAndMod("AutoBlock", this).getValBoolean() && (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)){
            mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 71626);
        }
        if(cps.hasReached((int) (20 / Lime.setmgr.getSettingByNameAndMod("CPS", this).getValDouble() * 50))){
            if(Lime.setmgr.getSettingByNameAndMod("KeepSprint", this).getValBoolean())
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ent, C02PacketUseEntity.Action.ATTACK));
            else
                mc.playerController.attackEntity(mc.thePlayer, ent);
            if(Lime.setmgr.getSettingByNameAndMod("NoSwing", this).getValBoolean())
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            else
                mc.thePlayer.swingItem();
            cps.reset();
        }
    }
    public EntityLivingBase getBestEntityByDistance() {
        ArrayList<EntityLivingBase> entity = new ArrayList<>();
        entity.clear();
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (e instanceof EntityLivingBase) {
                EntityLivingBase ent = (EntityLivingBase) e;
                if (isValid(ent) && e != mc.thePlayer) {
                    entity.add(ent);
                }
            }
        }
        if (entity.isEmpty()) {
            return null;
        }
        entity.sort(Comparator.comparingDouble(player -> mc.thePlayer.getDistanceToEntity(player)));
        return entity.get(0);
    }
    public boolean isValid(Entity ee){
        if(ee == null)
            return false;
        if(ee instanceof EntityPlayer && Lime.setmgr.getSettingByNameAndMod("Players", this).getValBoolean())
            return true;
        if(ee instanceof EntityMob)
            return true;
        if(ee instanceof EntityAnimal && Lime.setmgr.getSettingByNameAndMod("Animals", this).getValBoolean())
            return true;
        if(ee instanceof EntityPlayer && Lime.setmgr.getSettingByNameAndMod("Villagers", this).getValBoolean())
            return true;
        if(ee instanceof EntityPlayer && Lime.setmgr.getSettingByNameAndMod("Invisibles", this).getValBoolean())
            return true;
        return false;

    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double)player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = (double) MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0D / 3.141592653589793D));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsEntity(EntityLivingBase entity) {
        return MovementUtil.isMoving() ? getRotations(entity.posX + randomNumber(0.03D, -0.03D), entity.posY + (double)entity.getEyeHeight() - 0.4D + randomNumber(0.07D, -0.07D), entity.posZ + randomNumber(0.03D, -0.03D)) : getRotations(entity.posX, entity.posY + (double)entity.getEyeHeight() - 0.4D, entity.posZ);
    }
    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    ScaledResolution sr;

    public static void drawFace(int x, int y, int width, int height, AbstractClientPlayer target) {
        try {
            ResourceLocation skin = target.getLocationSkin();
            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
            //355, 190, 8.0f, 8.0f, 8, 8, 28, 28, 64.0f, 64.0f
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean down;
    @EventTarget
    public void onRender2D(Event2D event2D){
        if(ent != null){
            if(ent.canEntityBeSeen(mc.thePlayer) ||(!ent.canEntityBeSeen(mc.thePlayer) && getSettingByName("Through Walls").getValBoolean()))
                if(ent.getDistanceToEntity(mc.thePlayer) <= getSettingByName("Reach").getValDouble())
                    if(isValid(ent) && ent.ticksExisted > 30){
                        ScaledResolution sr = new ScaledResolution(this.mc);
                        try{
                            astolfoTargetHUD.draw(ent, sr.getScaledWidth() / 2 - 87, sr.getScaledHeight() / 2 + 100, getHealthColor(Math.round(ent.getHealth())).getRGB());
                        } catch (Exception ignored){

                        }
                    }
        }
    }
    public Color getHealthColor(int health){
        if(health >= 20)
            return new Color(0, 250, 0);
        else if(health >= 15)
            return new Color(250, 200, 0);
        else if(health >= 10)
            return new Color(250, 88, 0);
        else if(health >= 5)
            return new Color(100, 0, 0);
        return null;
    }

            /*if(ent == null || (!Lime.setmgr.getSettingByNameAndMod("Through Walls", this).getValBoolean() && !mc.thePlayer.canEntityBeSeen(ent))  || (mc.thePlayer.getDistanceToEntity(ent) > Lime.setmgr.getSettingByNameAndMod("Reach", this).getValDouble()) || !isValid(ent)) return;
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        sr = new ScaledResolution(Minecraft.getMinecraft());
        Util2D.drawRoundedRect(sr.getScaledWidth() / 2 - 95, sr.getScaledHeight() - 150, sr.getScaledWidth() / 2 + 95, sr.getScaledHeight() - 75, new Color(0, 0, 0, 255).getRGB(),  new Color(0, 0, 0, 200).getRGB());
        GL11.glPushMatrix();
        if(ent instanceof EntityPlayer){
            drawFace(sr.getScaledWidth() / 2 - 95 + 3, sr.getScaledHeight() - 150 + 3, 60, 60, (AbstractClientPlayer) ent);
        } else {
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            drawEntityOnScreen(sr.getScaledWidth() / 2 - 95 + 30, sr.getScaledHeight() - 150 + 60, 30, 0, 0, ent);
        }
        GL11.glPopMatrix();
        NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(ent.getUniqueID());
        String ping = (networkPlayerInfo == null) ? "0 ms" : (networkPlayerInfo.getResponseTime() + " ms");
        Lime.fontManager.comfortaa_hud.drawString("Ping: " + ping, sr.getScaledWidth() / 2 - 25, sr.getScaledHeight() - 150 + 20, new Color(255, 255, 255, 255).getRGB());
        Lime.fontManager.comfortaa_hud.drawString("Ground: " + ent.onGround, sr.getScaledWidth() / 2 - 25, sr.getScaledHeight() - 150 + 40, new Color(255, 255, 255, 255).getRGB());
        Lime.fontManager.comfortaa_hud.drawString(translateName(ent), sr.getScaledWidth() / 2 - 25, sr.getScaledHeight() - 150 + 10, new Color(255, 255, 255, 255).getRGB());
        Lime.fontManager.comfortaa_hud.drawString("Distance: " + Math.ceil(mc.thePlayer.getDistanceToEntity(ent)), sr.getScaledWidth() / 2 - 25, sr.getScaledHeight() - 150 + 30, new Color(255, 255, 255, 255).getRGB());
        String f = "";
        if(ent.getHealth() > mc.thePlayer.getHealth()){ f = "Losing";} else if(ent.getHealth() < mc.thePlayer.getHealth()) {f = "Winning";} else {f  = "Same health bruh";};
        Lime.fontManager.comfortaa_hud.drawString(f, sr.getScaledWidth() / 2 - 25, sr.getScaledHeight() - 150 + 50, new Color(255, 255, 255, 255).getRGB());
        if(ent instanceof EntityPlayer){
            // DRAW ARMOR ETC
        }
        // DRAW HEALTH
        double health = ent.getHealth() * ((ent instanceof EntityPlayer) || !((ent instanceof EntityMob)) ? 2.5 : 0);
        try{
            Util2D.drawRoundedRect(sr.getScaledWidth() / 2 - 95, sr.getScaledHeight() - 75 - 3, (float) (sr.getScaledWidth() / 2 - 95 + (ent.getHealth() * 9.5)), sr.getScaledHeight() - 75, getHealthColor(ent.getHealth()).getRGB(), getHealthColor(ent.getHealth()).getRGB());
        } catch (Exception ignored){}
            public static Color getHealthColor(float health){
        if(health >= 20){
            return new Color(0, 200, 0, 255);
        } else if(health >= 15){
            return new Color(221, 89, 40, 255);
        } else if(health >= 10){
            return new Color(241, 208, 55, 255);
        }else if(health >= 5){
            return new Color(131, 24, 39, 255);
        }
        return null;
    }

    public String translateName(Entity ent){
        if(ent instanceof EntityPlayer){
            return ent.getName();
        } else if(ent instanceof EntityAnimal) {
            return "Animal";
        }  else if(ent instanceof EntityMob){
            return "Mob";
        } else {
            return "wtf is that";
        }
    }*/


    public static float[] getRotations2(EntityLivingBase ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition2(x, z, y);
    }
    public static float[] getRotationFromPosition2(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    @EventTarget
    public void flagCheck(EventPacket e){
        if(e.getPacket() instanceof S08PacketPlayerPosLook){
            ChatUtils.sendMsg("Disabled " + this.name + " for lagback reasons");
            this.toggle();
        }
    }

    @EventTarget
    public void onRender3D(Event3D event3D){
        if(ent == null || ent.ticksExisted < 30 || (!mc.thePlayer.canEntityBeSeen(ent) && !getSettingByName("Through Walls").getValBoolean()) || mc.thePlayer.getDistanceToEntity(ent) > getSettingByName("Reach").getValDouble())
            return;
        try{
            Color color = RainbowUtil.blend2colors(new Color(255, 0, 0, 255), new Color(100, 0, 0, 255), (System.nanoTime() + (100000000L * 2)) / 1.0E09F % 2.0F);
            time += .01 * (Lime.deltaTime * .1);
            final double height = 0.5 * (1 + Math.sin(2 * Math.PI * (time * .3)));

            if (height > .995) {
                down = true;
            } else if (height < .01) {
                down = false;
            }

            final double x = ent.posX + (ent.posX - ent.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
            final double y = ent.posY + (ent.posY - ent.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
            final double z = ent.posZ + (ent.posZ - ent.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;

            GlStateManager.enableBlend();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GL11.glLineWidth(1.5F);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glDisable(GL11.GL_CULL_FACE);
            final double size = ent.width * 1.2;
            final double yOffset = ((ent.height * (1)) + .2) * height;
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            {
                for (int j = 0; j < 361; j++) {
                    color(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (down ? 255 * height : 255 * (1 - height))));
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size, y + yOffset, z - Math.sin(Math.toRadians(j)) * size);
                    color(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
                }
            }
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                for (int j = 0; j < 361; j++) {
                    color(color);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size, y + yOffset, z - Math.sin(Math.toRadians(j)) * size);
                }
            }
            GL11.glEnd();
            GlStateManager.enableAlpha();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.resetColor();
            return;
        } catch (Exception ignored){}
    }
    public static final void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static final void color(double red, double green, double blue) {
        color(red, green, blue, 1);
    }

    public static final void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }
}
