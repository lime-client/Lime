package lime.ui.gui;

import lime.utils.other.MathUtils;
import lime.utils.other.Timer;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class ProcessBar {
    private final Timer timer;
    private final int x, y, duration;

    public ProcessBar(int x, int y, int duration) {
        timer = new Timer();
        timer.reset();
        this.x = x;
        this.y = y;
        this.duration = duration;
    }

    public void draw() {
        if(!timer.hasReached(duration)) {
            Gui.drawRect(x, y - 3, x + 50, y + 3, new Color(0, 0, 0, 150).getRGB());
            Gui.drawRect(x + 1, y - 2, x + MathUtils.scale(timer.getTimeElapsed(), 0, duration, 0, 48), y + 2, new Color(255, 255, 255, 200).getRGB());
        }
    }

    public Timer getTimer() {
        return timer;
    }
}
