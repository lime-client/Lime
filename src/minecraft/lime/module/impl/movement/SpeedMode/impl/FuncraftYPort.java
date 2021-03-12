package lime.module.impl.movement.SpeedMode.impl;

import lime.events.impl.EventMotion;
import lime.module.impl.movement.SpeedMode.Speed;
import lime.utils.movement.MovementUtil;

public class FuncraftYPort extends Speed {
    public FuncraftYPort(String name){
        super(name);
    }

    @Override
    public void onMotion(EventMotion e) {
        if (MovementUtil.isMoving() && !(mc.thePlayer.fallDistance > 2.5)) {
            mc.thePlayer.cameraYaw = 0f;
            mc.thePlayer.setSprinting(true);
            mc.thePlayer.motionY = -0.41;
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.thePlayer.motionY = 0.4;
                mc.thePlayer.motionX *= 1.081;
                mc.thePlayer.motionZ *= 1.081;
            }
            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed());
            mc.timer.timerSpeed = 1.75f;
        }
        super.onMotion(e);
    }
}
