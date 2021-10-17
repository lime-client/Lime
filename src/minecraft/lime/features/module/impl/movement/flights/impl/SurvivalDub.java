package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class SurvivalDub extends FlightValue {
    public SurvivalDub()
    {
        super("Survival_Dub");
    }

    private double moveSpeed;
    private double lastDist;
    private int ticks;
    private int stage;
    private boolean receivedS12;

    @Override
    public void onEnable() {
        stage = 0;
        moveSpeed = 0;
        lastDist = 0;
        receivedS12 = false;
        ticks = 0;
        mc.timer.timerSpeed = 3f;
    }

    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            moveSpeed = 0.25;
        }
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                receivedS12 = true;
            }
        }
    }

    @Override
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            ticks++;
            if(ticks > 10) {
                mc.timer.timerSpeed = 1;
            }
        }
        if(!receivedS12 || ticks < 5) return;
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
            if(e.isPre())
            {
                MovementUtils.vClip(-(8.0E-6));
            }
        }
    }

    @Override
    public void onMove(EventMove e) {
        if(ticks == 6) {
            for (int i = 0; i < 8; i++) {
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.4, mc.thePlayer.posZ, false));
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            }

            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            e.setCanceled(true);
            return;
        } else if(!receivedS12 || ticks <= 5) {
            e.setX(0);
            e.setZ(0);
            return;
        }
        if((stage == 0 && !mc.thePlayer.onGround) || mc.thePlayer.isCollidedHorizontally) stage = -1;
        if(mc.thePlayer.isMoving())
        {
            if(stage != -1) {
                switch(stage)
                {
                    case 0:
                        if(mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically)
                            this.moveSpeed = 0.3;
                        break;
                    case 1:
                        if(mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically)
                            e.setY(mc.thePlayer.motionY = 0.42);
                        this.moveSpeed *= 2.149;
                        break;
                    case 2:
                        this.moveSpeed = 1;
                        break;
                    default:
                        this.moveSpeed = this.lastDist - this.lastDist / 159;
                        break;
                }


                MovementUtils.setSpeed(e, Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed()));
                ++stage;
            }
        }
    }
}
