package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventMotion;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;

public class Funcraft extends FlightValue {
    public Funcraft() {
        super("Funcraft");
    }

    private double moveSpeed;

    @Override
    public void onEnable() {
        if(mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            moveSpeed = 1.7;
        } else
            moveSpeed = 0.25;
    }

    @Override
    public void onMotion(EventMotion e) {
        e.setGround(true);
        mc.thePlayer.jumpMovementFactor = 0;
        if(e.isPre()) {
            mc.thePlayer.motionY = 0;
            if(getFlight().getTicks() > 175 && moveSpeed < 0.26) {
                getFlight().setTicks(0);
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.03, mc.thePlayer.posZ);
            }
        }

        if(mc.thePlayer.isCollidedHorizontally)
            moveSpeed = 0.25;
        if(mc.thePlayer.isMoving()) {
            mc.timer.timerSpeed = 1.0866f;
            if(mc.thePlayer.ticksExisted % 5 == 0) {
                mc.timer.timerSpeed = 1.75f;
            }
            MovementUtils.setSpeed(moveSpeed);
            if(moveSpeed > 0.25)
                moveSpeed -= moveSpeed / 159;
            else
                moveSpeed = 0.25;
        } else {
            MovementUtils.setSpeed(0);
            moveSpeed = 0.25;
        }
        if(e.isPre() && !MovementUtils.isOnGround(0.1)) {
            // 3.33315597345063e-11
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0000000000333315597345063, mc.thePlayer.posZ);
        }
    }
}
