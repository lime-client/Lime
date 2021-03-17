package lime.module.impl.movement;

import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventMove;
import lime.module.Module;
import lime.utils.movement.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;

public class LongJump extends Module {
    public LongJump(){
        super("LongJump", 0, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        this.lastDif = 0.0D;
        this.moveSpeed = 0.0D;
        this.stage = 0;
        this.groundTicks = 1;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private double lastDif;
    private double moveSpeed;
    private int stage;
    private int groundTicks;


    @EventTarget
    public void onMove(EventMove event){
        if (this.toggled) {
            EntityPlayerSP player = mc.thePlayer;
            boolean watchdog = true;
            if (MovementUtil.isMoving()) {
                switch(this.stage) {
                    case 0:
                    case 1:
                        this.moveSpeed = 0.0D;
                        break;
                    case 2:
                        if (player.onGround && player.isCollidedVertically) {
                            event.y = player.motionY = MovementUtil.getJumpBoostModifier(0.3999999463558197D);
                            this.moveSpeed = MovementUtil.getBaseMoveSpeed() * 2.0D;
                        }
                        break;
                    case 3:
                        this.moveSpeed = MovementUtil.getBaseMoveSpeed() * 2.1489999294281006D;
                        break;
                    case 4:
                        if (watchdog) {
                            this.moveSpeed *= 1.600000023841858D;
                        }
                        break;
                    default:
                        if (player.motionY < 0.0D) {
                            player.motionY *= 0.6D;
                        }

                        this.moveSpeed = this.lastDif - this.lastDif / 159.0D;
                }

                this.moveSpeed = Math.max(this.moveSpeed, MovementUtil.getBaseMoveSpeed());
                ++this.stage;
            }

            setSpeed(event, this.moveSpeed);
        }
    }



    @EventTarget
    public void onMotion(EventMotion event){
        EntityPlayerSP player = mc.thePlayer;
        if (event.isPre()) {
            if (player.onGround && player.isCollidedVertically) {
                event.setY(event.getY() + 7.435E-4D);
            }

            double xDif = player.posX - player.prevPosX;
            double zDif = player.posZ - player.prevPosZ;
            this.lastDif = Math.sqrt(xDif * xDif + zDif * zDif);
            if (MovementUtil.isMoving() && player.onGround && player.isCollidedVertically && this.stage > 2) {
                ++this.groundTicks;
            }

            if (this.groundTicks > 1) {
                this.toggle();
            }
        }
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
