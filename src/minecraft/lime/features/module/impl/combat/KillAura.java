package lime.features.module.impl.combat;

import lime.core.Lime;
import lime.core.events.EventBus;
import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.render.HUD;
import lime.features.module.impl.world.Scaffold;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.features.setting.impl.SubOptionProperty;
import lime.management.CommandManager;
import lime.ui.targethud.impl.AstolfoTargetHUD;
import lime.ui.targethud.impl.Lime2TargetHUD;
import lime.ui.targethud.impl.LimeTargetHUD;
import lime.utils.combat.CombatUtils;
import lime.utils.combat.Rotation;
import lime.utils.other.Timer;
import lime.utils.render.ColorUtils;
import lime.utils.render.RenderUtils;
import lime.utils.time.DeltaTime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {
    public KillAura() {
        super("Kill Aura", Category.COMBAT);
    }

    // Attack
    private final EnumProperty mode = new EnumProperty("Mode", this, "Single", "Single", "Switch");
    private final EnumProperty state = new EnumProperty("State", this, "PRE", "PRE", "POST");
    private final EnumProperty autoBlock = new EnumProperty("Auto Block", this, "NCP", "None", "NCP", "Verus", "Fake");
    private final NumberProperty switchDelay = new NumberProperty("Switch Delay", this, 0, 1000, 100, 50).onlyIf(mode.getSettingName(), "enum", "switch");
    private final NumberProperty autoBlockRange = new NumberProperty("Auto Block Range", this, 2.7, 12, 6, .5);
    private final NumberProperty range = new NumberProperty("Range", this, 2.7, 6, 4.2, .05);
    private final NumberProperty cps = new NumberProperty("CPS", this, 1, 20, 8, 1);
    private final BooleanProperty keepSprint = new BooleanProperty("Keep Sprint", this, false);
    private final BooleanProperty rayCast = new BooleanProperty("Ray Cast", this, false);
    private final BooleanProperty ver19plus = new BooleanProperty("1.9+", this, false);
    private final BooleanProperty blinkFirstHit = new BooleanProperty("Blink First Hit", this, false);
    private final BooleanProperty attackWithScaffold = new BooleanProperty("Attack with scaffold on", this, false);

    // Rotations
    private final EnumProperty rotations = new EnumProperty("Rotations", this, "NCP", "None", "NCP", "Smooth");
    private final NumberProperty turnSpeed = new NumberProperty("Turn Speed", this, 0, 180, 90, 10).onlyIf(rotations.getSettingName(), "enum", "smooth");
    public final NumberProperty randomizeYaw = new NumberProperty("Randomize Yaw", this, 0, 10, 3, 1);
    public final BooleanProperty movementFix = new BooleanProperty("Movement Fix", this, false).onlyIf(rotations.getSettingName(), "enum", "NCP", "Smooth");
    private final BooleanProperty gcd = new BooleanProperty("GCD", this, false).onlyIf(rotations.getSettingName(), "enum", "NCP", "Smooth");

    // Targets
    private final EnumProperty sortBy = new EnumProperty("Sort by", this, "Range", "Range", "Health", "FOV");
    private final BooleanProperty players = new BooleanProperty("Players", this, true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", this, true);
    private final BooleanProperty passives = new BooleanProperty("Passives", this, false);
    private final BooleanProperty teams = new BooleanProperty("Teams", this, false);
    private final BooleanProperty dead = new BooleanProperty("Dead", this, false);
    private final BooleanProperty throughWalls = new BooleanProperty("Through Walls", this, true);

    // Render
    private final BooleanProperty jelloEsp = new BooleanProperty("Jello ESP", this, true);

    private final SubOptionProperty targetsSub = new SubOptionProperty("Targets", this, sortBy, players, mobs, passives, teams, dead, throughWalls);
    private final SubOptionProperty attackSub = new SubOptionProperty("Attack", this, mode, state, autoBlock, switchDelay, autoBlockRange, range, cps, keepSprint, rayCast, ver19plus, blinkFirstHit, ver19plus);
    private final SubOptionProperty rotationsSub = new SubOptionProperty("Rotations", this, rotations, turnSpeed, randomizeYaw, movementFix, gcd);
    private final SubOptionProperty renderSub = new SubOptionProperty("Renders", this, jelloEsp);

    public static EntityLivingBase entity;
    public static final float[] currentRotations = new float[2];
    public static boolean isBlocking;

    private final List<C03PacketPlayer> packets = new ArrayList<>();
    public final LimeTargetHUD limeTargetHUD = new LimeTargetHUD();
    public final AstolfoTargetHUD astolfoTargetHUD = new AstolfoTargetHUD();
    public Lime2TargetHUD lime2TargetHUD = new Lime2TargetHUD();
    private final Timer cpsTimer = new Timer(), switchTimer = new Timer();
    private int ticks, index;
    private boolean blink;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            toggle();
            return;
        }
        limeTargetHUD.resetHealthAnimated();
        astolfoTargetHUD.resetHealthAnimated();
        currentRotations[0] = mc.thePlayer.rotationYaw;
        currentRotations[1] = mc.thePlayer.rotationPitch;
        isBlocking = false;
        ticks = index = 0;
        blink = false;
        switchTimer.reset();
    }

    @Override
    public void onDisable() {
        unblock();
        entity = null;

        if(!blink && blinkFirstHit.isEnabled()) {
            for (C03PacketPlayer packet : packets) {
                mc.getNetHandler().sendPacketNoEvent(packet);
            }
            packets.clear();
            blink = true;
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C03PacketPlayer && blinkFirstHit.isEnabled() && !blink) {
            e.setCanceled(true);
            packets.add((C03PacketPlayer) e.getPacket());
        }
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        setSuffix(mode.getSelected());
        if(!CombatUtils.hasSword()) {
            isBlocking = false;
        }

        if(!attackWithScaffold.isEnabled() && Lime.getInstance().getModuleManager().getModuleC(Scaffold.class).isToggled())
            return;

        ArrayList<EntityLivingBase> entities = getEntities();

        if(!entities.isEmpty()) {
            if(index > entities.size() - 1) index = 0;
            EntityLivingBase ent = entities.get(mode.is("switch") ? Math.min(index, entities.size() - 1) : 0);

            if(!rotations.is("none")) {
                float[] rotations = getRotations(ent);
                if(rayCast.isEnabled() && CombatUtils.raycastEntity(range.getCurrent(), rotations) == null) {
                    entity = null;
                    return;
                }
                e.setYaw(rotations[0]);
                e.setPitch(rotations[1]);
                mc.thePlayer.setRotationsTP(e);
            }
            entity = ent;

            if(autoBlock.is("ncp") && !e.isPre()) {
                block();
            }

            if(e.isPre()) {
                ++ticks;
            }

            if(autoBlock.is("verus") && e.isPre() && mc.thePlayer.getDistanceToEntity(ent) <= range.getCurrent() && ticks <= 2) {
                unblock();
                return;
            }

            if(mc.thePlayer.getDistanceToEntity(ent) <= range.getCurrent()) {
                attack(e, ent);
            }
        } else {
            index = 0;
            limeTargetHUD.resetArmorAnimated();
            limeTargetHUD.resetHealthAnimated();
            astolfoTargetHUD.resetHealthAnimated();
            lime2TargetHUD.reset();
            if(mode.is("verus") || mode.is("ncp")) {
                unblock();
            }
            switchTimer.reset();
            entity = null;
            currentRotations[0] = mc.thePlayer.rotationYaw;
            currentRotations[1] = mc.thePlayer.rotationPitch;
        }
    }

    public void attack(EventMotion e, EntityLivingBase entity) {
        if(cpsTimer.hasReached(ver19plus.isEnabled() ? 825 : (1000 / cps.intValue()))) {
            if(!blink && blinkFirstHit.isEnabled()) {
                for (C03PacketPlayer packet : packets) {
                    mc.getNetHandler().sendPacketNoEvent(packet);
                }
                packets.clear();
                blink = true;
            }
            if(state.is(e.getState().name())) {
                if(autoBlock.is("verus")) {
                    unblock();
                }

                EventBus.INSTANCE.call(new EventAttack(entity));

                if(ver19plus.isEnabled()) {
                    mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.swingItem();
                    mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                }

                cpsTimer.reset();
                if(!keepSprint.isEnabled()) {
                    mc.thePlayer.setSprinting(false);
                    if(e.isPre()) {
                        e.setSprint(false);
                    }
                }

                if(autoBlock.is("verus")) {
                    block();
                    ticks = 0;
                }

                if(switchTimer.hasReached(switchDelay.intValue())) {
                    index++;
                    switchTimer.reset();
                }
            }
        }
    }

    public void block() {
        if(CombatUtils.hasSword() && !isBlocking) {
            mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
            isBlocking = true;
        }
    }

    public void unblock() {
        if(isBlocking) {
            mc.getNetHandler().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            isBlocking = false;
        }
    }

    public ArrayList<EntityLivingBase> getEntities() {
        ArrayList<EntityLivingBase> entities = new ArrayList<>();

        mc.theWorld.loadedEntityList.forEach(entity -> {
            if(entity instanceof EntityLivingBase && isValid((EntityLivingBase) entity)) {
                entities.add((EntityLivingBase) entity);
            }
        });

        entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
        if(!entities.isEmpty() && mc.thePlayer.getDistanceToEntity(entities.get(0)) <= range.getCurrent()) {
            entities.removeIf(entity -> mc.thePlayer.getDistanceToEntity(entity) > range.getCurrent());
        }
        entities.sort(Comparator.comparingDouble(e -> mc.thePlayer.getDistanceToEntity(e)));
        try {
            entities.sort(Comparator.comparingDouble(e -> Lime.getInstance().getTargetManager().shouldTarget(e) && mc.thePlayer.getDistanceToEntity(entity) <= range.getCurrent() ? 0 : 1));
        } catch (Exception ignored){}

        return entities;
    }

    public float[] getRotations(EntityLivingBase e) {
        float[] rots = CombatUtils.getEntityRotations(e, true);
        if(rotations.is("smooth")) {
            Rotation rotation = CombatUtils.smoothAngle(rots, currentRotations, turnSpeed.intValue(), turnSpeed.intValue());
            rots[0] = rotation.getYaw();
            rots[1] = rotation.getPitch();
        }
        if(gcd.isEnabled()) {
            rots = CombatUtils.fixedSensitivity(rots[0], rots[1], currentRotations[0], currentRotations[1]);
        }

        currentRotations[0] = rots[0];
        currentRotations[1] = rots[1];


        return rots;
    }

    public int getColor(int count) {
        float f1 = 20;
        float f2 = Math.max(0.0F, Math.min((float) count, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }

    public static EntityLivingBase getEntity() {
        return entity;
    }

    @EventTarget
    public void on2D(Event2D e) {
        HUD hud = (HUD) Lime.getInstance().getModuleManager().getModule("HUD");
        if(entity != null && isValid(entity) && mc.thePlayer.getDistanceToEntity(entity) <= range.getCurrent()) {
            switch(hud.targetHud.getSelected().toLowerCase()) {
                case "lime":
                    limeTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70), getColor(Math.round(entity.getHealth())));
                    break;
                case "lime2":
                    lime2TargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70), getColor(Math.round(entity.getHealth())));
                    break;
                case "astolfo":
                    astolfoTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70), getColor(Math.round(entity.getHealth())));
                    break;
            }
        }
    }

    @EventTarget
    public void on3D(Event3D e) {
        if(entity != null && isValid(entity) && mc.thePlayer.getDistanceToEntity(entity) <= range.getCurrent()) {
            renderJello(entity);
        }
    }

    private double time;
    public boolean down;

    public void renderJello(EntityLivingBase e) {
        time += .015 * (DeltaTime.getDeltaTime() * .1);
        final double height = 0.5 * (1 + Math.sin(2 * Math.PI * (time * .3)));

        if (height > .995) {
            down = true;
        } else if (height < .01) {
            down = false;
        }

        final double x = e.posX + (e.posX - e.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
        final double y = e.posY + (e.posY - e.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
        final double z = e.posZ + (e.posZ - e.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;

        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GL11.glLineWidth(1.5F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_CULL_FACE);
        final double size = e.width;
        final double yOffset = (e.height + .2) * height;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        {
            for (int j = 0; j < 361; j++) {
                RenderUtils.glColor(ColorUtils.setAlpha(HUD.getColor(0), (int) (!down ? 255 * height : 255 * (1 - height))));
                GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size, y + yOffset, z - Math.sin(Math.toRadians(j)) * size);
                RenderUtils.glColor(ColorUtils.setAlpha(HUD.getColor(0), 0));
                GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size, y + yOffset + ((!down ? -.5 * (1 - height) : .5 * height)), z - Math.sin(Math.toRadians(j)) * size);
            }
        }
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_LOOP);
        {
            for (int j = 0; j < 361; j++) {
                RenderUtils.glColor(HUD.getColor(0));
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

    public boolean isValid(EntityLivingBase e) {
        AntiBot antiBot = Lime.getInstance().getModuleManager().getModuleC(AntiBot.class);
        if(antiBot.isToggled() && e instanceof EntityPlayer && antiBot.checkBot((EntityPlayer) e)) return false;
        if(!e.isEntityAlive() && !dead.isEnabled()) return false;
        if(range.getCurrent() < mc.thePlayer.getDistanceToEntity(e) && autoBlockRange.getCurrent() < mc.thePlayer.getDistanceToEntity(e)) return false;
        if(Lime.getInstance().getFriendManager().isFriend(e)) return false;
        if(mc.thePlayer.getDistanceToEntity(e) <= autoBlockRange.getCurrent() && mc.thePlayer.getDistanceToEntity(e) > range.getCurrent() && !CombatUtils.hasSword()) {
            return false;
        }
        if(teams.isEnabled() && mc.thePlayer.isOnSameTeam(e)) return false;
        return ((e instanceof EntityPlayer && players.isEnabled() && e != mc.thePlayer) || (e instanceof EntityMob && mobs.isEnabled()) || ((e instanceof EntityAnimal || e instanceof EntityVillager) && passives.isEnabled()) && (mc.thePlayer.canEntityBeSeen(e) || throughWalls.isEnabled()));
    }
}
