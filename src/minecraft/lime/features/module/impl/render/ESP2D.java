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
import org.lwjgl.opengl.GL11;

@ModuleData(name = "ESP2D", category = Category.RENDER)
public class ESP2D extends Module
{
    @EventTarget
    public void on3D(Event3D e)
    {
        for(Entity entity : mc.theWorld.getLoadedEntityList())
        {
            if(entity instanceof EntityPlayer && entity != mc.thePlayer)
            {
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;

                float customScale = 0.2f / 8;

                GL11.glPushMatrix();
                GlStateManager.translate(x, y + entity.height + .3, z);
                GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
                GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);

                //GlStateManager.scale(-customScale, -customScale, customScale);
                GlStateManager.enableTexture2D();
                GlStateManager.disableDepth();
                GL11.glColor4f(1, 1, 1, 1);
                //fill Gui.drawRect((entity.width / 2) + 0.3, 0, -entity.width + 0, -entity.height - 0.4, -1);


                //left
                Gui.drawRect((entity.width / 2) + 0.3, 0, (entity.width / 2) + 0.2, -entity.height - 0.4, -1);
                //right
                Gui.drawRect(-entity.width, 0, -entity.width + 0.1, -entity.height - 0.4, -1);
                //up
                //Gui.drawRect(-entity);
                GlStateManager.enableDepth();
                GL11.glPopMatrix();
            }
        }
    }
}
