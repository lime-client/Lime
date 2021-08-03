package lime.features.module.impl.combat;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.render.HUD;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.ui.notifications.Notification;
import lime.ui.targethud.impl.AstolfoTargetHUD;
import lime.ui.targethud.impl.LimeTargetHUD;
import lime.utils.combat.CombatUtils;
import lime.utils.combat.Rotation;
import lime.utils.other.MathUtils;
import lime.utils.other.Timer;
import lime.utils.render.ColorUtils;
import lime.utils.render.RenderUtils;
import lime.utils.time.DeltaTime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(name = "Kill Aura", category = Category.COMBAT)
public class KillAura extends Module {

    // Settings
    private final EnumValue state = new EnumValue("State", this, "PRE", "PRE", "POST");
    private final EnumValue priority = new EnumValue("Priority", this, "Distance", "Distance", "Health", "FOV");
    private final EnumValue rotations = new EnumValue("Rotations", this, "Basic", "None", "Basic", "Smooth");
    private final EnumValue targetEsp = new EnumValue("Target ESP", this, "Circle", "None", "Circle");
    private final EnumValue autoBlock = new EnumValue("Auto Block", this, "Fake", "None", "Basic", "Fake");
    private final EnumValue autoBlockState = new EnumValue("Auto Block State", this, "POST", "PRE", "POST");
    private final SlideValue rotationsSpeedMin = new SlideValue("Rotations Min", this, 5, 100, 50, 1).onlyIf(rotations.getSettingName(), "enum", "smooth");
    private final SlideValue rotationsSpeedMax = new SlideValue("Rotations Max", this, 5, 100, 90, 1).onlyIf(rotations.getSettingName(), "enum", "smooth");
    private final SlideValue range = new SlideValue("Range", this, 2.8, 6, 4.2, 0.05);
    private final SlideValue cps = new SlideValue("CPS", this, 1, 20, 8, 1);
    private final SlideValue randomizeCps = new SlideValue("Randomize CPS", this, 0, 5, 3, 1);
    private final BoolValue players = new BoolValue("Players", this, true);
    private final BoolValue passives = new BoolValue("Passives", this, false);
    private final BoolValue mobs = new BoolValue("Mobs", this, true);
    private final BoolValue teams = new BoolValue("Teams", this, true);
    private final BoolValue rayCast = new BoolValue("Ray Cast", this, false);
    private final BoolValue throughWalls = new BoolValue("Through Walls", this, true);
    private final BoolValue keepSprint = new BoolValue("Keep Sprint", this, true);
    private final BoolValue deathCheck = new BoolValue("Death Check", this, true);
    //

    private static EntityLivingBase entity;
    private final Timer cpsTimer = new Timer();

    // AutoBlock
    public static boolean isBlocking = false;

    // Smooth rotations
    private float currentYaw, currentPitch;

    // TargetHUD
    private final LimeTargetHUD limeTargetHUD = new LimeTargetHUD();
    private final AstolfoTargetHUD astolfoTargetHUD = new AstolfoTargetHUD();

