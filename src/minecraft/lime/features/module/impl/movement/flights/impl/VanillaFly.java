package lime.features.module.impl.movement.flights.impl;

import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;

public class VanillaFly extends FlightValue {
    public VanillaFly()
    {
        super("Vanilla");
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onUpdate() {
        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? .42 : mc.gameSettings.keyBindSneak.isKeyDown() ? -.42 : 0;
        if(mc.thePlayer.isMoving()) {
            MovementUtils.setSpeed(getFlight().speed.getCurrent());
        } else {
            MovementUtils.setSpeed(0);
        }
    }
}
