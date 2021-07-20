package lime.features.module.impl.movement.flights.impl;

import lime.features.module.impl.movement.flights.FlightValue;

public class FuncraftGamer extends FlightValue {
    public FuncraftGamer()
    {
        super("Funcraft_Gamer");
    }

    @Override
    public void onUpdate() {
        mc.timer.timerSpeed = 1.5f;
        mc.thePlayer.setSprinting(false);
        mc.thePlayer.onGround = true;
        mc.thePlayer.motionY *= 0.3;
    }
}
