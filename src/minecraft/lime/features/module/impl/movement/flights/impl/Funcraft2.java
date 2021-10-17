package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Funcraft2 extends FlightValue {
    public Funcraft2()
    {
        super("Funcraft2");
    }

    private double moveSpeed, lastDist;
    private int stage;
    private boolean damage, lostBoost;

    @Override
    public void onEnable() {
        moveSpeed = lastDist = 0;
        stage = 0;
        damage = false;
        lostBoost = false;
    }

    @Override
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            mc.thePlayer.jumpMovementFactor = 0;
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);

            if(!mc.thePlayer.isMoving() || mc.thePlayer.isCollidedHorizontally || (!mc.thePlayer.onGround && stage < 2)) {
                lostBoost = true;
            }

            if(stage > 0 || lostBoost) {
                e.setGround(true);
                mc.thePlayer.motionY = 0;
                if(!MovementUtils.isOnGround(3.33315597345063e-11)) {
                    MovementUtils.vClip(-(3.33315597345063e-11));
                }
            }
        }
    }

    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                damage = true;
            }
        }
    }

    @Override
    public void onMove(EventMove e) {
        if(mc.thePlayer.isMoving() && !lostBoost) {
            switch(stage) {
                case 0:
                    moveSpeed = 0;
                    break;
                case 1:
                    e.setY(mc.thePlayer.motionY = 0.3999);
                    moveSpeed *= 2.149;
                    break;
                case 2:
                    moveSpeed = 1.6;
                    break;
                default:
                    moveSpeed = lastDist - lastDist / 159;
                    break;
            }

            moveSpeed = Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed());
            MovementUtils.setSpeed(e, moveSpeed);
            ++stage;
        } else if(mc.thePlayer.isMoving() && lostBoost) {
            MovementUtils.setSpeed(e, MovementUtils.getBaseMoveSpeed());
        }
    }
}
