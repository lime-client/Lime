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

public class Funcraft2 extends FlightValue {
    public Funcraft2()
    {
        super("Funcraft2");
    }

    private double moveSpeed;
    private double lastDist;
    private int stage;

    @Override
    public void onEnable() {
        stage = 0;
    }

    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            moveSpeed = 0.25;
        }
    }

    @Override
    public void onMotion(EventMotion e) {

        if(!Lime.getInstance().getModuleManager().getModuleC(Timer.class).isToggled())
        {
            mc.timer.timerSpeed = 1.0866f;
            if(mc.thePlayer.ticksExisted % 3 == 0)
            {
                mc.timer.timerSpeed = 1.75f;
            }
        }

        e.setGround(true);

        if(moveSpeed < MovementUtils.getBaseMoveSpeed())
            moveSpeed = MovementUtils.getBaseMoveSpeed();

        if(e.isPre())
        {
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        }

        if((stage > 2 || stage == -1) && !MovementUtils.isOnGround(3.33315597345063e-11))
        {
            mc.thePlayer.motionY = 0;
            if(e.isPre())
            {
                MovementUtils.vClip(-(3.33315597345063e-11));
            }
        }
    }

    @Override
    public void onMove(EventMove e) {
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
                            e.setY(mc.thePlayer.motionY = 0.42);
                        this.moveSpeed *= 2.149;
                        break;
                    case 2:
                        this.moveSpeed = 1.6;
                        break;
                    default:
                        this.moveSpeed = this.lastDist - this.lastDist / 159;
                        break;
                }
                if (KillAura.getEntity() != null) {
                    TargetStrafe targetStrafe2 = (TargetStrafe) Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
                    targetStrafe2.setMoveSpeed(e, moveSpeed);
                } else {
                    MovementUtils.setSpeed(e, moveSpeed);
                }
                ++stage;
            }
        }
    }
}
