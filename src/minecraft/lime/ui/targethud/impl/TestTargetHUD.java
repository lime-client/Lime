package lime.ui.targethud.impl;

import lime.managers.FontManager;
import lime.ui.targethud.TargetHUD;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TestTargetHUD extends TargetHUD {
    public TestTargetHUD(int width, int height) {
        super(width, height, "Test");
        this.animation = new Animate().setSpeed(50).setEase(Easing.CUBIC_IN).setMin(0);
        this.animation1 = new Animate().setSpeed(25).setEase(Easing.CUBIC_IN).setMin(0);
    }

    private final Animate animation, animation1;

    @Override
    public void draw(EntityLivingBase target, float x, float y, int color) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);

        int entityWidth = 15;

        animation.setMax(getWidth() + entityWidth - 3).update();
        animation1.setMax(getWidth() + entityWidth - 3).update();

        RenderUtils.drawHollowBox(-1, -1, getWidth() + 1, getHeight(), 1, new Color(25, 25, 25).getRGB());
        Gui.drawRect(0, 0, getWidth(), getHeight(), new Color(41, 41, 41).getRGB());

        if(!Float.isNaN(target.getHealth())) {
            FontManager.ProductSans20.getFont().drawStringWithShadow(target.getHealth()+"", getWidth() - FontManager.ProductSans20.getFont().getStringWidth(target.getHealth() + "") - 2, -1, color);
        }

        if(target instanceof AbstractClientPlayer) {
            FontManager.ProductSans20.getFont().drawStringWithShadow(target.getName(), 38, -1, -1);
            RenderUtils.drawFace(1, 1, 34, 34, (AbstractClientPlayer) target);
        }

        GL11.glPopMatrix();
    }

    public void reset() {
        this.animation.reset();
        this.animation1.reset();
    }
}
