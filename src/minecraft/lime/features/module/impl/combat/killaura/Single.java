package lime.features.module.impl.combat.killaura;

import lime.core.Lime;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventMotion;
import lime.features.module.impl.combat.Criticals;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.render.HUD;
import lime.utils.combat.CombatUtils;
import lime.utils.combat.Rotation;
import lime.utils.other.MathUtils;
import lime.utils.other.Timer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;

public class Single extends KillAuraMode {

    public Single(KillAura killAura) {
        super(killAura);
    }

    private final Timer cpsTimer = new Timer();
    private EntityLivingBase entity;
    private float currentYaw, currentPitch;

    @Override
    public void onEnable() {
        entity = null;
        if(mc.thePlayer == null) {
            this.killAura.toggle();
        } else {
            currentYaw = mc.thePlayer.rotationYaw;
            currentPitch = mc.thePlayer.rotationPitch;
            KillAura.isBlocking = false;
            killAura.down = false;
        }
    }

    @Override
    public void onDisable() {
        if(mc.thePlayer != null) {
            if(killAura.hasSword() && KillAura.isBlocking) {
                mc.playerController.syncCurrentPlayItem();
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                KillAura.isBlocking = false;
            }
        } else {
            return;
        }
        entity = KillAura.entity = null;
    }

    @Override
    public void on3D(Event3D e) {
        if(entity != null && killAura.isValid(entity)) {
            killAura.renderJello(entity);
        }
    }

    @Override
    public void onMotion(EventMotion e) {
        if(KillAura.isBlocking && killAura.hasSword() && !killAura.autoBlock.is("basic")) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.playerController.syncCurrentPlayItem();
            KillAura.isBlocking = false;
        }

        EntityLivingBase entity = getEntity();

        if(entity != null && killAura.isValid(entity)) {

            if(!killAura.keepSprint.isEnabled()) {
                mc.thePlayer.setSprinting(false);
                e.setSprint(false);
                mc.gameSettings.keyBindSprint.pressed = false;
            }

            // Rotations
            if(!killAura.rotations.is("none")) {
                float[] rotations = null;

                switch(killAura.rotations.getSelected().toLowerCase()) {
                    case "basic":
                        rotations = CombatUtils.getEntityRotations(entity, true);
                        break;
                    case "smooth":
                        rotations = CombatUtils.getEntityRotations(entity, false);
                        float[] crt = new float[] {currentYaw, currentPitch};
                        Rotation rotation = CombatUtils.smoothAngle(new float[]{rotations[0], rotations[1]}, crt, (float) killAura.rotationsSpeedMin.getCurrent(), (float) killAura.rotationsSpeedMax.getCurrent());
                        rotations[0] = rotation.getYaw();
                        rotations[1] = rotation.getPitch();
                        currentYaw = rotations[0];
                        currentPitch = rotations[1];
                        break;
                }

                if(rotations == null || rotations[1] > 90 || rotations[1] < -90) return;

                e.setYaw(rotations[0]);
                e.setPitch(rotations[1]);

                mc.thePlayer.setRotationsTP(e);

                // Ray Cast
                if(killAura.rayCast.isEnabled()) {
                    Entity entity1 = CombatUtils.raycastEntity(killAura.range.getCurrent(), rotations);
                    if(entity1 == null) return;
                    entity = (EntityLivingBase) entity1;
                }
            }

            this.entity = entity;

            if(killAura.autoBlockState.is(e.getState().name()) && killAura.hasSword() && killAura.autoBlock.is("basic") && !KillAura.isBlocking) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 32767);
                KillAura.isBlocking = true;
            }
            if(KillAura.isBlocking) {
                mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 32767);
            }

            if(!killAura.hasSword() || (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword))) {
                KillAura.isBlocking = false;
            }

            if(!killAura.state.is(e.getState().name())) return;
            int cps = Math.max(killAura.cps.intValue() + (int) MathUtils.random(-killAura.randomizeCps.intValue(), killAura.randomizeCps.intValue()), 1);
            if(cpsTimer.hasReached(20 / cps * 50L) && mc.thePlayer.getDistanceToEntity(entity) <= killAura.range.getCurrent()) {
                mc.thePlayer.swingItem();
                mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                cpsTimer.reset();

                if(!mc.thePlayer.onGround && !Lime.getInstance().getModuleManager().getModuleC(Criticals.class).isToggled() && killAura.particles.isEnabled()) {
                    mc.thePlayer.onCriticalHit(entity);
                }
                if(killAura.hasSword() && killAura.particles.isEnabled() && EnchantmentHelper.getEnchantmentLevel(16, mc.thePlayer.getHeldItem()) != 0) {
                    mc.thePlayer.onEnchantmentCritical(entity);
                }
            }
        } else {
            killAura.limeTargetHUD.resetArmorAnimated();
            killAura.astolfoTargetHUD.resetHealthAnimated();
            currentYaw = mc.thePlayer.rotationYaw;
            currentPitch = mc.thePlayer.rotationPitch;
            this.entity = null;
            if(killAura.autoBlock.is("basic") && killAura.hasSword() && KillAura.isBlocking) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                mc.playerController.syncCurrentPlayItem();
                KillAura.isBlocking = false;
            }
        }
    }

    @Override
    public void on2D(Event2D e) {
        HUD hud = (HUD) Lime.getInstance().getModuleManager().getModule("HUD");
        if(entity != null && killAura.isValid(entity)) {
            switch(hud.targetHud.getSelected().toLowerCase()) {
                case "lime":
                    killAura.limeTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70), killAura.getColor(Math.round(entity.getHealth())));
                    //killAura.limeTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70), killAura.getColor(Math.round(entity.getHealth())));
                    break;
                case "astolfo":
                    killAura.astolfoTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70), killAura.getColor(Math.round(entity.getHealth())));
                    break;
            }
        }
    }

    public EntityLivingBase getEntity() {
        ArrayList<EntityLivingBase> entities = new ArrayList<>();

        for (Entity entity1 : mc.theWorld.loadedEntityList) {
            if(entity1 instanceof EntityLivingBase && killAura.isValid(entity1) && entity1 != mc.thePlayer) {
                entities.add((EntityLivingBase) entity1);
            }
        }

        killAura.sortEntities(entities, true);

        if(!entities.isEmpty() && mc.thePlayer.getDistanceToEntity(entities.get(0)) <= killAura.range.getCurrent()) {
            entities.removeIf(entity -> mc.thePlayer.getDistanceToEntity(entity) > killAura.range.getCurrent());
        }

        killAura.sortEntities(entities, false);

        if(entities.isEmpty()) return null;
        return entities.get(0);
    }

    @Override
    public EntityLivingBase getTargetedEntity() {
        return entity;
    }
}
