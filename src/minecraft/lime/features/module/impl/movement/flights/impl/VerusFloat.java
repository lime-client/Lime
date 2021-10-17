package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.ui.notifications.Notification;
import lime.utils.movement.MovementUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.AxisAlignedBB;

public class VerusFloat extends FlightValue {
    public VerusFloat() {
        super("Verus Float");
    }

    private double moveSpeed, y;
    private int stage;
    private boolean firstHop, spoofGround;

    @Override
    public void onEnable() {
        if(!mc.thePlayer.onGround) {
            Lime.getInstance().getNotificationManager().addNotification("Flight", "You need to be on ground at the start of flight.", Notification.Type.FAIL);
            this.getFlight().toggle();
            return;
        }
        moveSpeed = 0;
        stage = 0;
        y = mc.thePlayer.posY;
        firstHop = true;
        spoofGround = false;
    }

    @Override
    public void onMotion(EventMotion e) {
        mc.getNetHandler().sendPacketNoEvent(new C0CPacketInput());
        e.setGround(mc.thePlayer.onGround || spoofGround);
    }

    @Override
    public void onMove(EventMove e) {
        if(firstHop) {
            mc.getNetHandler().sendPacketNoEvent(new C0CPacketInput());
            if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                e.setY(mc.thePlayer.motionY = 0.41999998688697815);
                spoofGround = true;
                stage = 0;
            } else if (this.stage <= 6) {
                e.setY(mc.thePlayer.motionY = 0);
                ++stage;
            } else {
                spoofGround = false;
                firstHop = false;
            }

            mc.thePlayer.motionY = e.getY();
        } else {
            mc.getNetHandler().sendPacketNoEvent(new C0CPacketInput());
            if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                moveSpeed = 0.5;
                e.setY(mc.thePlayer.motionY = 0.41999998688697815);
                spoofGround = true;
                stage = 0;
            } else if (this.stage <= (getFlight().verusHeavy.isEnabled() ? 1 : 7)) {
                this.moveSpeed += 0.12;
                e.setY(mc.thePlayer.motionY = 0);
                ++stage;
            } else {
                moveSpeed = 0.24;
                spoofGround = false;
            }

            MovementUtils.setSpeed(e, moveSpeed - 1.0E-4);
        }
    }

    @Override
    public void onBoundingBox(EventBoundingBox e) {
        if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < y && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox())) {
            e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, y, e.getBlockPos().getZ() + 1));
        }
    }
}
