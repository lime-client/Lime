package lime.module.impl.movement.FlightMode.impl;

import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventUpdate;
import lime.module.impl.movement.FlightMode.Flight;
import lime.utils.movement.MovementUtil;

public class Funcraft extends Flight {
    private double lastDist; int stage;
    private int state = 0;
    private double oof;
    public Funcraft(String name){
        super(name);
    }

    @Override
    public void onEnable() {
        stage = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onUpdate(EventUpdate e) {
        if (mc.thePlayer.onGround){stage=1;} else {
            mc.thePlayer.motionY = 0;
            if(mc.thePlayer.ticksExisted % 3 == 0){
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12, mc.thePlayer.posZ);
            } else {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-12, mc.thePlayer.posZ);
            }
            mc.timer.timerSpeed = 1.65f;
        }
        if (stage == 1) {
            oof = lastDist / 159 + 1.5;
            mc.timer.timerSpeed = 1.0F;
            stage = 2;
        }
        if (stage == 2) {
            if (this.mc.thePlayer.onGround) {
                mc.thePlayer.jump();
            } else {
                mc.thePlayer.jumpMovementFactor = 0f;

                if (MovementUtil.isMoving()) {
                    mc.timer.timerSpeed = (float) 1.65F;

                    /*mc.gameSettings.keyBindRight.pressed = true;
                    mc.gameSettings.keyBindLeft.pressed = true;
                    mc.gameSettings.keyBindBack.pressed = true;

                     */
                }

                if (oof > 0.25) {
                    oof -= 0.01;
                }

                if(mc.thePlayer.ticksExisted % 3 == 0){
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12, mc.thePlayer.posZ);
                } else {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-12, mc.thePlayer.posZ);
                }
            }
            if (!this.mc.thePlayer.onGround)
                if(MovementUtil.isMoving())
                    MovementUtil.setSpeed(oof);
        }
        super.onUpdate(e);
    }
    @Override
    public void onMotion(EventMotion e){
        switch(e.getState()) {
            case PRE:
                double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                break;
            default: break;
        }
    }
}
