package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.Flight;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.ui.notifications.Notification;
import lime.utils.movement.MovementUtils;
import lime.utils.other.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;

public class VerusFast extends FlightValue {
    public VerusFast() {
        super("Verus_Fast");
    }

    private boolean receivedVelocityPacket;

    @Override
    public void onEnable() {
        if(!mc.thePlayer.onGround)
        {
            Lime.getInstance().getNotificationManager().addNotification(new Notification("Error", "Can damage only on the ground!", Notification.Type.ERROR));
            getFlight().toggle();
            return;
        }
        PlayerUtils.verusDamage();
        mc.thePlayer.jump();
    }

    @Override
    public void onDisable() {
        receivedVelocityPacket = false;
    }

    @Override
    public void onMotion(EventMotion e) {
        if(getFlight().verusGlide.isEnabled() && receivedVelocityPacket && mc.thePlayer.motionY < 0) {
            mc.thePlayer.motionY = -0.0784000015258789;
        }

        if(mc.thePlayer.isCollidedHorizontally && getFlight().getTicks() < 25)
        {
            Lime.getInstance().getNotificationManager().addNotification(new Notification("Flight", "Disabled boost for safety", Notification.Type.WARNING));
            getFlight().setTicks(25);
        }

        if(getFlight().getTicks() <= 24 && receivedVelocityPacket && e.isPre()) {
            MovementUtils.setSpeed(getFlight().speed.getCurrent());
        } else if(getFlight().getTicks() == 25) {
            MovementUtils.setSpeed(0);
        }
    }

    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity)
        {
            if(((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId())
                receivedVelocityPacket = true;
        }
    }

    @Override
    public void onMove(EventMove e) {
        if(getFlight().mode.is("verus_fast") && !receivedVelocityPacket) {
            mc.timer.timerSpeed = 0.6f;
            e.setX(0);
            e.setZ(0);
        } else
            mc.timer.timerSpeed = 1;
    }

    @Override
    public void onBoundingBox(EventBoundingBox e) {
        if(receivedVelocityPacket && !getFlight().verusGlide.isEnabled()) {
            if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox())) {
                e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
            }
        }
    }
}
