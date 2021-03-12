package lime.module.impl.movement.FlightMode.impl;

import lime.Lime;
import lime.events.impl.EventMotion;
import lime.events.impl.EventMove;
import lime.module.impl.movement.FlightMode.Flight;
import lime.utils.movement.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

public class Funcraft2 extends Flight {
    public Funcraft2(String name){
        super(name);
    }
    int stage, ticks;
    private double lastDist;
    private double moveSpeed, y;

    @Override
    public void onEnable() {
        this.y = 0.0D;
        this.lastDist = 0.0D;
        this.moveSpeed = 0.0D;
        this.stage = 0;
        ticks = 0;

        super.onEnable();
    }

    @Override
    public void onMove(EventMove event) {
        EntityPlayerSP player = mc.thePlayer;
        double offset;
        if (MovementUtil.isMoving()) {
            switch(this.stage) {
                case 0:
                    if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                        this.moveSpeed = 0.5D * 1;
                    }
                    break;
                case 1:
                    if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                        event.y = player.motionY = MovementUtil.getJumpBoostModifier(0.39999994D);
                    }

                    this.moveSpeed *= 2.149D;
                    break;
                case 2:
                    this.moveSpeed = 1.3D * 1;
                    break;
                default:
                    this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
            }

            setSpeed(event, Math.max(this.moveSpeed, MovementUtil.getBaseMoveSpeed()));
            ++this.stage;
        }
        super.onMove(event);
    }


    @Override
    public void onMotion(EventMotion e) {
        mc.thePlayer.jumpMovementFactor = 0f;
        EntityPlayerSP player = mc.thePlayer;

        mc.timer.timerSpeed = 1.65f;
        double offset;
        if (e.getState() == EventMotion.State.PRE) {
            if (this.stage > 2) {
                player.motionY = 0.0D;
            }


            if (this.stage > 2) {
                player.setPosition(player.posX, player.posY - 0.003D, player.posZ);
                ++this.ticks;
                offset = 3.25E-4D;
                switch(this.ticks) {
                    case 1:
                        this.y *= -0.949999988079071D;
                        break;
                    case 2:
                    case 3:
                    case 4:
                        this.y += 3.25E-4D;
                        break;
                    case 5:
                        this.y += 5.0E-4D;
                        this.ticks = 0;
                }

                e.setY(player.posY + this.y);
            }
        } else if (this.stage > 2) {
            if(mc.thePlayer.ticksExisted % 3 == 0){
                player.setPosition(player.posX, player.posY + 1.0E-12, player.posZ);
            } else {
                player.setPosition(player.posX, player.posY - 1.0E-12, player.posZ);
            }
        }
        if (e.getState() == EventMotion.State.PRE) {
            offset = player.posX - player.prevPosX;
            double zDif = player.posZ - player.prevPosZ;
            this.lastDist = Math.sqrt(offset * offset + zDif * zDif);
        }
        super.onMotion(e);
    }

    public void setSpeed(EventMove moveEvent, double moveSpeed) {
        setSpeed(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, (double)mc.thePlayer.movementInput.moveStrafe, (double)mc.thePlayer.movementInput.moveForward);
    }

    public void setSpeed(EventMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (pseudoForward != 0.0D) {
            if (pseudoStrafe > 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? -45 : 45);
            } else if (pseudoStrafe < 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;
            if (pseudoForward > 0.0D) {
                forward = 1.0D;
            } else if (pseudoForward < 0.0D) {
                forward = -1.0D;
            }
        }

        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }

        double mx = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
        moveEvent.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        moveEvent.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }

}
