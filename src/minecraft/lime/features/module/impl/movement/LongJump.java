package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleData(name = "Long Jump", category = Category.MOVEMENT)
public class LongJump extends Module {

    private enum Mode {
        VANILLA, TAKA, NCP_BOW, VERUS_BOW
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.VANILLA);
    private final SlideValue speed = new SlideValue("Speed", this, 1, 9, 5, 0.5);

    private double moveSpeed = 0;
    private boolean receivedS12 = false;

    @Override
    public void onEnable() {
        receivedS12 = false;
        if(mode.is("taka")) mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 4, mc.thePlayer.posX, true));
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(mode.is("ncp_bow") && !receivedS12) {
            MovementUtils.setSpeed(0);
            e.setPitch(-90);
        }
        if(mode.is("verus_bow")) {
            if(!receivedS12) {
                MovementUtils.setSpeed(0);
                mc.thePlayer.motionY = -0.0784000015258789;
                e.setPitch(-90);
            } else {
                if(e.isPre()) {
                    if(MovementUtils.isOnGround(3) && mc.thePlayer.motionY < 0) {
                        moveSpeed = 0.25;
                        mc.thePlayer.motionY = -0.0784000015258789;
                    }
                    moveSpeed -= 0.0001;
                    MovementUtils.setSpeed(moveSpeed);
                    if(MovementUtils.isOnGround(1)) this.toggle();
                }
            }
        }
    }

    @EventTarget
    public void onBoundingBox(EventBoundingBox e) {

    }

    @EventTarget
    public void onMove(EventMove e) {
        if(!receivedS12) {
            if(mode.is("taka")) {
                e.setX(e.getX() * 0.05);
                e.setZ(e.getZ() * 0.05);
                e.setY(0);
            } else if(mode.is("verus_bow")) {
                e.setX(0);
                e.setZ(0);
            } else {
                e.setCanceled(true);
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if(packet.getEntityID() != mc.thePlayer.getEntityId()) return;
            receivedS12 = true;
            if(mode.is("taka")) {
                MovementUtils.setSpeed(7);
                this.toggle();
            }
            if(mode.is("ncp_bow")) {
                mc.thePlayer.motionY = 0.4;
                if(packet.getMotionY() == 3686) {
                    MovementUtils.setSpeed(1.4);
                } else {
                    MovementUtils.setSpeed(0.8);
                }
                this.toggle();
            }

            if(mode.is("verus_bow")) {
                //MovementUtils.vClip(4);
                moveSpeed = speed.getCurrent();
                MovementUtils.setSpeed(moveSpeed);
                mc.thePlayer.motionY = 1.1;
            }
        }
    }
}
