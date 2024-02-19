package lime.ui.notifications;

import lime.management.FontManager;
import lime.utils.other.ChatUtils;
import lime.utils.other.MathUtils;
import lime.utils.other.Timer;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class Notification {
    public enum Type {
        FAIL, WARNING, SUCCESS, INFORMATION
    }

    private final String name;
    private final int time, width, height;
    private final Type type;
    private final Timer timer;
    private final Animate animation;

    public Notification(String name, int time, Type type) {
        this.name = name;
        this.type = type;
        this.timer = new Timer();
        timer.reset();
        this.time = time * 1000;
        this.width = FontManager.ProductSans20.getFont().getStringWidth(ChatUtils.removeColors(name)) + 24;
        this.height = 28;
        this.animation = new Animate().setEase(Easing.CUBIC_OUT).setSpeed(500).setValue(-1);
    }

    public Notification(String name, Type type) {
        this(name, 3, type);
    }

    public void drawNotification(int x, int y) {
        if(animation.getValue() == -1) {
            animation.setReversed(false).setMin(0).setMax(getWidth());
            animation.reset();
            animation.setValue(animation.getMax());
        }/*
        animation.update();
        x -= (int) animation.getValue() - getWidth();

        if(timer.hasReached(time)) {
            animation.setReversed(true);
        }

        RenderUtils.drawRoundedRect(x,y,getWidth(),getHeight(), 15, new Color(41, 41, 41, 240).getRGB());
        RenderUtils.drawImage(new ResourceLocation("lime/images/" + type.name().toLowerCase() + ".png"), (int) x - 4, y - 4, 16, 16);
        FontManager.ProductSans20.getFont().drawStringWithShadow(getName(), x + 13.5f, y, -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow(getDisplayName(), x + 1.5f, y + FontManager.ProductSans20.getFont().getFontHeight(), -1);*/
        animation.update();
        x -= (int) animation.getValue() - getWidth();
        if(timer.hasReached(time)) {
            animation.setReversed(true);
        }

        Gui.drawRect(x, y, x+getWidth(), y+getHeight(), new Color(0, 0, 0, 150).getRGB());
        RenderUtils.drawImage(new ResourceLocation("lime/images/" + type.name().toLowerCase() + ".png"), x + 4, y + (getHeight() / 2F) - (16 / 2F), 16, 16);
        Gui.drawRect(x, y + getHeight() - 2, x + Math.min(MathUtils.scale(timer.getTimeElapsed(), 0, time, 0, getWidth()), getWidth()), y + getHeight(), -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow(getName(), x + 20, y + 8, -1);
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return animation.isReversed() && timer.hasReached(time) && animation.getValue() == animation.getMin();
    }

    public int getTime() {
        return time;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Timer getTimer() {
        return timer;
    }

    public Type getType() {
        return type;
    }
}
