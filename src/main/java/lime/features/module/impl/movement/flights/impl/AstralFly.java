package lime.features.module.impl.movement.flights.impl;

import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;

public class AstralFly extends FlightValue {
    public AstralFly() {
        super("Astral");
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer.isMoving())
            mc.timer.timerSpeed = 2.5f;
        else
            mc.timer.timerSpeed = 1f;
        if (mc.thePlayer.motionY < -0.25) {
            mc.thePlayer.motionY = 0.25;
            if (mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(0.5);
            }
        } else if (mc.thePlayer.motionY > 0.25) {
            if (mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(0.5);
            }
        }
    }
}
