package lime.module.impl.movement.FlightMode.impl;

import lime.events.impl.EventUpdate;
import lime.module.impl.movement.FlightMode.Flight;
import lime.utils.movement.MovementUtil;

public class VanillaCreative extends Flight {
    public VanillaCreative(String name){
        super(name);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.capabilities.isFlying = false;
        if(!mc.thePlayer.capabilities.isCreativeMode){
            mc.thePlayer.capabilities.allowFlying = false;
        } else {
            mc.thePlayer.capabilities.allowFlying = true;
        }
        super.onDisable();
    }

    @Override
    public void onUpdate(EventUpdate e) {
        mc.thePlayer.capabilities.isFlying = true;
        mc.thePlayer.capabilities.allowFlying = true;
        if(MovementUtil.isMoving() && !(getSettingByName("Flight").getValDouble() == 1 )){
            MovementUtil.setSpeed(getSettingByName("Flight").getValDouble());
        }
        super.onUpdate(e);
    }
}
