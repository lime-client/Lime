package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;

@ModuleData(name = "Step", category = Category.MOVEMENT)
public class Step extends Module {

    private enum Mode {
        VANILLA
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.VANILLA);
    private final SlideValue height = new SlideValue("Height", this, 1, 5, 2.5, 0.1);

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.65f;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(mode.is("vanilla")) mc.thePlayer.stepHeight = (float) height.getCurrent();
    }
}
