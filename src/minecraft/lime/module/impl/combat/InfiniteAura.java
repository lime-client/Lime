package lime.module.impl.combat;


import lime.Lime;
import lime.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.Event3D;
import lime.events.impl.EventMotion;
import lime.module.Module;
import lime.module.impl.combat.infiniteaura.InfAuraPathFinder;
import lime.module.impl.combat.infiniteaura.InfiniteAuraVec3;
import lime.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;


public class InfiniteAura extends Module {

    private double dashDistance = 5;
    private ArrayList<InfiniteAuraVec3> path = new ArrayList<>();
    private List<InfiniteAuraVec3>[] test = new ArrayList[50];
    private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
    private Timer cps = new Timer();
    public static Timer timer = new Timer();
    public static boolean canReach;

    //MODIFICATION DE LA REACH DANS ENTITYRENDERER
    public InfiniteAura() {
        super("InfiniteAura", Keyboard.KEY_Y, Module.Category.COMBAT);
        ArrayList<String> m = new ArrayList<>();
        m.add("Vanilla");
        Lime.setmgr.rSetting(new Setting("TPAura Mode", this, "Vanilla", m));
        Lime.setmgr.rSetting(new Setting("TPAura Players", this, true));
        Lime.setmgr.rSetting(new Setting("TPAura ESP", this, true));
        Lime.setmgr.rSetting(new Setting("TPAura PATHESP", this, true));
        Lime.setmgr.rSetting(new Setting("TPAura Animals", this, false));
        Lime.setmgr.rSetting(new Setting("TPAura Invisibles", this, false));
        Lime.setmgr.rSetting(new Setting("TPAura Range", this, 50, 0, 200, true));
        Lime.setmgr.rSetting(new Setting("TPAura CPS", this, 3, 0, 20, true));
        Lime.setmgr.rSetting(new Setting("Max Targets", this, 2, 0, 10, true));
        Lime.setmgr.rSetting(new Setting("TPAura Timer", this, 1, 0, 5, false));
    }

    @Override
    public void onEnable() {
        timer.reset();
        targets.clear();
        super.onEnable();
    }
    @Override
    public void onDisable(){
        super.onDisable();
    }
    @EventTarget
    public void onRender3D(Event3D e){
        if(!this.isToggled()) return;
        int maxtTargets = (int) getSettingByName("Max Targets").getValDouble();
        if(!targets.isEmpty() && getSettingByName("TPAura ESP").getValBoolean()){
            if(targets.size() > 0) {
                for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {

                }
            }
        }
        if(!path.isEmpty() && getSettingByName("TPAura PathESP").getValBoolean()){
            for (int i = 0; i < targets.size(); i++) {
                try {

                    if (test != null){
                        int oof = 0;
                        for (InfiniteAuraVec3 pos : test[i]) {
                            oof++;
                            if (pos != null){

                            }

                        }
                    }


                } catch (Exception e1) {

                }
            }
        }
    }



    @EventTarget
    public void onEvent(EventMotion em) {
        if(!this.isToggled()) return;
        if(mc.theWorld == null || mc.thePlayer == null) return;
        String mode = getSettingByName("TPAura Mode").getValString();
        int maxtTargets = (int) getSettingByName("Max Targets").getValDouble();
        int delayValue = (20 / ((int) getSettingByName("TPAura CPS").getValDouble())) * 50;
        if (em.getState() == EventMotion.State.PRE) {


            targets = getTargets();

            if (cps.hasReached(delayValue))
                if (targets.size() > 0) {
                    test = new ArrayList[50];
                    for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
                        EntityLivingBase T = targets.get(i);
                        InfiniteAuraVec3 topFrom = new InfiniteAuraVec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                        InfiniteAuraVec3 to = new InfiniteAuraVec3(T.posX, T.posY, T.posZ);

                        path = computePath(topFrom, to);
                        test[i] = path;
                        for (InfiniteAuraVec3 pathElm : path) {

                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                        }

                        mc.thePlayer.swingItem();
                        mc.playerController.attackEntity(mc.thePlayer, T);
                        Collections.reverse(path);
                        for (InfiniteAuraVec3 pathElm : path) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                        }
                    }
                    cps.reset();
                }
        }



    }



    private ArrayList<InfiniteAuraVec3> computePath(InfiniteAuraVec3 topFrom, InfiniteAuraVec3 to) {
        if (!canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }
        InfAuraPathFinder pathfinder = new InfAuraPathFinder(topFrom, to);
        pathfinder.compute();

        int i = 0;
        InfiniteAuraVec3 lastLoc = null;
        InfiniteAuraVec3 lastDashLoc = null;
        ArrayList<InfiniteAuraVec3> path = new ArrayList<InfiniteAuraVec3>();
        ArrayList<InfiniteAuraVec3> pathFinderPath = pathfinder.getPath();
        for (InfiniteAuraVec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!InfAuraPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }

    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }


    boolean validEntity(EntityLivingBase entity) {
        float range = (float) getSettingByName("TPAura Range").getValDouble();
        boolean players = getSettingByName("TPAura Players").getValBoolean();
        boolean animals = getSettingByName("TPAura Animals").getValBoolean();

        if ((mc.thePlayer.isEntityAlive())
                && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= range) {

                if (entity.isPlayerSleeping()) {
                    return false;
                }

                if (entity instanceof EntityPlayer) {
                    if (players) {

                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive()
                                && player.getHealth() == 0.0) {
                            return false;
                        } else if (player.isInvisible()
                                && !getSettingByName("TPAura Invisibles").getValBoolean()) {
                            return false;
                        }  else
                            return true;
                    }
                } else {
                    if (!entity.isEntityAlive()) {

                        return false;
                    }
                }

                if (entity instanceof EntityMob && animals) {

                    return true;
                }
                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)
                        && animals) {
                    if (entity.getName().equals("Villager")) {
                        return false;
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();

        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if( (entity.posY < mc.thePlayer.posY - 6 || entity.posY > mc.thePlayer.posY +7) && mc.getIntegratedServer() != null && mc.getCurrentServerData().serverIP.contains("brw")){
                    continue;
                }
                if (validEntity(entity)) {
                    targets.add(entity);
                }
            }
        }
        targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) * 1000 - o2.getDistanceToEntity(mc.thePlayer) * 1000));
        return targets;
    }

    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }



}