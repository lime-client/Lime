package lime.features.module.impl.combat;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.render.HUD;
import lime.features.setting.impl.SlideValue;
import lime.ui.targethud.impl.AstolfoTargetHUD;
import lime.ui.targethud.impl.LimeTargetHUD;
import lime.utils.movement.pathfinder.CustomVec;
import lime.utils.movement.pathfinder.utils.PathComputer;
import lime.utils.other.Timer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@ModuleData(name = "TP Aura", category = Category.COMBAT)
public class TPAura extends Module {

    private final SlideValue cps = new SlideValue("CPS", this, 1, 20, 5, 1);
    private final SlideValue _targets = new SlideValue("Targets", this, 1, 10, 1, 1);
    private final SlideValue range = new SlideValue("Range", this, 5, 100, 50, 5);

    private final Timer cpsTimer = new Timer();
    private ArrayList<CustomVec> lastPath = new ArrayList<>();
    private ArrayList<CustomVec>[] paths = new ArrayList[50];
    private ArrayList<Entity> targets = new ArrayList<>();

    // TargetHUD
    private final LimeTargetHUD limeTargetHUD = new LimeTargetHUD();
    private final AstolfoTargetHUD astolfoTargetHUD = new AstolfoTargetHUD();

    @EventTarget
    public void on3D(Event3D e) {
        if(targets.isEmpty()) return;
        for(int i = 0; i < Math.min(targets.size(), (int) _targets.getCurrent()); ++i) {
            if(targets.get(i) == null) {
                continue;
            }
            Entity entity = targets.get(i);
            GL11.glPushMatrix();

            GlStateManager.enableBlend();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();

            GL11.glColor4f(1, 1, 1, 1);
            GL11.glLineWidth(2.5f);

            GL11.glBegin(GL11.GL_LINE_STRIP);

            double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
            double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
            double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

            GL11.glVertex3d(x,y,z);

            for (CustomVec customVec : paths[i]) {
                GL11.glVertex3d(customVec.getX() - mc.getRenderManager().viewerPosX, customVec.getY() - mc.getRenderManager().viewerPosY, customVec.getZ() - mc.getRenderManager().viewerPosZ);
            }

            GL11.glVertex3d(entity.posX - mc.getRenderManager().viewerPosX, entity.posY - mc.getRenderManager().viewerPosY, entity.posZ - mc.getRenderManager().viewerPosZ);

            GL11.glEnd();

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            GL11.glPopMatrix();
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        HUD hud = (HUD) Lime.getInstance().getModuleManager().getModule("HUD");
        if(!targets.isEmpty()) {
            int i = 0;
            for (Entity entity1 : targets) {
                EntityPlayer entity = (EntityPlayer) entity1;
                if(entity != null && entity.isEntityAlive() && this.isToggled() && mc.thePlayer.getDistanceToEntity(entity1) <= range.getCurrent() && (entity.canEntityBeSeen(mc.thePlayer)) && i+1 <= _targets.intValue()){
                    switch(hud.targetHud.getSelected().toLowerCase()) {
                        case "lime":
                            limeTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70) + (i * 50), getColor(Math.round(entity.getHealth())));
                            break;
                        case "astolfo":
                            astolfoTargetHUD.draw(entity, (float) hud.targetHudX.getCurrent() / 100f * (e.getScaledResolution().getScaledWidth() - 174), (float) hud.targetHudY.getCurrent() / 100f * (e.getScaledResolution().getScaledHeight() - 70) + (i * 75), getColor(Math.round(entity.getHealth())));
                            break;
                    }
                }
                ++i;
            }
        }
    }

    private int getColor(int count) {
        float f1 = 20;
        float f2 = Math.max(0.0F, Math.min((float) count, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(cpsTimer.hasReached(20 / cps.intValue() * 50)) {
            ArrayList<Entity> entities = getTargets();
            this.targets = entities;
            if(!entities.isEmpty()) {
                for (int i = 0; i < Math.min(entities.size(), (int) _targets.getCurrent()); ++i) {
                    Entity entity = entities.get(i);
                    if(entity != null) {
                        CustomVec from = new CustomVec(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                        CustomVec to = new CustomVec(entity.posX, entity.posY, entity.posZ);

                        ArrayList<CustomVec> path = PathComputer.computePath(from, to);
                        this.paths[i] = path;

                        for (CustomVec customVec : path) {
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(customVec.getX(), customVec.getY(), customVec.getZ(), true));
                        }

                        mc.playerController.attackEntity(mc.thePlayer, entity);
                        mc.thePlayer.swingItem();

                        ArrayList<CustomVec> pathReversed = new ArrayList<>(path);
                        Collections.reverse(pathReversed);

                        for (CustomVec customVec : pathReversed) {
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(customVec.getX(), customVec.getY(), customVec.getZ(), true));
                        }
                        cpsTimer.reset();
                    } else
                        paths[i].clear();
                }
            }

        }
    }

    private ArrayList<Entity> getTargets() {
        ArrayList<Entity> entities = new ArrayList<>();
        for(Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(entity instanceof EntityPlayer && entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) <= range.getCurrent()) {
                entities.add(entity);
            }
        }
        if(entities.isEmpty()) return entities;
        entities.sort(Comparator.comparingDouble(ent -> mc.thePlayer.getDistanceToEntity(ent)));
        return entities;
    }
}
