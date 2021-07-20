package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventMotion;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;

public class Astral extends FlightValue {
    public Astral()
    {
        super("Astral");
    }

    @Override
    public void onMotion(EventMotion e) {
        if(!e.isPre()) return;
        if(mc.thePlayer.isMoving())
            mc.timer.timerSpeed = 1.5f;
        else
            mc.timer.timerSpeed = 1;
        if(mc.thePlayer.motionY < -0.20) {
            e.setGround(true);
            mc.thePlayer.motionY = 0.2;
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(0.8);
            }
        } else if(mc.thePlayer.motionY > 0.1) {
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(0.8);
            }
        }
    }
}
