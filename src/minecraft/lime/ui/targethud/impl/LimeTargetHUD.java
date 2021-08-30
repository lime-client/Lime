package lime.ui.targethud.impl;


import lime.managers.FontManager;
import lime.ui.targethud.TargetHUD;
import lime.utils.other.MathUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

public class LimeTargetHUD extends TargetHUD {
    private float healthAnimated;
    private float healthAnimated1;
    private float armorAnimated;
    private float armorAnimated1;

    public LimeTargetHUD() {
        super(150, 45, "Lime");
    }

    @Override
    public void draw(EntityLivingBase target, float x, float y, int color) {
        if (target == null)
            return;

        if (healthAnimated > target.getMaxHealth())
            healthAnimated = target.getMaxHealth();

        if (target.getMaxHealth() >= target.getHealth()) {
            if (target.getHealth() > healthAnimated) {
                if (target.getHealth() > healthAnimated + 0.4)
                    healthAnimated += 0.4;
                else
                    healthAnimated = target.getHealth();
            }

            if (target.getHealth() < healthAnimated) {
                if (target.getHealth() < healthAnimated / 1.1)
                    healthAnimated /= 1.1;
                else
                    healthAnimated = target.getHealth();
            }

            if (target.getHealth() > healthAnimated1)
                healthAnimated1 = target.getHealth();

            if (target.getHealth() < healthAnimated1) {
                if (target.getHealth() < healthAnimated1 / 1.025)
                    healthAnimated1 /= 1.025;
                else
                    healthAnimated1 = target.getHealth();
            }
        } else
            resetHealthAnimated();


        if(target instanceof EntityPlayer) {
            if (armorAnimated > 20)
                armorAnimated = 20;

            if (20 >= target.getTotalArmorValue()) {
                if (target.getTotalArmorValue() > armorAnimated) {
                    if (target.getTotalArmorValue() > armorAnimated + 0.4)
                        armorAnimated += 0.4;
                    else
                        armorAnimated = target.getTotalArmorValue();
                }

                if (target.getTotalArmorValue() < armorAnimated) {
                    if (target.getTotalArmorValue() < armorAnimated / 1.1)
                        armorAnimated /= 1.1;
                    else
                        armorAnimated = target.getTotalArmorValue();
                }

                if (target.getTotalArmorValue() > armorAnimated1)
                    armorAnimated1 = target.getTotalArmorValue();

                if (target.getTotalArmorValue() < armorAnimated1) {
                    if (target.getTotalArmorValue() < armorAnimated1 / 1.025)
                        armorAnimated1 /= 1.025;
                    else
                        armorAnimated1 = target.getTotalArmorValue();
                }
            } else
                resetArmorAnimated();

        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);

        Gui.drawRect(-1, -1, getWidth() + 1, getHeight() + 1, new Color(20, 20, 20).getRGB());

        //background
        Gui.drawRect(0, 0, getWidth(), getHeight(), new Color(40, 40, 40).getRGB());

        //health bar
        Gui.drawRect(46, getHeight() - 7 - 20, getWidth() - 2, getHeight() - 6 - 20, 0x90000000);



        if (target.getMaxHealth() >= target.getHealth()) {
            Gui.drawRect(46 + (healthAnimated * (getWidth() - 48) / target.getMaxHealth()), getHeight() - 7 - 20,
                    46 + (healthAnimated1 * (getWidth() - 48) / target.getMaxHealth()), getHeight() - 6 - 20,
                    new Color(color).darker().darker().getRGB());

            Gui.drawRect(46, getHeight() - 7 - 20, 46 + (healthAnimated * (getWidth() - 48) / target.getMaxHealth()),
                    getHeight() - 6 - 20, color);
        }

        if (target.getTotalArmorValue() > 0 && target instanceof EntityPlayer) {
            Gui.drawRect(46, getHeight() - 7 - 13, getWidth() - 2, getHeight() - 6 - 13, 0x90000000);
            Gui.drawRect(46 + (armorAnimated * (getWidth() - 48) / 20), getHeight() - 7 - 13,
                    46 + (armorAnimated1 * (getWidth() - 48) / 20), getHeight() - 6 - 13,
                    new Color(10, 20, 190).darker().darker().getRGB());

            Gui.drawRect(46, getHeight() - 7 - 13, 46 + (armorAnimated * (getWidth() - 48) / 20),
                    getHeight() - 6 - 13, new Color(10, 20, 190).getRGB());
        }

        //drawing the entity
        GlStateManager.color(1, 1, 1);
        if(target instanceof AbstractClientPlayer) {
            RenderUtils.drawFace(2, 2, 40, 40, (AbstractClientPlayer) target);
        }

        //target name
        FontManager.ProductSans20.getFont().drawString(target.getName().replace("♥", ""), 46, 0, -1, true);
        if(!Float.isNaN(target.getHealth())) {
            FontManager.ProductSans20.getFont().drawString(MathUtils.roundToPlace(target.getHealth(), 2) + "", 46, 30, color, true);
        }

        //armor
        int itemsX = getWidth() - 57;
        int itemsY = getHeight() - 15;
        GlStateManager.scale(0.95, 0.95, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(3), itemsX, itemsY);
        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(2), itemsX + 15, itemsY);
        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(1), itemsX + 31, itemsY);
        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(0), itemsX + 47, itemsY);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public void resetHealthAnimated() {
        healthAnimated = 0;
        healthAnimated1 = 0;
    }

    public void resetArmorAnimated() {
        armorAnimated1 = 0;
        armorAnimated = 0;
    }
}
