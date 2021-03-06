package lime.module.impl.movement.SpeedMode.impl;

import lime.events.impl.EventUpdate;
import lime.module.impl.movement.SpeedMode.Speed;
import lime.utils.movement.MovementUtil;

public class BRWServ extends Speed {
    public BRWServ(String name){
        super(name);
    }

    @Override
    public void onUpdate(EventUpdate e) {
        if(MovementUtil.isMoving() && mc.thePlayer.onGround) mc.thePlayer.motionY = 0.42;
        if(MovementUtil.isMoving()){
            MovementUtil.setSpeed(0.6);
        }
        if(mc.thePlayer.ticksExisted % 3 == 0) mc.thePlayer.motionY -= 0.04;
        super.onUpdate(e);
    }
}
