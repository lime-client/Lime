package lime.ui.notifications;

import lime.core.Lime;
import lime.core.events.impl.Event2D;
import lime.managers.FontManager;
import lime.utils.other.ChatUtils;
import lime.utils.other.Timer;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Notification {
    public enum Type {
        ERROR, SUCCESS, WARNING, INFORMATION
    }

    private final Animate animationY;
    private int lastKnownY;

    private final String name, information;
    private final Enum _enum;
    private final int seconds;

    private final Timer timer;
    private final Animate animate;

    private boolean finished;

    public Notification(String name, String information, int seconds, Type type) {
        this.name = name;
        this.information = information;
        this._enum = type;
        this.seconds = seconds;
        this.timer = new Timer();
        this.timer.reset();

        this.animate = new Animate();
        animate.setEase(Easing.CUBIC_OUT);
        animate.setSpeed(300);
        animate.setMin(0);
        animate.setMax(FontManager.ProductSans20.getFont().getStringWidth(ChatUtils.removeColors(information)) + 48);

        this.animationY = new Animate();
        animationY.setEase(Easing.CUBIC_OUT);
        animationY.setSpeed(300);
        animationY.setMin(0);
        lastKnownY = -1;
    }

    public Notification(String name, String information, Type type) {
        this(name, information, 3, type);
    }

    public void render(Event2D e, int yOffset) {
        if(lastKnownY == -1)
            lastKnownY = yOffset;

        if(lastKnownY != yOffset) {

        }
        animate.update();
        animationY.update();
        RenderUtils.drawBluredRect(e.getScaledResolution().getScaledWidth() - animate.getValue(), lastKnownY != yOffset ? animationY.getValue() : yOffset, e.getScaledResolution().getScaledWidth() - animate.getValue() + FontManager.ProductSans20.getFont().getStringWidth(ChatUtils.removeColors(information)) + 44, (lastKnownY != yOffset ? animationY.getValue() : yOffset) + 36, new Color(25, 25, 25, 200).getRGB(), 10);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("lime/images/" + getType().name().toLowerCase() + ".png"));
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.resetColor();
        Gui.drawModalRectWithCustomSizedTexture((e.getScaledResolution().getScaledWidth() - animate.getValue() + 2) * 2, (yOffset + 2)  * 2, 0, 0, 64, 64, 64, 64);
        GL11.glPopMatrix();
        FontManager.ProductSans24.getFont().drawStringWithShadow(name, e.getScaledResolution().getScaledWidth() - animate.getValue() + 36, yOffset, -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow(information, e.getScaledResolution().getScaledWidth() - animate.getValue() + 36, yOffset + FontManager.ProductSans24.getFont().getFontHeight(), -1);

        if(timer.hasReached(getSeconds() * 1000L)) {
            animate.setReversed(true);

            if(animate.getValue() == animate.getMin()) {
                finished = true;
            }
        }

        if(animationY.getValue() == animationY.getMax()) {
            lastKnownY = yOffset;
        }
    }


    public String getName() {
        return name;
    }

    public String getInformation() {
        return information;
    }

    public Enum getType() {
        return _enum;
    }

    public int getSeconds() {
        return seconds;
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean isFinished() {
        return finished;
    }
}
