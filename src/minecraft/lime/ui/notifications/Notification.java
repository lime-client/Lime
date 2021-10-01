package lime.ui.notifications;

import lime.managers.FontManager;
import lime.utils.other.ChatUtils;
import lime.utils.other.Timer;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class Notification {
    public enum Type {
        FAIL, WARNING, SUCCESS, INFORMATION
    }

    private final String name, displayName;
    private final int time, width, height;
    private final Type type;
    private final Timer timer;
    private final Animate animation;

    public Notification(String name, String displayName, int time, Type type) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.timer = new Timer();
        timer.reset();
        this.time = time * 1000;
        this.width = Math.max(FontManager.ProductSans20.getFont().getStringWidth(ChatUtils.removeColors(displayName) + 3), FontManager.ProductSans20.getFont().getStringWidth(ChatUtils.removeColors(name)));
        this.height = 28;
        this.animation = new Animate().setEase(Easing.CUBIC_OUT).setSpeed(100).setValue(-1);
    }

    public Notification(String name, String displayName, Type type) {
        this(name, displayName, 5, type);
    }

    public void drawNotification(int x, int y) {
        if(animation.getValue() == -1) {
            animation.setReversed(false).setMin(0).setMax(getWidth());
            animation.reset();
            animation.setValue(animation.getMax());
        }
        animation.update();
        x -= (int) animation.getValue() - getWidth();

        if(timer.hasReached(time)) {
            animation.setReversed(true);
        }

        RenderUtils.drawRoundedRect(x,y,getWidth(),getHeight(), 15, new Color(41, 41, 41, 240).getRGB());
        RenderUtils.drawImage(new ResourceLocation("lime/images/" + type.name().toLowerCase() + ".png"), (int) x - 4, y - 4, 16, 16);
        FontManager.ProductSans20.getFont().drawStringWithShadow(getName(), x + 13.5f, y, -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow(getDisplayName(), x + 1.5f, y + FontManager.ProductSans20.getFont().getFontHeight(), -1);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
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
