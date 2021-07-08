package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.SlideValue;

import java.util.concurrent.ThreadLocalRandom;

@ModuleData(name = "Derp", category = Category.PLAYER)
public class Derp extends Module {
    private final SlideValue rotationsSpeed = new SlideValue("Rotations Speed", this, 1, 100, 50, 1);
    private final BoolValue sneak = new BoolValue("Sneak", this, true);

    private float yaw = -180;

    @Override
    public void onDisable() {
        yaw = -180;
    }


    @EventTarget
    public void onMotion(EventMotion e) {
        if(sneak.isEnabled()) {
            e.setSneak(ThreadLocalRandom.current().nextBoolean());
        }

        if(yaw + rotationsSpeed.intValue() > 180) {
            yaw = -180;
        }

        yaw += rotationsSpeed.intValue();

        e.setYaw(yaw);
        e.setPitch(-180);

        mc.thePlayer.setRotationsTP(e);
    }
}
