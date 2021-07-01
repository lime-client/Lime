package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;

@ModuleData(name = "Flight", category = Category.MOVEMENT)
public class Flight extends Module {

    private enum Mode {
        VANILLA, FUNCRAFT, VERUS, VERUS_FAST
    }

    //Settings
    private final EnumValue mode = new EnumValue("Mode", this, Mode.VANILLA);
    private final BoolValue bobbing = new BoolValue("Bobbing", this, true);

    private int ticks;
    private double moveSpeed;
    private boolean receivedVelocityPacket;

    @Override
    public void onEnable() {
        if(mode.is("funcraft")) {
            if(mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                moveSpeed = 1.875;
            } else
                moveSpeed = 0.25;
        }
        if(mode.is("verus_fast")) {
            for(int i = 0; i < 1; ++i) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY , mc.thePlayer.posZ, false));
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));

            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688697815, mc.thePlayer.posZ, false));

            /*for (int i = 0; i < 2; i++) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688697815, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.36502771690226155, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1647732818260721, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.08307781780646721, mc.thePlayer.posZ, false));
            }*/


            moveSpeed = 4;
        }

        ticks = 0;
        receivedVelocityPacket = false;
    }

    @Override
    public void onDisable() {
        MovementUtils.setSpeed(0);
        mc.timer.timerSpeed = 1;
    }

    @EventTarget
    public void on2D(Event2D e) {
        mc.fontRendererObj.drawStringWithShadow("BP/S: " + MovementUtils.getBPS(), 3, 3, -1);
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(bobbing.isEnabled() && mc.thePlayer.isMoving()) {
            mc.thePlayer.cameraYaw = 0.116f;
        }
        if(mode.is("vanilla")) {
            mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.80 : mc.gameSettings.keyBindSneak.isKeyDown() ? -0.80 : 0;
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(0.8);
            } else {
                MovementUtils.setSpeed(0);
            }
        }
        if(mode.is("verus") && mc.gameSettings.keyBindJump.isKeyDown() && e.isPre()) {
            mc.thePlayer.motionY = -0.0784000015258789;
        }

        if(mode.is("verus_fast")) {
            if(e.isPre())
            if(receivedVelocityPacket) {
                if(ticks <= 20) {
                    //mc.thePlayer.motionY = 0;

                    MovementUtils.setSpeed(moveSpeed);

                    if(moveSpeed > 0.25) {
                        moveSpeed -= 0.21;
                    }
                    ticks++;
                }
            }
        }

        if(mode.is("funcraft")) {
            e.setGround(true);
            mc.thePlayer.jumpMovementFactor = 0;
            mc.thePlayer.motionY = 0;
            if(mc.thePlayer.isCollidedHorizontally)
                moveSpeed = 0.25;
            if(mc.thePlayer.isMoving()) {
                mc.timer.timerSpeed = 1.0866f;
                if(mc.thePlayer.ticksExisted % 5 == 0) {
                    mc.timer.timerSpeed = 1.4f;
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
            if(e.isPre()) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3.33315597345063e-11, mc.thePlayer.posZ);
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                receivedVelocityPacket = true;
                if(mode.is("verus_fast")) {
                    MovementUtils.vClip(1);
                }
            }
        }
    }

    @EventTarget
    public void onBoundingBox(EventBoundingBox e) {
        if(mode.is("verus") || (mode.is("verus_fast") && receivedVelocityPacket)) {
            if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox()))
                e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
        }
    }
}
