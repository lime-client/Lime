package lime.features.module.impl.movement.flights.impl;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.ChatUtils;
import lime.utils.other.MathUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Mineplex extends FlightValue {
    public Mineplex() {
        super("Mineplex");
    }

    private boolean back;
    private int stage, ticks;
    private double moveSpeed, y;

    @Override
    public void onEnable() {
        back = false;
        stage = 0;
        moveSpeed = 0.25;
        y = 0;
    }

    @Override
    public void onMove(EventMove e) {
        if(stage == 0 && mc.thePlayer.isMoving()) {
            if(mc.thePlayer.onGround) {
                e.setY(mc.thePlayer.motionY = 0.42 + y);
                MovementUtils.setSpeed(e, -0.07);
                moveSpeed += 0.45;
                //mc.timer.timerSpeed = 1.5f;
                ticks = 0;
                y += 0.04;
                return;
            }

            moveSpeed -= moveSpeed / 80;
            MovementUtils.setSpeed(e, back ? -moveSpeed : moveSpeed);
            back = !back;

            if(moveSpeed > 1.25) {
                stage = 1;
                moveSpeed = 1.45;
                back = false;
            }
            ticks++;
        }
        if(stage == 1) {
            mc.timer.timerSpeed = 1;
            if(mc.thePlayer.onGround) {
                MovementUtils.setSpeed(e, -0.07);
                e.setY(mc.thePlayer.motionY = 0.42 + y);
                back = true;
            } else {
                if(moveSpeed > 0.8) {
                    e.setY(mc.thePlayer.motionY += 0.028888888888888 + MathUtils.random(0.00005, 0.00105));
                }
                MovementUtils.setSpeed(e, moveSpeed -= moveSpeed / 49);
                if(back && mc.thePlayer.onGround)
                    getFlight().toggle();
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            ChatUtils.sendMessage(moveSpeed+"");
        }
    }
}
