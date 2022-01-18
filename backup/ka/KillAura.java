package lime.features.module.impl.combat;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventWorldChange;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.killaura.Multi;
import lime.features.module.impl.combat.killaura.Single;
import lime.features.module.impl.render.HUD;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.ui.notifications.Notification;
import lime.ui.targethud.impl.AstolfoTargetHUD;
import lime.ui.targethud.impl.Lime2TargetHUD;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class KillAura extends Module {

    public KillAura() {
        super("Kill Aura", Category.COMBAT);
    }

    // Settings
    public final EnumProperty state = new EnumProperty("State", this, "PRE", "PRE", "POST");
    private final EnumProperty mode = new EnumProperty("Mode", this, "Single", "Single", "Multi");
    private final EnumProperty priority = new EnumProperty("Priority", this, "Distance", "Distance", "Health", "FOV");
    public final EnumProperty rotations = new EnumProperty("Rotations", this, "Basic", "None", "Basic", "Smooth");
    private final EnumProperty targetEsp = new EnumProperty("Target ESP", this, "Circle", "None", "Circle");
    public final EnumProperty autoBlock = new EnumProperty("Auto Block", this, "Fake", "None", "Basic", "Still", "Fake");
    public final EnumProperty autoBlockState = new EnumProperty("Auto Block State", this, "POST", "PRE", "POST");
    public final NumberProperty rotationsSpeedMin = new NumberProperty("Rotations Min", this, 5, 100, 50, 1).onlyIf(rotations.getSettingName(), "enum", "smooth");
    public final NumberProperty rotationsSpeedMax = new NumberProperty("Rotations Max", this, 5, 100, 90, 1).onlyIf(rotations.getSettingName(), "enum", "smooth");
    private final NumberProperty autoBlockRange = new NumberProperty("Auto Block Range", this, 2.8, 12, 8, 0.05).onlyIf(autoBlock.getSettingName(), "enum", "basic", "still", "fake");
    public final NumberProperty range = new NumberProperty("Range", this, 2.8, 6, 4.2, 0.05);
    public final NumberProperty cps = new NumberProperty("CPS", this, 1, 20, 8, 1);
    public final NumberProperty randomizeCps = new NumberProperty("Randomize CPS", this, 0, 5, 3, 1);
    public final NumberProperty randomizeYaw = new NumberProperty("Randomize Yaw", this, 0, 5, 3, 1).onlyIf(rotations.getSettingName(), "enum", "Smooth", "Basic");
    private final BooleanProperty players = new BooleanProperty("Players", this, true);
    private final BooleanProperty passives = new BooleanProperty("Passives", this, false);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", this, true);
    private final BooleanProperty teams = new BooleanProperty("Teams", this, true);
    public final BooleanProperty rayCast = new BooleanProperty("Ray Cast", this, false);
    private final BooleanProperty throughWalls = new BooleanProperty("Through Walls", this, true);
    public final BooleanProperty keepSprint = new BooleanProperty("Keep Sprint", this, true);
    public final BooleanProperty gcd = new BooleanProperty("GCD", this, false);
    private final BooleanProperty deathCheck = new BooleanProperty("Death Check", this, true);
    public final BooleanProperty particles = new BooleanProperty("Particles", this, false);

    public static EntityLivingBase entity;

    // AutoBlock
    public static boolean isBlocking = false;

    // TargetHUD
    public final LimeTargetHUD limeTargetHUD = new LimeTargetHUD();
    public final AstolfoTargetHUD astolfoTargetHUD = new AstolfoTargetHUD();
    public Lime2TargetHUD lime2TargetHUD = new Lime2TargetHUD();

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
        lime2TargetHUD.reset();
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
        Lime.getInstance().getNotificationManager().addNotification("Disabled Kill Aura because you changed world!", Notification.Type.WARNING);
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

    public boolean hasSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public void sortEntities(ArrayList<EntityLivingBase> entities, boolean distance) {
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

        if(distance) {
            entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
        }
    }

    public boolean isValid(Entity entity) {
        AntiBot antiBot = Lime.getInstance().getModuleManager().getModuleC(AntiBot.class);
        if(entity instanceof EntityPlayer && antiBot.checkBot((EntityPlayer) entity)) return false;
        if(Lime.getInstance().getFriendManager().isFriend(entity)) return false;
        if(teams.isEnabled() && entity instanceof EntityLivingBase && mc.thePlayer.isOnSameTeam((EntityLivingBase) entity)) return false;
        if((deathCheck.isEnabled() && !entity.isEntityAlive()) || (!mc.thePlayer.canEntityBeSeen(entity) && !throughWalls.isEnabled())) return false;
        if(autoBlock.is("none") && mc.thePlayer.getDistanceToEntity(entity) >= this.range.getCurrent()) return false;
        if((!autoBlock.is("none") && mc.thePlayer.getDistanceToEntity(entity) >= this.autoBlockRange.getCurrent())) return false;
        if(!autoBlock.is("none") && mc.thePlayer.getDistanceToEntity(entity) <= this.autoBlockRange.getCurrent() && !hasSword() && mc.thePlayer.getDistanceToEntity(entity) >= this.range.getCurrent()) return false;
        return (entity instanceof EntityPlayer && this.players.isEnabled()) || ((entity instanceof EntityVillager || entity instanceof EntityAnimal) && this.passives.isEnabled()) || (entity instanceof EntityMob && this.mobs.isEnabled());
    }
}