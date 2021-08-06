package lime.features.module.impl.movement.flights.impl;

import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;

public class Vanilla extends FlightValue {
    public Vanilla()
    {
        super("Vanilla");
    }

    @Override
    public void onUpdate() {
        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? .80 : mc.gameSettings.keyBindSneak.isKeyDown() ? -.80 : 0;
        if(mc.thePlayer.isMoving()) {
            MovementUtils.setSpeed(getFlight().speed.getCurrent());
        } else {
            MovementUtils.setSpeed(0);
        }
    }
}
