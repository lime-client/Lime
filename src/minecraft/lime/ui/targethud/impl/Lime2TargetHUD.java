package lime.ui.targethud.impl;

import lime.ui.targethud.TargetHUD;
import lime.utils.other.MathUtils;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import lime.utils.render.font2.FontManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

public class Lime2TargetHUD extends TargetHUD {
    public Lime2TargetHUD() {
        super(125, 50, "Moon");
        health = new Animate();
        health.setMin(0);
        health.setMax(0);
        health.setSpeed(100);
        health.setEase(Easing.CUBIC_OUT);
        armor = new Animate();
        armor.setMin(0);
        armor.setMax(0);
        armor.setSpeed(100);
        armor.setEase(Easing.CUBIC_OUT);
    }

    private final Animate health, armor;

    @Override
    public void draw(EntityLivingBase target, float x, float y, int color) {
        health.update();
        armor.update();
        Gui.drawRect(x,y,x+getWidth(),y+getHeight(),new Color(25, 25, 25, 175).getRGB());

        if(target instanceof EntityPlayer) {
            RenderUtils.drawFace((int)x + 3, (int)y + 3, 24, 24, (AbstractClientPlayer) target);
        }

        FontManager.SfUiArray.drawStringWithShadow(target.getName(), x+29, y+5, -1);
        health.setMax((float) MathUtils.scale(Math.min(target.getHealth(), target.getMaxHealth()), 0, target.getMaxHealth(), 0, 118));
        armor.setMax((float) MathUtils.scale(target.getTotalArmorValue(), 0, 20, 0, 118));

        Gui.drawRect(x+3, y+30, x + 123, y + 36, new Color(25, 25, 25,225).getRGB());
        Gui.drawRect(x + 4, y + 31, x + 4 + health.getValue(), y + 35, new Color(0, 255, 0).getRGB());

        if(target.getTotalArmorValue() != 0) {
            Gui.drawRect(x+3, y+39, x + 123, y + 45, new Color(25, 25, 25,225).getRGB());
            Gui.drawRect(x + 4, y + 40, x + 4 + armor.getValue(), y + 44, new Color(0, 100, 200).getRGB());
        }
    }

    public void reset() {
        health.reset();
        armor.reset();
    }
}