    @Override
    public void onEnable() {
        limeTargetHUD.resetHealthAnimated();
        limeTargetHUD.resetHealthAnimated();
        astolfoTargetHUD.resetHealthAnimated();
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        } else {
            currentYaw = mc.thePlayer.rotationYaw;
            currentPitch = mc.thePlayer.rotationPitch;
        }
        isBlocking = false;
        down = false;
    }

    @Override
    public void onDisable() {
        if(mc.thePlayer != null) {
            if(hasSword() && isBlocking) {
                mc.playerController.syncCurrentPlayItem();
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                isBlocking = false;
            }
        } else {
            return;
        }
        entity = null;
    }

    public static EntityLivingBase getEntity() {
        return entity;
    }

    @EventTarget
    public void onMotion(EventMotion e) {

        if(isBlocking && hasSword() && !autoBlock.is("basic")) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.playerController.syncCurrentPlayItem();
            isBlocking = false;
        }

        EntityLivingBase entity = getEntityByPriority();

        if(entity != null && isValid(entity)) {
            // Rotations
            if(!rotations.is("none")) {
                float[] rotations = null;

                switch(this.rotations.getSelected().toLowerCase()) {
                    case "basic":
                        rotations = CombatUtils.getEntityRotations(entity, false);
                        break;
                    case "smooth":
                        rotations = CombatUtils.getEntityRotations(entity, false);
                        float[] crt = new float[] {currentYaw, currentPitch};
                        Rotation rotation = CombatUtils.smoothAngle(new float[]{rotations[0], rotations[1]}, crt, (float) rotationsSpeedMin.getCurrent(), (float) rotationsSpeedMax.getCurrent());
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
                if(this.rayCast.isEnabled()) {
                    Entity entity1 = CombatUtils.raycastEntity(this.range.getCurrent(), rotations);
                    if(entity1 == null) return;
                    entity = (EntityLivingBase) entity1;
                }
            }

            KillAura.entity = entity;

            if(autoBlockState.is(e.getState().name()) && hasSword() && autoBlock.is("basic") && !isBlocking) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 32767);
                isBlocking = true;
            }
            if(isBlocking) {
                mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 32767);
            }

            if(!hasSword()) {
                isBlocking = false;
            }

            if(!state.is(e.getState().name())) return;
            int cps = Math.max(this.cps.intValue() + (int) MathUtils.random(-randomizeCps.intValue(), randomizeCps.intValue()), 1);
            if(cpsTimer.hasReached(20 / cps * 50L)) {
                mc.thePlayer.swingItem();
                mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                if(!this.keepSprint.isEnabled()) mc.thePlayer.setSprinting(false);
                cpsTimer.reset();
            }
        } else {
            limeTargetHUD.resetArmorAnimated();
            limeTargetHUD.resetHealthAnimated();
            currentYaw = mc.thePlayer.rotationYaw;
            currentPitch = mc.thePlayer.rotationPitch;
            KillAura.entity = null;
            if(autoBlock.is("basic") && hasSword() && isBlocking) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                mc.playerController.syncCurrentPlayItem();
                isBlocking = false;
            }
        }
    }

    @EventTarget
    public void onWorldLoaded(EventWorldChange e)
    {
        this.disableModule();
        KillAura.entity = null;
        Lime.getInstance().getNotificationManager().addNotification(new Notification("Kill Aura", "Disabled Kill Aura because you changed world!", Notification.Type.WARNING));
    }

    @EventTarget
    public void on2D(Event2D e)
    {
        HUD hud = (HUD) Lime.getInstance().getModuleManager().getModule("HUD");
        if(entity != null && entity.isEntityAlive() && this.isToggled() && mc.thePlayer.getDistanceToEntity(KillAura.entity) <= range.getCurrent() && (entity.canEntityBeSeen(mc.thePlayer) || (!entity.canEntityBeSeen(mc.thePlayer) && throughWalls.isEnabled()))){
            switch(hud.targetHud.getSelected().toLowerCase()) {
                case "lime":
                    limeTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70), getColor(Math.round(entity.getHealth())));
                    break;
                case "astolfo":
                    astolfoTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70), getColor(Math.round(entity.getHealth())));
                    break;
            }
        }
    }

    private int getColor(int count) {
        float f1 = 20;
        float f2 = Math.max(0.0F, Math.min((float) count, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }


    private double time;
    private boolean down;

    @EventTarget
    public void on3D(Event3D e) {
        if(KillAura.getEntity() != null && isValid(entity) && targetEsp.is("circle")) {
            time += .01 * (DeltaTime.getDeltaTime() * 0.25);
            final double height = 0.5 * (1 + Math.sin(2 * Math.PI * (time * .3)));

            if (height > .995) {
                down = true;
            } else if (height < .01) {
                down = false;
            }

            final net.minecraft.util.Timer timer = mc.timer;
            final RenderManager renderManager = mc.getRenderManager();

            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX;
            final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY;
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ;

            GlStateManager.enableBlend();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GL11.glLineWidth(1.5F);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glDisable(GL11.GL_CULL_FACE);
            final double size = entity.width * 0.85;
            final double yOffset =  2 * (height);

            Color clientColor = HUD.getColor(0);

            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            {
                for (int j = 0; j < 361; j++) {
                    RenderUtils.glColor(ColorUtils.setAlpha(clientColor, (int) (!down ? 255 * height : 255 * (1 - height))));
                    final double x1 = x + Math.cos(Math.toRadians(j)) * size;
                    final double z1 = z - Math.sin(Math.toRadians(j)) * size;
                    GL11.glVertex3d(x1, y + yOffset, z1);
                    RenderUtils.glColor(ColorUtils.setAlpha(clientColor, 0));
                    GL11.glVertex3d(x1, y + yOffset + ((!down ? -1 * (1 - height) : .5 * height)), z1);
                }
            }
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                for (int j = 0; j < 361; j++) {
                    RenderUtils.glColor(clientColor);
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
        }

    }

    private boolean hasSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private EntityLivingBase getEntityByPriority() {
        ArrayList<Entity> entities = new ArrayList<>();

        for(Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(mc.thePlayer != entity && entity instanceof EntityLivingBase && this.isValid(entity) && mc.thePlayer.getDistanceToEntity(entity) <= this.range.getCurrent())
                entities.add(entity);
        }

        if(entities.isEmpty()) return null;

        switch(priority.getSelected().toLowerCase()) {
            case "health":
                entities.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase) entity).getHealth()));
                break;
            case "distance":
                entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
                break;
            case "fov":
                entities.sort(Comparator.comparingDouble(entity -> CombatUtils.getRotationDifference((EntityLivingBase) entity)));
                break;
        }

        return (EntityLivingBase) entities.get(0);
    }

    private boolean isValid(Entity entity) {
        AntiBot antiBot = (AntiBot) Lime.getInstance().getModuleManager().getModuleC(AntiBot.class);
        if(entity instanceof EntityPlayer && antiBot.checkBot((EntityPlayer) entity)) return false;
        if(teams.isEnabled() && entity instanceof EntityLivingBase && mc.thePlayer.isOnSameTeam((EntityLivingBase) entity)) return false;
        if((deathCheck.isEnabled() && !entity.isEntityAlive()) || (!mc.thePlayer.canEntityBeSeen(entity) && !throughWalls.isEnabled())) return false;
        return (entity instanceof EntityPlayer && this.players.isEnabled()) || ((entity instanceof EntityVillager || entity instanceof EntityAnimal) && this.passives.isEnabled()) || (entity instanceof EntityMob && this.mobs.isEnabled()) && mc.thePlayer.getDistanceToEntity(entity) <= this.range.getCurrent();
    }
}