package lime.module.impl.movement.FlightMode.impl;

import lime.events.impl.EventUpdate;
import lime.module.impl.movement.FlightMode.Flight;
import lime.utils.movement.MovementUtil;

public class Vanilla extends Flight {
    public Vanilla(String name){
        super(name);
    }

    @Override
    public void onUpdate(EventUpdate e) {
        mc.thePlayer.motionY = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.thePlayer.motionY = 0.1;
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.thePlayer.motionY = -0.1;
        if(MovementUtil.isMoving() && !(getSettingByName("Flight").getValDouble() == 1 )){
            MovementUtil.setSpeed(getSettingByName("Flight").getValDouble());
        }
        super.onUpdate(e);
    }
}
