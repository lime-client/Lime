package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;

@ModuleData(name = "No Fall", category = Category.PLAYER)
public class NoFall extends Module {

    private enum Mode {
        VANILLA, VERUS
    }

    private EnumValue mode = new EnumValue("Mode", this, Mode.VERUS);

    @EventTarget
    public void onUpdate(EventMotion e) {
        if(e.isPre()) {
            if(mc.thePlayer.fallDistance > 2.5) {
                e.setGround(true);
                if(mode.is("verus")) mc.thePlayer.motionY = -0.0784000015258789;
                mc.thePlayer.fallDistance = 0;
            }
        }
    }
}
