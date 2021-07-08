package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

@ModuleData(name = "ESP", category = Category.RENDER)
public class ESP extends Module {

    private enum Mode {
        Box, Cylinder
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.Box);

    @EventTarget
    public void on3D(Event3D e) {
        this.setSuffix(mode.getSelected().name());
        for(Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(entity == mc.thePlayer) continue;
            if(entity instanceof EntityPlayer) {
                GL11.glPushMatrix();

                GlStateManager.enableBlend();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GlStateManager.disableTexture2D();
                GlStateManager.disableDepth();

                RenderUtils.glColor(HUD.getColor(0));
                GL11.glLineWidth(2.5f);

                double factor = entity.width - 0.15;
                double yOffset = entity.height + 0.2;

                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

                if(mode.is("box")) {
                    drawBox(x, y, z, factor, yOffset);
                }
                if(mode.is("cylinder")) {
                    drawCylinder(x, y, z, factor, yOffset);
                }

                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();

                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
    }

    public void drawBox(double x, double y, double z, double factor, double yOffset) {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x - factor, y, z + factor);
        GL11.glVertex3d(x + factor, y, z + factor);
        GL11.glVertex3d(x + factor, y, z - factor);
        GL11.glVertex3d(x - factor, y, z - factor);
        GL11.glVertex3d(x - factor, y, z + factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x - factor, y + yOffset, z + factor);
        GL11.glVertex3d(x + factor, y + yOffset, z + factor);
        GL11.glVertex3d(x + factor, y + yOffset, z - factor);
        GL11.glVertex3d(x - factor, y + yOffset, z - factor);
        GL11.glVertex3d(x - factor, y + yOffset, z + factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x - factor, y, z + factor);
        GL11.glVertex3d(x - factor, y + yOffset, z + factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x + factor, y, z + factor);
        GL11.glVertex3d(x + factor, y + yOffset, z + factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x - factor, y, z - factor);
        GL11.glVertex3d(x - factor, y + yOffset, z - factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x + factor, y, z - factor);
        GL11.glVertex3d(x + factor, y + yOffset, z - factor);
        GL11.glEnd();
    }

    public void drawCylinder(double x, double y, double z, double factor, double yOffset) {
        factor += 0.3;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i = 0; i < 361; i++) {
            GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * factor, y, z - Math.sin(Math.toRadians(i)) * factor);
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i = 0; i < 361; i++) {
            GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * factor, y + yOffset, z - Math.sin(Math.toRadians(i)) * factor);
        }
        GL11.glEnd();

        for (int i = 0; i < 361; i++) {
            if(i == 90 || i == 90 + 90 || i == 90 + 90 + 90 || i == 90 + 90 + 90 + 90) {
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * factor, y, z - Math.sin(Math.toRadians(i)) * factor);
                GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * factor, y + yOffset, z - Math.sin(Math.toRadians(i)) * factor);
                GL11.glEnd();
            }
        }
    }
}
