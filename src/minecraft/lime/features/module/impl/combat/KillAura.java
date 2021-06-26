package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.combat.CombatUtils;
import lime.utils.other.Timer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(name = "Kill Aura", category = Category.COMBAT)
public class KillAura extends Module {

    // Settings
    private enum State { PRE, POST }
    private enum AutoBlock { NONE, FAKE, BASIC }
    private enum Priority { DISTANCE, HEALTH }
    private enum Rotations { NONE, BASIC }
    private enum TargetESP { NONE, CIRCLE }

    private final EnumValue state = new EnumValue("State", this, State.PRE);
    private final EnumValue priority = new EnumValue("Priority", this, Priority.DISTANCE);
    private final EnumValue rotations = new EnumValue("Rotations", this, Rotations.BASIC);
    private final EnumValue targetEsp = new EnumValue("Target ESP", this, TargetESP.CIRCLE);
    private final EnumValue autoBlock = new EnumValue("Auto Block", this, AutoBlock.FAKE);
    private final SlideValue range = new SlideValue("Range", this, 2.8, 6, 4.2, 0.05);
    private final SlideValue cps = new SlideValue("CPS", this, 1, 20, 8, 1);
    private final BoolValue players = new BoolValue("Players", this, true);
    private final BoolValue passives = new BoolValue("Passives", this, false);
    private final BoolValue mobs = new BoolValue("Mobs", this, true);
    private final BoolValue rayCast = new BoolValue("Ray Cast", this, false);
    private final BoolValue throughWalls = new BoolValue("Through Walls", this, true);
    private final BoolValue keepSprint = new BoolValue("Keep Sprint", this, true);
    private final BoolValue deathCheck = new BoolValue("Death Check", this, true);
    //

    private static Entity entity;
    private final Timer cpsTimer = new Timer();

    // ESP
    private double yIndex;
    private boolean down;
    private final Timer timer = new Timer();

    // AutoBlock
    private boolean isBlocking = false;

    @Override
    public void onEnable() {
        isBlocking = false;
    }

    @Override
    public void onDisable() {
        if(autoBlock.is("basic") && hasSword() && isBlocking) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            isBlocking = false;
        }
        entity = null;
    }

    public static Entity getEntity() {
        return entity;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        Entity entity = getEntityByPriority();

        if(entity != null && isValid(entity)) {
            // Rotations
            if(!rotations.is("none")) {
                float[] rotations = null;

                switch(this.rotations.getSelected().name().toLowerCase()) {
                    case "basic":
                        rotations = CombatUtils.getEntityRotations((EntityLivingBase) entity, false);
                        break;
                }

                if(rotations == null) return;

                e.setYaw(rotations[0]);
                e.setPitch(rotations[1]);

                mc.thePlayer.setRotationsTP(e);

                // Ray Cast
                if(this.rayCast.isEnabled()) {
                    entity = CombatUtils.raycastEntity(this.range.getCurrent(), rotations);

                    if(entity == null) return;
                }
            }

            KillAura.entity = entity;

            if(!e.isPre() && hasSword() && autoBlock.is("basic")) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 32767);
                isBlocking = true;
            }

            if(!state.is(e.getState().name())) return;

            if(cpsTimer.hasReached(20 / (int) this.cps.getCurrent() * 50L)) {
                mc.thePlayer.swingItem();
                mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                if(!this.keepSprint.isEnabled()) mc.thePlayer.setSprinting(false);
                cpsTimer.reset();
            }
        } else {
            KillAura.entity = null;
            isBlocking = false;
            if(autoBlock.is("basic") && hasSword() && isBlocking) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                isBlocking = false;
            }
        }
    }


    @EventTarget
    public void on3D(Event3D e) {
        if(KillAura.getEntity() != null && isValid(entity) && targetEsp.is("circle")) {
            GL11.glPushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableBlend();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);

            GL11.glLineWidth(2.5f);

            GL11.glColor4f(1, 1, 1, 1);
            GL11.glBegin(GL11.GL_LINE_STRIP);

            double x = KillAura.entity.lastTickPosX + (KillAura.entity.posX - KillAura.entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
            double y = KillAura.entity.lastTickPosY + (KillAura.entity.posY - KillAura.entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
            double z = KillAura.entity.lastTickPosZ + (KillAura.entity.posZ - KillAura.entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

            double entitySize = KillAura.entity.width * 0.85;

            for(int i = 0; i < 361; ++i) {
                GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entitySize, y + yIndex, z - Math.sin(Math.toRadians(i)) * entitySize);

            }
            GL11.glEnd();

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.enableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.resetColor();

            GL11.glPopMatrix();
            if(yIndex > KillAura.entity.height + 0.2) {
                down = true;
            } else if(yIndex <= 0.05)
                down = false;

            if(timer.hasReached(1)) {
                if(down)
                    yIndex -= 0.05;
                else
                    yIndex += 0.05;
                timer.reset();
            }
        }

    }

    private boolean hasSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private Entity getEntityByPriority() {
        ArrayList<Entity> entities = new ArrayList<>();

        for(Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(this.isValid(entity) && mc.thePlayer != entity && entity instanceof EntityLivingBase && mc.thePlayer.getDistanceToEntity(entity) <= this.range.getCurrent())
                entities.add(entity);
        }

        if(entities.isEmpty()) return null;

        switch(priority.getSelected().name().toLowerCase()) {
            case "health":
                entities.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase) entity).getHealth()));
                break;
            case "distance":
                entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
                break;
        }

        return entities.get(0);
    }

    private boolean isValid(Entity entity) {
        if((deathCheck.isEnabled() && !entity.isEntityAlive()) || (!mc.thePlayer.canEntityBeSeen(entity) && !throughWalls.isEnabled())) return false;
        return (entity instanceof EntityPlayer && this.players.isEnabled()) || ((entity instanceof EntityVillager || entity instanceof EntityAnimal) && this.passives.isEnabled()) || (entity instanceof EntityMob && this.mobs.isEnabled()) && mc.thePlayer.getDistanceToEntity(entity) <= this.range.getCurrent();
    }
}
