package lime.module.impl.render;

import lime.events.EventTarget;
import lime.events.impl.Event3D;
import lime.module.Module;
import lime.utils.render.RainbowUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tracers extends Module {
    public Tracers(){
        super("Tracers", 0, Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    @EventTarget
    public void onRender3D(Event3D event){
        for(Entity ent :mc.theWorld.getLoadedEntityList()){
            if(ent instanceof EntityPlayer && ent != mc.thePlayer && !(ent instanceof EntityArmorStand) && ent instanceof EntityLivingBase){
                float posX = (float) ((float) (ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * event.getPartialTicks()) - mc.getRenderManager().renderPosX);
                float posY = (float) ((float) (ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * event.getPartialTicks()) - mc.getRenderManager().renderPosY);
                float posZ = (float) ((float) (ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * event.getPartialTicks()) - mc.getRenderManager().renderPosZ);
                draw3DLine(posX, posY, posZ, RainbowUtil.blend2colors(new Color(255, 0, 0, 255), new Color(100, 0, 0, 255), (System.nanoTime() + (100000000L * 2)) / 1.0E09F % 2.0F).getRGB());
            }
        }
    }

    public static void draw3DLine(double x, double y, double z, int color) {

        pre3D();
        GL11.glLoadIdentity();
        Minecraft.getMinecraft().entityRenderer.orientCamera(Minecraft.getMinecraft().timer.renderPartialTicks);
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(var6, var7, var8, var11);
        GL11.glLineWidth(1.5f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(0, Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        post3D();
    }
    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
    }
}
