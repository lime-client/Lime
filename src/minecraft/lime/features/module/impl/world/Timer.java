package lime.features.module.impl.world;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.NumberProperty;

public class Timer extends Module {

    public Timer() {
        super("Timer", Category.WORLD);
    }

    private final NumberProperty timerSpeed = new NumberProperty("Timer Speed", this, 0.1, 10, 1.5, 0.1);

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        mc.timer.timerSpeed = (float) timerSpeed.getCurrent();
    }
}
