package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.features.module.impl.exploit.Disabler;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.ui.notifications.Notification;
import lime.utils.movement.MovementUtils;

public class MineplexFast extends FlightValue {
    public MineplexFast() {
        super("Mineplex Fast");
    }

    private double moveSpeed;

    @Override
    public void onEnable() {
        if(!Lime.getInstance().getModuleManager().getModuleC(Disabler.class).isToggled()) {
            Lime.getInstance().getNotificationManager().addNotification(new Notification("Flight", "This flight need Mineplex Disabler!", Notification.Type.ERROR));
            this.getFlight().toggle();
            return;
        }
        moveSpeed = 0.3;
    }

    @Override
    public void onMotion(EventMotion e) {
    }

    @Override
    public void onMove(EventMove e) {
        if(!mc.thePlayer.isMoving() || mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
            moveSpeed = 0.3;
        }

        if(mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
            e.setY(mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.4 : -0.4);
            return;
        }

        if(mc.thePlayer.ticksExisted % 6 == 0) {
            e.setY(mc.thePlayer.motionY = 0.2);
            MovementUtils.setSpeed(e, 0);
            moveSpeed += 0.315;
        } else {
            MovementUtils.setSpeed(e, Math.min(moveSpeed -= moveSpeed / 29.25, 2.5));
        }
    }
}
