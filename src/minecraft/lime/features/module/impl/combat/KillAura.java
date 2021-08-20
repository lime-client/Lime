package lime.features.module.impl.combat;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.combat.killaura.Multi;
import lime.features.module.impl.combat.killaura.Single;
import lime.features.module.impl.render.HUD;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.ui.notifications.Notification;
import lime.ui.targethud.impl.AstolfoTargetHUD;
import lime.ui.targethud.impl.LimeTargetHUD;
import lime.utils.combat.CombatUtils;
import lime.utils.render.ColorUtils;
import lime.utils.render.RenderUtils;
import lime.utils.time.DeltaTime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(name = "Kill Aura", category = Category.COMBAT)
public class KillAura extends Module {

    // Settings
    public final EnumValue state = new EnumValue("State", this, "PRE", "PRE", "POST");
    private final EnumValue mode = new EnumValue("Mode", this, "Single", "Single", "Multi");
    private final EnumValue priority = new EnumValue("Priority", this, "Distance", "Distance", "Health", "FOV");
    public final EnumValue rotations = new EnumValue("Rotations", this, "Basic", "None", "Basic", "Smooth");
    private final EnumValue targetEsp = new EnumValue("Target ESP", this, "Circle", "None", "Circle");
    public final EnumValue autoBlock = new EnumValue("Auto Block", this, "Fake", "None", "Basic", "Fake");
    public final EnumValue autoBlockState = new EnumValue("Auto Block State", this, "POST", "PRE", "POST");
    public final SlideValue rotationsSpeedMin = new SlideValue("Rotations Min", this, 5, 100, 50, 1).onlyIf(rotations.getSettingName(), "enum", "smooth");
    public final SlideValue rotationsSpeedMax = new SlideValue("Rotations Max", this, 5, 100, 90, 1).onlyIf(rotations.getSettingName(), "enum", "smooth");
    private final SlideValue autoBlockRange = new SlideValue("Auto Block Range", this, 2.8, 12, 8, 0.05).onlyIf(autoBlock.getSettingName(), "enum", "basic", "fake");
    public final SlideValue range = new SlideValue("Range", this, 2.8, 6, 4.2, 0.05);
    public final SlideValue cps = new SlideValue("CPS", this, 1, 20, 8, 1);
    public final SlideValue randomizeCps = new SlideValue("Randomize CPS", this, 0, 5, 3, 1);
    private final BoolValue players = new BoolValue("Players", this, true);
    private final BoolValue passives = new BoolValue("Passives", this, false);
    private final BoolValue mobs = new BoolValue("Mobs", this, true);
    private final BoolValue teams = new BoolValue("Teams", this, true);
    public final BoolValue rayCast = new BoolValue("Ray Cast", this, false);
    private final BoolValue throughWalls = new BoolValue("Through Walls", this, true);
    public final BoolValue keepSprint = new BoolValue("Keep Sprint", this, true);
    private final BoolValue deathCheck = new BoolValue("Death Check", this, true);
    public final BoolValue particles = new BoolValue("Particles", this, false);

    public static EntityLivingBase entity;

    // AutoBlock
    public static boolean isBlocking = false;

    // TargetHUD
    public final LimeTargetHUD limeTargetHUD = new LimeTargetHUD();
    public final AstolfoTargetHUD astolfoTargetHUD = new AstolfoTargetHUD();

    private final Single single = new Single(this);
    private final Multi multi = new Multi(this);

    @Override
    public void onEnable() {
        limeTargetHUD.resetHealthAnimated();
        astolfoTargetHUD.resetHealthAnimated();

        if(mode.is("single")) {
            single.onEnable();
        }
        if(mode.is("multi")) {
            multi.onEnable();
        }
    }

    @Override
    public void onDisable() {
        if(mode.is("single")) {
            single.onDisable();
        }
        if(mode.is("multi")) {
            multi.onDisable();
        }
    }

    public static EntityLivingBase getEntity() {
        return entity;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(mode.is("single")) {
            single.onMotion(e);
            entity = single.getTargetedEntity();
        }
        if(mode.is("multi")) {
            multi.onMotion(e);
            entity = multi.getTargetedEntity();
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
        if(autoBlockRange.getCurrent() < range.getCurrent()) {
            autoBlockRange.setCurrentValue(range.getCurrent());
        }
        if(mode.is("single")) {
            single.on2D(e);
        } else if(mode.is("multi")) {
            multi.on2D(e);
        }
    }

    public int getColor(int count) {
        float f1 = 20;
        float f2 = Math.max(0.0F, Math.min((float) count, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }


    private double time;
    public boolean down;

    @EventTarget
    public void on3D(Event3D e) {
        if(targetEsp.is("circle")) {
            if(mode.is("single")) {
                single.on3D(e);
            }
            if(mode.is("multi")) {
                multi.on3D(e);
            }
        }
    }

    public void renderJello(EntityLivingBase e) {
        time += .01 * (DeltaTime.getDeltaTime() * 0.25);
        final double height = 0.5 * (1 + Math.sin(2 * Math.PI * (time * .3)));

        if (height > .995) {
            down = true;
        } else if (height < .01) {
            down = false;
        }

        final double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
        final double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
        final double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;

        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GL11.glLineWidth(1.5F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_CULL_FACE);
        final double size = e.width * 0.85;
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

    public boolean hasSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public void sortEntities(ArrayList<EntityLivingBase> entities) {
        switch(priority.getSelected().toLowerCase()) {
            case "health":
                entities.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            case "distance":
                entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
                break;
            case "fov":
                entities.sort(Comparator.comparingDouble(CombatUtils::getRotationDifference));
                break;
        }
    }

    public boolean isValid(Entity entity) {
        AntiBot antiBot = (AntiBot) Lime.getInstance().getModuleManager().getModuleC(AntiBot.class);
        if(entity instanceof EntityPlayer && antiBot.checkBot((EntityPlayer) entity)) return false;
        if(teams.isEnabled() && entity instanceof EntityLivingBase && mc.thePlayer.isOnSameTeam((EntityLivingBase) entity)) return false;
        if((deathCheck.isEnabled() && !entity.isEntityAlive()) || (!mc.thePlayer.canEntityBeSeen(entity) && !throughWalls.isEnabled())) return false;
        if(autoBlock.is("none") && mc.thePlayer.getDistanceToEntity(entity) >= this.range.getCurrent() && (autoBlock.is("none") || !hasSword())) return false;
        if((!autoBlock.is("none") && mc.thePlayer.getDistanceToEntity(entity) >= this.autoBlockRange.getCurrent())) return false;
        return (entity instanceof EntityPlayer && this.players.isEnabled()) || ((entity instanceof EntityVillager || entity instanceof EntityAnimal) && this.passives.isEnabled()) || (entity instanceof EntityMob && this.mobs.isEnabled());
    }
}