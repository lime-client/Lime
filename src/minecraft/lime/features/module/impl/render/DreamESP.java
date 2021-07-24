package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@ModuleData(name = "DreamESP", category = Category.RENDER)
public class DreamESP extends Module {
    @EventTarget
    public void on3D(Event3D e) {
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(entity instanceof EntityPlayer && entity != mc.thePlayer) {
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;


                GL11.glPushMatrix();
                GlStateManager.translate(x, y + entity.height + .6, z);
                GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
                GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);
                GlStateManager.scale(-.025, -.025, .025);
                GlStateManager.enableTexture2D();

                GlStateManager.disableDepth();
                GL11.glColor4f(1, 1, 1, 1);
                mc.getTextureManager().bindTexture(new ResourceLocation("lime/dream.png"));
                Gui.drawModalRectWithCustomSizedTexture(-25, 10, 0, 0, 48, 96, 48, 96);
                GlStateManager.enableDepth();

                GL11.glPopMatrix();
            }
        }
    }
}