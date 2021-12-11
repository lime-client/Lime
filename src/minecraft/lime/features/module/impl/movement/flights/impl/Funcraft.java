package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.movement.TargetStrafe;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.features.module.impl.world.Timer;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Funcraft extends FlightValue {
    public Funcraft()
    {
        super("Funcraft");
    }

    private double moveSpeed, timerSpeed;
    private double lastDist;
    private int stage;

    @Override
    public void onEnable() {
        stage = 0;
        moveSpeed = 0;
        lastDist = 0;
        timerSpeed = getFlight().funcraftTimerSpeed.getCurrent();
    }

    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            moveSpeed = 0.25;
            timerSpeed = 1.75;
        }
    }

    @Override
    public void onMotion(EventMotion e) {
        if(!Lime.getInstance().getModuleManager().getModuleC(Timer.class).isToggled())
        {
            mc.timer.timerSpeed = mc.thePlayer.isMoving() ? (float) timerSpeed : 1;
            timerSpeed -= timerSpeed / 360;
            timerSpeed = Math.max(timerSpeed, 1.6);
        }

        if(!mc.thePlayer.isMoving()) {
            MovementUtils.setSpeed(0);
        }

        e.setGround(true);

        if(e.isPre())
        {
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        }

        if((stage > 2 || stage == -1) && !MovementUtils.isOnGround(3.33315597345063e-11))
        {
            mc.thePlayer.motionY = 0;
            if(stage == -1 && mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(.25);
            }
            if(e.isPre())
            {
                MovementUtils.vClip(-(3.33315597345063e-11));
            }
        }
    }

    @Override
    public void onMove(EventMove e) {
        mc.thePlayer.jumpMovementFactor = 0;
        if((stage == 0 && !mc.thePlayer.onGround) || mc.thePlayer.isCollidedHorizontally) stage = -1;
        if(mc.thePlayer.isMoving())
        {
            if(stage != -1) {
                switch(stage)
                {
                    case 0:
                        if(mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically)
                            this.moveSpeed = 0.5;
                        break;
                    case 1:
                        if(mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically)
                            e.setY(mc.thePlayer.motionY = 0.3999);
                        this.moveSpeed *= 2.149;
                        break;
                    case 2:
                        this.moveSpeed = getFlight().funcraftSpeed.getCurrent();
                        break;
                    default:
                        this.moveSpeed = this.lastDist - this.lastDist / 159;
                        break;
                }

                moveSpeed = Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed());

                MovementUtils.setSpeed(e, moveSpeed);
                ++stage;
            }
        }
    }
}
