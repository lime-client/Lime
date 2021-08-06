package lime.utils.movement;

import lime.core.Lime;
import lime.core.events.impl.EventMove;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.movement.TargetStrafe;
import lime.utils.IUtil;

public class HopFriction implements IUtil {
    private double speed;
    private boolean prevOnGround;

    public void updateFriction(EventMove e, boolean setMotionY, double motionY, double multiplication, double difference, double friction, double lastDist) {
        if(mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
            if(setMotionY)
                e.setY(mc.thePlayer.motionY = motionY);
            else
                e.setY(motionY);
            speed = Math.max(MovementUtils.getBaseMoveSpeed() * multiplication, speed * multiplication);
            prevOnGround = true;
        }
        else {
            if (prevOnGround) {
                speed = lastDist - (difference * (lastDist - MovementUtils.getBaseMoveSpeed()));
                prevOnGround = false;
            } else {
                speed = lastDist - lastDist / friction;
            }
        }
        if (KillAura.getEntity() != null) {
            TargetStrafe targetStrafe2 = (TargetStrafe) Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
            targetStrafe2.setMoveSpeed(e, speed);
        } else {
            MovementUtils.setSpeed(e, speed);
        }
    }

    public void reset() {
        speed = 0;
        prevOnGround = false;
    }
}