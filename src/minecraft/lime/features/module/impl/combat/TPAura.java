package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.SlideValue;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@ModuleData(name = "TP Aura", category = Category.COMBAT)
public class TPAura extends Module {

    private final SlideValue _targets = new SlideValue("Targets", this, 1, 10, 1, 1);
    private final SlideValue range = new SlideValue("Range", this, 5, 100, 50, 5);

    private final Timer cpsTimer = new Timer();
    private ArrayList<CustomVec> lastPath = new ArrayList<>();
    private ArrayList<CustomVec>[] paths = new ArrayList[50];
    private ArrayList<Entity> targets = new ArrayList<>();

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
    public void onUpdate(EventUpdate e) {
        if(cpsTimer.hasReached(20 / 5 * 50)) {
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
