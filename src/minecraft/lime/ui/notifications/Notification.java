package lime.ui.notifications;

import lime.core.events.impl.Event2D;
import lime.managers.FontManager;
import lime.utils.other.ChatUtils;
import lime.utils.other.MathUtils;
import lime.utils.other.Timer;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Notification {
    public enum Type {
        ERROR, SUCCESS, WARNING, INFORMATION
    }

    private final Animate animationY;
    private int lastKnownY;

    private final String name, information;
    private final Type _enum;
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
        if(lastKnownY == -1) {
            lastKnownY = yOffset;
            animationY.setMin(0);
            animationY.setMax(1);
            animationY.reset();
        }
        if(lastKnownY != yOffset) {
            animationY.setMax(yOffset - lastKnownY);
            animationY.update();
        }

        if(animationY.getValue() == yOffset) {
            lastKnownY = yOffset;
        }
        animate.update();

        float animPercentage = lastKnownY == yOffset ? yOffset : lastKnownY + animationY.getValue();
        float width = FontManager.ProductSans20.getFont().getStringWidth(ChatUtils.removeColors(information)) + 44;
        double percentage = MathUtils.scale(Math.min(timer.getTimeElapsed(), getSeconds() * 1000L), 0, getSeconds() * 1000, 0, width);
        Gui.drawRect(e.getScaledResolution().getScaledWidth() - animate.getValue(), animPercentage, e.getScaledResolution().getScaledWidth() - animate.getValue() + width, animPercentage + 36, 0xCC << 24);
        Gui.drawRect(e.getScaledResolution().getScaledWidth() - animate.getValue(), animPercentage + 34, e.getScaledResolution().getScaledWidth() - animate.getValue() + percentage, animPercentage + 36, -1);
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.resetColor();
        RenderUtils.drawImage(new ResourceLocation("lime/images/" + getType().name().toLowerCase() + ".png"), (e.getScaledResolution().getScaledWidth() - animate.getValue() + 2) * 2, (animPercentage + 2) * 2, 64, 64, true);
        GL11.glPopMatrix();
        FontManager.ProductSans24.getFont().drawStringWithShadow(name, e.getScaledResolution().getScaledWidth() - animate.getValue() + 36, animPercentage, -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow(information, e.getScaledResolution().getScaledWidth() - animate.getValue() + 36, animPercentage + FontManager.ProductSans24.getFont().getFontHeight(), -1);

        if(timer.hasReached(getSeconds() * 1000L)) {
            animate.setReversed(true);

            if(animate.getValue() == animate.getMin()) {
                finished = true;
            }
        }
    }


    public String getName() {
        return name;
    }

    public String getInformation() {
        return information;
    }

    public Type getType() {
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
