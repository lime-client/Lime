package lime.core.events.impl;

import lime.core.events.Event;
import net.minecraft.util.Timer;

public class EventGameLoop extends Event {
    private final Timer timer;

    public EventGameLoop(Timer timer) {
        this.timer = timer;
    }

    public Timer getTimer() {
        return timer;
    }
}
