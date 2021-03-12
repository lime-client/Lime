package lime.module.impl.combat;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.Event2D;
import lime.events.impl.Event3D;
import lime.events.impl.EventMotion;
import lime.module.Module;
import lime.module.impl.render.targethuds.AstolfoTargetHUD;
import lime.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class KillAuraNew extends Module {
    public KillAuraNew(){
        super("KillAuraNew", 0, Category.COMBAT);
        Lime.setmgr.rSetting(new Setting("State", this, "PRE", "PRE", "POST"));
        Lime.setmgr.rSetting(new Setting("AutoBlock Reach", this, 6, 2.6, 12, false));
        Lime.setmgr.rSetting(new Setting("Reach", this, 4.2, 2.6, 7, false));
        Lime.setmgr.rSetting(new Setting("CPS", this, 8, 1, 20, true));
        Lime.setmgr.rSetting(new Setting("Players", this, true));
        Lime.setmgr.rSetting(new Setting("Animals", this, false));
        Lime.setmgr.rSetting(new Setting("Villagers", this, false));
        Lime.setmgr.rSetting(new Setting("Mobs", this, true));
        Lime.setmgr.rSetting(new Setting("Through Walls", this, true));
        Lime.setmgr.rSetting(new Setting("KeepSprint", this, true));
        Lime.setmgr.rSetting(new Setting("AutoBlock", this, true));
        Lime.setmgr.rSetting(new Setting("TargetInfo", this, true));
        Lime.setmgr.rSetting(new Setting("ESP", this, true));
        Lime.setmgr.rSetting(new Setting("Teams", this, true));
    }
    public static EntityLivingBase entity;
    public boolean validEntity = false;
    public static boolean attacking = false;
    public Timer cps = new Timer();
    public AstolfoTargetHUD astolfoTargetHUD = new AstolfoTargetHUD();

    @Override
    public void onEnable() {
        astolfoTargetHUD.resetHealthAnimated();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        entity = null;
        attacking = false;
        unblock();
        super.onDisable();
    }

    @EventTarget
    public void onMotion(EventMotion e){
        double reach = getSettingByName("Reach").getValDouble();
        boolean block = getSettingByName("AutoBlock").getValBoolean();
        double blockReach = getSettingByName("AutoBlock Reach").getValDouble();
        boolean keepSprint = getSettingByName("KeepSprint").getValBoolean();
        boolean throughWalls = getSettingByName("Through Walls").getValBoolean();
        EntityLivingBase entity = getBestEntity(reach, e);
        boolean isValid = isValid(entity, e);
        validEntity = isValid;
        int cps = (int) (20 / getSettingByName("CPS").getValDouble() * 50);

        if(isValid)
            attack(e, entity, reach, true, cps, block, blockReach, throughWalls, false, keepSprint);
    }

    @EventTarget
    public void on2D(Event2D e){
        if(entity != null && validEntity && getSettingByName("TargetInfo").getValBoolean()){
            targetInfo(e, entity, getSettingByName("Reach").getValDouble(), true);
        }

    }

    @EventTarget
    public void on3D(Event3D e){
        if(entity != null && validEntity && getSettingByName("ESP").getValBoolean())
            esp3d(e, entity, getSettingByName("Reach").getValDouble(), true);
    }

    public EntityLivingBase getBestEntity(double maxReach, EventMotion e){
        ArrayList<Entity> entities = new ArrayList<>();
        for(Entity ent : mc.theWorld.loadedEntityList){
            if(ent instanceof EntityLivingBase && isValid((EntityLivingBase) ent, e) && mc.thePlayer.getDistanceToEntity(ent) <= maxReach)
                entities.add(ent);

        }
        if(entities.isEmpty()) return null;
        entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
        return (EntityLivingBase) entities.get(0);
    }

    public void attack(EventMotion e, EntityLivingBase ent, double maxReach, boolean isValid, int cps, boolean block, double blockReach, boolean throughWalls, boolean noSwing, boolean keepSprint){
        if(ent.getDistanceToEntity(ent) <= maxReach && isValid && ((ent).canEntityBeSeen(mc.thePlayer) || (!(ent).canEntityBeSeen(mc.thePlayer) && throughWalls))){
            {
                AntiBot antiBot = (AntiBot) Lime.moduleManager.getModuleByName("AntiBot");
                if(antiBot.isToggled() && antiBot.skipEntity(ent)){
                    return;
                }
            }
            entity = ent;
            float[] rotations = getRotations(ent);
            e.setYaw(rotations[0]);
            e.setPitch(rotations[1]);
            if(mc.gameSettings.thirdPersonView != 0){
                doRotationsInThirdPerson(e);
            }
            if(this.cps.hasReached(cps)){
                if(noSwing)
                    mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                else
                    mc.thePlayer.swingItem();
                if(keepSprint)
                    mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(ent, C02PacketUseEntity.Action.ATTACK));
                else
                    mc.playerController.attackEntity(mc.thePlayer, ent);
                this.cps.reset();
            }
        }
        if(ent.getDistanceToEntity(ent) <= blockReach && isValid)
            if(block)
                this.block();
    }
    public void block(){
        if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword){
            mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 71626);
        }
    }
    public void unblock(){
        if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword){
            mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), -1);
        }
    }

    public void targetInfo(Event2D e, EntityLivingBase ent, double maxReach, boolean isValid){
        ScaledResolution sr = new ScaledResolution(this.mc);
        if(isValid && mc.thePlayer.getDistanceToEntity(ent) <= maxReach){
            {
                AntiBot antiBot = (AntiBot) Lime.moduleManager.getModuleByName("AntiBot");
                if(antiBot.isToggled() && antiBot.skipEntity(ent)){
                    return;
                }
            }
            Render.doRenderNameTags = false;
            Gui.drawRect(sr.getScaledWidth() / 2 - 87, sr.getScaledHeight() /2 + 100, sr.getScaledWidth() / 2 - 87 + 174, sr.getScaledHeight() / 2 + 170, 0xAA000000);
            astolfoTargetHUD.draw(ent, sr.getScaledWidth() / 2 - 87, sr.getScaledHeight() / 2 + 100, getHealthColor(Math.round(ent.getHealth())).getRGB());
            Render.doRenderNameTags = true;
        }

    }

    public Color getHealthColor(int health){
        if(health >= 20)
            return new Color(0, 250, 0);
        else if(health >= 15)
            return new Color(0, 250, 0);
        else if(health >= 10)
            return new Color(250, 88, 0);
        else if(health >= 5)
            return new Color(250, 88, 0);
        else if(health <= 5)
            return new Color(100, 0, 0);

        return null;
    }

    public void esp3d(Event3D e, EntityLivingBase ent, double maxReach, boolean isValid){

    }

    public static float[] getRotations(EntityLivingBase ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }
    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public boolean isValid(EntityLivingBase ent, EventMotion e){
        if(ent == null || ent == mc.thePlayer) return false;
        if(ent.isOnSameTeam(mc.thePlayer) && getSettingByName("Teams").getValBoolean()) return false;
        if(ent instanceof EntityAnimal && getSettingByName("Animals").getValBoolean()) return true;
        if(ent instanceof EntityPlayer && getSettingByName("Players").getValBoolean()) return true;
        if(ent instanceof EntityMob && getSettingByName("Mobs").getValBoolean()) return true;
        if(ent instanceof EntityVillager && getSettingByName("Villagers").getValBoolean()) return true;
        if(e.getState() == EventMotion.State.PRE && getSettingByName("State").getValString().equalsIgnoreCase("post")) return false;
        if(e.getState() == EventMotion.State.POST && getSettingByName("State").getValString().equalsIgnoreCase("pre")) return false;
        return false;
    }

    public void doRotationsInThirdPerson(EventMotion eventMotion){
        mc.thePlayer.rotationYawHead = eventMotion.getYaw();
        mc.thePlayer.renderYawOffset = eventMotion.getYaw();
        mc.thePlayer.renderArmYaw = eventMotion.getYaw();
        mc.thePlayer.renderArmPitch = eventMotion.getPitch();
        mc.thePlayer.rotationPitchHead = eventMotion.getPitch();
    }


}
