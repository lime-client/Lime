package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.NumberProperty;

import java.util.concurrent.ThreadLocalRandom;

public class Derp extends Module {

    public Derp() {
        super("Derp", Category.PLAYER);
    }

    private final NumberProperty rotationsSpeed = new NumberProperty("Rotations Speed", this, 1, 100, 50, 1);
    private final BooleanProperty headless = new BooleanProperty("Headless", this, true);
    private final BooleanProperty sneak = new BooleanProperty("Sneak", this, true);

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
        if(headless.isEnabled()) {
            e.setPitch(-180);
        }

        mc.thePlayer.setRotationsTP(e);
    }
}
