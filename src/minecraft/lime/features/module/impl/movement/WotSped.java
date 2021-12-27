package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.movement.MovementUtils;

public class WotSped extends Module {
    public WotSped() {
        super("Wot Sped", Category.MOVE);
    }

    private double moveSpeed, lastDist;
    private int stage;

    @Override
    public void onEnable() {
        moveSpeed = lastDist = 0;
        stage = 0;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            mc.thePlayer.jumpMovementFactor = 0;
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(mc.thePlayer.isMoving()) {

            if(mc.thePlayer.onGround && stage > 10) {
                stage = 0;
            }

            switch(stage) {
                case 0:
                case 1:
                case 2:
                case 4:
                    e.setCanceled(true);
                    break;
                case 5:
                    e.setY(mc.thePlayer.motionY = 0.3999);
                    moveSpeed *= 2.149;
                    break;
                case 6:
                    moveSpeed = 1.2;
                    break;
                default:
                    moveSpeed = lastDist - lastDist / 72;
                    break;
            }

            moveSpeed = Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed());
            MovementUtils.setSpeed(e, moveSpeed);
            ++stage;
        }
    }
}
