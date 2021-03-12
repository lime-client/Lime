package lime.module.impl.render;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.Event3D;
import lime.events.impl.EventNameTags;
import lime.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Nametags extends Module {
    public Nametags() {
        super("Nametags", 0, Module.Category.RENDER);
        Lime.setmgr.rSetting(new Setting("Nametags Health", this, true));
        Lime.setmgr.rSetting(new Setting("Nametags Scale", this, 1, 0.5, 3, false));
        Lime.setmgr.rSetting(new Setting("Nametags Auto Scale", this, true));
        Lime.setmgr.rSetting(new Setting("Nametags Players", this, true));
        Lime.setmgr.rSetting(new Setting("Nametags Creatures", this, false));
        Lime.setmgr.rSetting(new Setting("Nametags Villagers", this, false));
        Lime.setmgr.rSetting(new Setting("Nametags Invisibles", this, false));
    }



    @EventTarget
    public void onRender3D(Event3D e) {
        ArrayList<Object> sorted = new ArrayList<>(Arrays.asList(mc.theWorld.loadedEntityList.stream().sorted(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer))).toArray()));

        Collections.reverse(sorted);

        for (Object entity : sorted) {
            if (entity instanceof EntityLivingBase && isValid((EntityLivingBase) entity)) {
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);

                renderNametag((EntityLivingBase) entity, ((EntityLivingBase) entity).getName());

                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.color(1, 1, 1, 1);
            }
        }
    }

    protected void renderNametag(EntityLivingBase entity, String str) {
        Color color = new Color(0xBF1212);

        if (entity.getHealth() <= entity.getMaxHealth() && entity.getHealth() > entity.getMaxHealth() / 4 * 3)
            color = new Color(0x0FE53F);
        else if (entity.getHealth() >= entity.getMaxHealth() / 4 * 3)
            color = new Color(0xFFFF00);
        else if (entity.getHealth() >= entity.getMaxHealth() / 4 * 2)
            color = new Color(0xF8B200);
        else if (entity.getHealth() >= entity.getMaxHealth() / 4)
            color = new Color(0xDB4307);
        else if (entity.getHealth() >= entity.getMaxHealth() / 8)
            color = new Color(0xBF1212);



        float scale = 0.025f;
        double distance = Math.max(mc.thePlayer.getDistanceToEntity(entity) / 10, 1.3);
        String distanceText = Math.round(mc.thePlayer.getDistanceToEntity(entity)) + "m";
        boolean auto = getSettingByName("Nametags Auto Scale").getValBoolean();
        double customScale = getSettingByName("Nametags Scale").getValDouble() / 8f;
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
        float textY = getSettingByName("Nametags Health").getValBoolean() ? -2 : -4;
        String string = (!EnumChatFormatting.getTextWithoutFormattingCodes(str).equals("") ? str + "    " : "");
        float center = Math.max(30, Lime.fontManager.roboto_sense.getStringWidth(string)/2f - 16);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + entity.height + 0.6, z);
        GlStateManager.rotate(-RenderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(RenderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(auto ? -scale * distance : -customScale, auto ? -scale * distance : -customScale, auto ? scale / distance : customScale);

        GlStateManager.disableTexture2D();
        //Gui.drawRect(-center + 3, -2, center - 3, getSettingByName("Nametags Health").getValBoolean() ? 12 : 10, 0xAA000000);

        if (getSettingByName("Nametags Health").getValBoolean())
            if (!(entity.getMaxHealth() < entity.getHealth()))
                //Gui.drawRect(-center, 11, -center + (entity.getHealth() * (center * 2) / entity.getMaxHealth()), 12, color.getRGB());

        GlStateManager.enableTexture2D();

        GlStateManager.scale(0.5, 0.5, 0.5);
        Lime.fontManager.roboto_sense.drawCenteredString(string + distanceText, 0, (int) textY, -1, false);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    @EventTarget
    public void onRenderNametags(EventNameTags e) {
        if(isValid((EntityLivingBase) e.getEntity())){
            e.setCancelled(true);
        }
    }

    public boolean isValid(EntityLivingBase entity) {
        boolean players = getSettingByName("Nametags Players").getValBoolean();
        boolean creatures = getSettingByName("Nametags Creatures").getValBoolean();
        boolean villagers = getSettingByName("Nametags Villagers").getValBoolean();
        boolean invisibles = getSettingByName("Nametags Invisibles").getValBoolean();
        boolean teams = false;

        return entity != null && (!(entity instanceof EntityArmorStand) && entity != mc.thePlayer && (!entity.isInvisible() || invisibles) && !entity.isDead &&
                (entity.getHealth() != 0 && (!(entity instanceof EntityAnimal || entity instanceof EntityMob || entity instanceof EntityIronGolem ||
                        entity instanceof EntitySquid || entity instanceof EntityBat) || creatures) && (!(entity instanceof EntityVillager) || villagers) &&
                        (!(entity instanceof EntityOtherPlayerMP) || (players
                                && !entity.getName().equalsIgnoreCase("[NPC]") && !entity.getName().equals("")))));
    }
}