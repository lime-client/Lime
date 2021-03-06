package lime.module.impl.movement.SpeedMode.impl;

import lime.events.impl.EventUpdate;
import lime.module.impl.movement.SpeedMode.Speed;
import lime.utils.movement.MovementUtil;

public class Vanilla extends Speed {
    public Vanilla(String name){
        super(name);
    }

    @Override
    public void onUpdate(EventUpdate e) {
        if(MovementUtil.isMoving()) MovementUtil.setSpeed(getSettingByName("Speed Power").getValDouble());
        super.onUpdate(e);
    }

}
