package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleData(name = "Custom Flight", category = Category.MOVEMENT)
public class CustomFlight extends Module {
    private enum Damage { NONE, BASIC }

    private final EnumValue damage = new EnumValue("Damage", this, Damage.NONE);
    private final SlideValue timer = new SlideValue("Timer", this, 0.1, 10, 1, 0.5);
    private final SlideValue speed = new SlideValue("Speed", this, 0.25, 10, 3, 0.25);
    private final SlideValue decreaseSpeed = new SlideValue("Decrease Speed", this, 0, 3, 0.2, 0.05);
    private final SlideValue motion = new SlideValue("Motion", this, -5, 10, 0, 0.5);
    private final SlideValue stopSpeedAfter = new SlideValue("Stop Speed After", this, 0, 30000, 0, 1000);
    private final BoolValue motionAfterDamage = new BoolValue("Motion After Damage", this, false);
    private final BoolValue speedAfterDamage = new BoolValue("Speed After Damage", this, false);

    private boolean receivedVelocityPacket;

    private final Timer speedDelay = new Timer();
    private double moveSpeed;

    @Override
    public void onEnable() {
        if(damage.is("basic")) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.5, mc.thePlayer.posZ, false));
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));

            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688697815, mc.thePlayer.posZ, false));
            //mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
        }
        if(!speedAfterDamage.isEnabled()) {
            speedDelay.reset();
            moveSpeed = speed.getCurrent();
        } else {
            moveSpeed = 0;
        }
    }

    @Override
    public void onDisable() {
        receivedVelocityPacket = false;
        mc.timer.timerSpeed = 1;
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(motionAfterDamage.isEnabled() && receivedVelocityPacket) {
            mc.thePlayer.motionY = motion.getCurrent();
        }
        if(speedAfterDamage.isEnabled() && receivedVelocityPacket && mc.thePlayer.isMoving() && (!speedDelay.hasReached((long) stopSpeedAfter.getCurrent()) || stopSpeedAfter.getCurrent() != 0)) {
            MovementUtils.setSpeed(moveSpeed);
        }

        if(!speedAfterDamage.isEnabled() && mc.thePlayer.isMoving() && (!speedDelay.hasReached((long) stopSpeedAfter.getCurrent()) || stopSpeedAfter.getCurrent() == 0)) {
            MovementUtils.setSpeed(moveSpeed);
        }
        if(!motionAfterDamage.isEnabled()) {
            mc.thePlayer.motionY = motion.getCurrent();
        }

        mc.timer.timerSpeed = (float) timer.getCurrent();

        if(moveSpeed >= 0.25 && moveSpeed - decreaseSpeed.getCurrent() >= 0.25) {
            moveSpeed -= decreaseSpeed.getCurrent();
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                receivedVelocityPacket = true;
                if(speedAfterDamage.isEnabled()) {
                    speedDelay.reset();
                    moveSpeed = speed.getCurrent();
                }
            }
        }
        if(e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) e.getPacket();
            c03PacketPlayer.setOnGround(false);
            e.setPacket(c03PacketPlayer);
        }
    }
}
