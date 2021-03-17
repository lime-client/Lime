package lime.module.impl.combat;

import lime.Lime;
import lime.settings.impl.*;
import lime.events.EventTarget;
import lime.events.impl.*;
import lime.module.Module;
import lime.module.impl.render.HUD;
import lime.module.impl.render.targethuds.AstolfoTargetHUD;
import lime.utils.movement.MovementUtil;
import lime.utils.other.OtherUtil;
import lime.utils.Timer;
import lime.utils.render.UtilGL;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class KillAura extends Module {
    ListValue state = new ListValue("State", this, "PRE", "PRE", "POST");
    ListValue espMode = new ListValue("ESP Mode", this, "Box", "Box", "Little Box", "Jello");
    ComboBooleanValue disableOn = new ComboBooleanValue("Disable on", this);
    BooleanValue onDeath = new BooleanValue("Death", this, true, disableOn.getSet());
    BooleanValue onWorldChange = new BooleanValue("Changing World", this, true, disableOn.getSet());
    ComboBooleanValue entities = new ComboBooleanValue("Entities", this);
    SlideValue autoBlockReach = new SlideValue("AutoBlock Reach", this, 6, 2.6, 12, false);
    SlideValue reach = new SlideValue("Reach", this, 4.2, 2.6, 7, false);
    SlideValue CPS = new SlideValue("CPS", this, 8, 1, 20, true);
    BooleanValue lockview = new BooleanValue("Lockview", this, false);
    BooleanValue players = new BooleanValue("Players", this, true, entities.getSet());
    BooleanValue animals = new BooleanValue("Animals", this, false, entities.getSet());
    BooleanValue villagers = new BooleanValue("Villagers", this, false, entities.getSet());
    BooleanValue mobs = new BooleanValue("Mobs", this, true, entities.getSet());
    BooleanValue throughWalls = new BooleanValue("Through Walls", this, true);
    BooleanValue keepSprint = new BooleanValue("KeepSprint", this, true);
    BooleanValue autoBlock = new BooleanValue("AutoBlock", this, true);
    BooleanValue targetInfo = new BooleanValue("TargetInfo", this, true);
    BooleanValue esp = new BooleanValue("ESP", this, true);
    BooleanValue teams = new BooleanValue("Teams", this, true);
    public ColorPicker colorPicker = new ColorPicker("ESP Color", this, new Color(69, 42, 71).getRGB());

    public KillAura(){
        super("KillAura", Keyboard.KEY_R, Category.COMBAT);
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
        //unblock();
        super.onDisable();
    }

    @EventTarget
    public void onWorldChange(EventWorldChange e){
        if(onWorldChange.getValue())
            this.disable();
    }

    @EventTarget
    public void onDeath(EventDeath e){
        if(onDeath.getValue())
            this.disable();
    }

    @EventTarget
    public void onMotion(EventMotion e){
        double reach = this.reach.getValue();
        boolean block = autoBlock.getValue();
        double blockReach = autoBlockReach.getValue();
        boolean keepSprint = this.keepSprint.getValue();
        boolean throughWalls = this.throughWalls.getValue();
        EntityLivingBase entity = getBestEntity(reach, e);
        boolean isValid = isValid(entity, e);
        validEntity = isValid;
        int cps = 20 / this.CPS.getIntValue() * 50;
        if(isValid)
            attack(e, entity, reach, true, cps, block, blockReach, throughWalls, false, keepSprint);
    }

    @EventTarget
    public void on2D(Event2D e){
        if(entity != null && Lime.friendManager.isIn(entity.getName())) return;
        if(entity != null && validEntity && this.targetInfo.getValue()){
            targetInfo(e, entity, this.reach.getValue(), true);
        }

    }

    @EventTarget
    public void on3D(Event3D e){
        if(entity != null && Lime.friendManager.isIn(entity.getName())) return;
        if(entity != null && validEntity && this.esp.getValue())
            esp3d(e, entity, this.reach.getValue(), true);
    }

    public EntityLivingBase getBestEntity(double maxReach, EventMotion e){
        ArrayList<Entity> entities = new ArrayList<>();
        for(Entity ent : mc.theWorld.loadedEntityList){
            if(ent instanceof EntityLivingBase && isValid((EntityLivingBase) ent, e) && mc.thePlayer.getDistanceToEntity(ent) <= maxReach) {
                {
                    AntiBot antiBot = (AntiBot) Lime.moduleManager.getModuleByName("AntiBot");
                    if (antiBot.isToggled() && antiBot.skipEntity(ent)) {
                        continue;
                    }
                }
                {
                    if (AutoPot.doSp || AutoPot.doPot) {
                        continue;
                    }
                }
                entities.add(ent);
            }
        }
        if(entities.isEmpty()) return null;
        entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
        return (EntityLivingBase) entities.get(0);
    }

    public void attack(EventMotion e, EntityLivingBase ent, double maxReach, boolean isValid, int cps, boolean block, double blockReach, boolean throughWalls, boolean noSwing, boolean keepSprint){

        if(ent.getDistanceToEntity(mc.thePlayer) <= maxReach && isValid && ((ent).canEntityBeSeen(mc.thePlayer) || (!(ent).canEntityBeSeen(mc.thePlayer) && throughWalls))){

            entity = ent;
            float[] rotations = getRotations(ent);
            if(!lockview.getValue()){
                e.setYaw(rotations[0]);
                e.setPitch(rotations[1]);
            } else {
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            }

            OtherUtil.doRotationsInThirdPerson(e);
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
        if(ent.getDistanceToEntity(mc.thePlayer) <= blockReach && isValid)
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
        if(isValid && mc.thePlayer.getDistanceToEntity(ent) <= maxReach){
            {
                AntiBot antiBot = (AntiBot) Lime.moduleManager.getModuleByName("AntiBot");
                if(antiBot.isToggled() && antiBot.skipEntity(ent)){
                    return;
                }
            }
            Render.doRenderNameTags = false;
            HUD hud = (HUD) Lime.moduleManager.getModuleByName("HUD");
            switch(hud.targetHudMode.getValue().toLowerCase()){
                case "astolfo":
                    astolfoTargetHUD.draw(ent, hud.targetHudX.getFloatValue() / 100f * (e.getWidth() - 174), hud.targetHudY.getFloatValue() / 100f * (e.getHeight() - 70), getHealthColor(Math.round(ent.getHealth())).getRGB());
                    break;
            }
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
    double y = 0;

    public void esp3d(Event3D e, EntityLivingBase ent, double maxReach, boolean isValid){
        if(isValid && ent.getDistanceToEntity(mc.thePlayer) <= maxReach){
            if(espMode.getValue().equalsIgnoreCase("Little Box")){
                double x = entity.lastTickPosX  + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;

                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks + entity.getEyeHeight()*1.2;

                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
                double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX) + 0.2;
                double widthZ = Math.abs(entity.boundingBox.maxZ - entity.boundingBox.minZ) + 0.2;
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                UtilGL.drawBox(new Vec3(x - width / 2 - 0.2, y + 0.1, z - widthZ / 2), new Vec3(1, 0.1, 1), setAlpha(new Color(colorPicker.getValue()), 50));
            } else if(espMode.getValue().equalsIgnoreCase("Box")){
                UtilGL.drawBox(ent, setAlpha(new Color(colorPicker.getValue()), 50));
            }
        }
    }

    public static Color setAlpha(Color color, int alpha){
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
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
        float yawAdd = MovementUtil.isMoving() ? new Random().nextBoolean() ? random(0.7, 1.4) : -random(0.4, 0.7) : 0;
        float pitchAdd = MovementUtil.isMoving() ? new Random().nextBoolean() ? random(0.4, 0.7) : -random(0.4, 0.7) : 0;
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F + yawAdd;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D) + pitchAdd;
        return new float[]{yaw, pitch};
    }

    public static float random(double min, double max){
        return (float) (Math.random() * (max - min) + min);
    }

    public boolean isValid(EntityLivingBase ent, EventMotion e){
        if(ent == null || ent == mc.thePlayer) return false;
        if(ent.isOnSameTeam(mc.thePlayer) && teams.getValue()) return false;
        if(ent instanceof EntityAnimal && animals.getValue()) return true;
        if(ent instanceof EntityPlayer && players.getValue()) return true;
        if(ent instanceof EntityMob && mobs.getValue()) return true;
        if(ent instanceof EntityVillager && villagers.getValue()) return true;
        if(e.getState() == EventMotion.State.PRE && state.getValue().equalsIgnoreCase("post")) return false;
        if(e.getState() == EventMotion.State.POST && state.getValue().equalsIgnoreCase("pre")) return false;
        if(Lime.friendManager.isIn(ent.getName()) && Lime.moduleManager.getModuleByName("Friends").isToggled()) return false;
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
