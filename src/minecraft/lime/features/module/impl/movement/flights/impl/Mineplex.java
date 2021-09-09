package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.exploit.Disabler;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.ui.notifications.Notification;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Mineplex extends FlightValue {
    public Mineplex() {
        super("Mineplex");
    }

    private double moveSpeed;
    private double fakeY, y;

    @Override
    public void onEnable() {
        if(!Lime.getInstance().getModuleManager().getModuleC(Disabler.class).isToggled()) {
            Lime.getInstance().getNotificationManager().addNotification(new Notification("Flight", "This flight need Mineplex Disabler!", Notification.Type.ERROR));
            //this.getFlight().toggle();
            return;
        }
        moveSpeed = 0.2873;
        fakeY = mc.thePlayer.posY;
        y = mc.thePlayer.posY;
    }

    @Override
    public void onUpdate() {
        mc.thePlayer.posY = y;
    }

    @Override
    public void onMotion(EventMotion e) {
        if(!e.isPre()) {
            y = mc.thePlayer.posY;
            if(!mc.thePlayer.isMoving() || mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
                fakeY = mc.thePlayer.posY;
            }
            mc.thePlayer.posY = fakeY;
        }
    }

    @Override
    public void onMove(EventMove e) {
        if(!mc.thePlayer.isMoving()) {
            moveSpeed = 0.3;
        }

        if(mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
            e.setY(mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.4 : -0.4);
            moveSpeed = 0.3;
            return;
        }

        if(mc.thePlayer.ticksExisted % 6 == 0) {
            e.setY(mc.thePlayer.motionY = 0.2);
            MovementUtils.setSpeed(e, 0);
            moveSpeed += 0.315;
            fakeY = mc.thePlayer.posY;
        } else {
            MovementUtils.setSpeed(e, Math.min(moveSpeed -= moveSpeed / 31, 2.5));
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            moveSpeed = 0.3;
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            y = packet.getY();
        }
    }
}
