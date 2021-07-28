package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventMotion;
import lime.features.module.impl.movement.flights.FlightValue;

public class SurvivalDub extends FlightValue {
    public SurvivalDub() {
        super("Survival_Dub");
    }

    @Override
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            e.setY(mc.thePlayer.posY - Math.random() * (0.001 - 0.0001) + 0.0001);
            mc.thePlayer.motionY = -0.0055;
            if(mc.thePlayer.onGround)
                mc.thePlayer.jump();
            mc.timer.timerSpeed = 1.0855f;
        }
        super.onMotion(e);
    }
}
