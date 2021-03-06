package lime.module.impl.render.targethuds;


import lime.Lime;
import lime.module.impl.render.TargetHUD;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class AstolfoTargetHUD extends TargetHUD {
    private float healthAnimated;
    private float healthAnimated1;

    public AstolfoTargetHUD() {
        super(174, 70, "Astolfo");
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

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);

        //background
        Gui.drawRect(0, 0, getWidth(), getHeight(), 0xAA000000);

        //health bar
        Gui.drawRect(46, getHeight() - 7, getWidth() - 2, getHeight() - 2, 0x90000000);

        if (target.getMaxHealth() >= target.getHealth()) {
            Gui.drawRect(46 + (healthAnimated * (getWidth() - 48) / target.getMaxHealth()), getHeight() - 7,
                    46 + (healthAnimated1 * (getWidth() - 48) / target.getMaxHealth()), getHeight() - 2,
                    new Color(color).darker().darker().getRGB());

            Gui.drawRect(46, getHeight() - 7, 46 + (healthAnimated * (getWidth() - 48) / target.getMaxHealth()),
                    getHeight() - 2, color);
        }

        //drawing the entity
        GuiInventory.drawEntityOnScreen(24, 68, 32, -30.0F, 0.0F, target);

        //target name
        Lime.fontManager.roboto_sense.drawStringWithShadow(target.getName(), 48, 2, -1);

        //target health
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.5, 2.5, 0);
        GlStateManager.translate(54 / 2.5f, 18 / 2.5f, 0);
        mc.fontRendererObj.drawStringWithShadow((Math.round(target.getHealth() * 10f) / 10f + "❤").replace(".0", ""), -2,
                0, color);
        GlStateManager.popMatrix();

        //armor
        int itemsX = getWidth() - 22;
        int itemsY = 0;

        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(3), itemsX, itemsY);
        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(2), itemsX, itemsY + 15);
        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(1), itemsX, itemsY + 31);
        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(0), itemsX, itemsY + 47);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    public void resetHealthAnimated() {
        healthAnimated = 0;
        healthAnimated1 = 0;
    }
}
