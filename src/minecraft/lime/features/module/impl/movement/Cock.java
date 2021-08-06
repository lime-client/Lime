package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.combat.KillAura;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleData(name = "Cock", category = Category.MOVEMENT)
public class Cock extends Module {

    private double moveSpeed, longJumpMoveSpeed, lastBoostedDistance, lastDist;
    private boolean back;
    private boolean prevOnGround;
    private int stage;

    @Override
    public void onEnable() {
        moveSpeed = lastBoostedDistance = 0;
        longJumpMoveSpeed = 0;
        stage = 0;
        back = false;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(stage == 0 && mc.thePlayer.isMoving()) {
            if(mc.thePlayer.onGround) {
                e.setY(mc.thePlayer.motionY = 0.4);
                lastBoostedDistance = moveSpeed;
                prevOnGround = true;
                back = false;
                longJumpMoveSpeed += 0.35;
                MovementUtils.hClip(-0.07);
                return;
            } else {
                if(prevOnGround) {
                    moveSpeed = lastBoostedDistance + 0.56;
                    prevOnGround = false;
                } else {
                    moveSpeed = lastDist * (0.985);
                }
            }

            double max = 0.6;
            MovementUtils.setSpeed(e, back ? -moveSpeed : moveSpeed);
            back = !back;

            if(longJumpMoveSpeed > 0.5) {
                stage = 1;
            }
        } else if(stage == 1) {
            //if(mc.thePlayer.onGround) {
            //    e.setY(mc.thePlayer.motionY = 0.42);
            //    MovementUtils.hClip(-0.07);
                stage = 2;
                prevOnGround = true;
            //}
        } else if(stage == 2) {
            if(prevOnGround) {
                MovementUtils.setSpeed(longJumpMoveSpeed);
                prevOnGround = true;
            } else {
                //MovementUtils.setSpeed(e, longJumpMoveSpeed -= longJumpMoveSpeed / 80);
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            System.out.println(longJumpMoveSpeed);
        }
    }
}
