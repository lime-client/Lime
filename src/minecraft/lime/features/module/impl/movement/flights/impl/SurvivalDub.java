package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.PlayerUtils;
import lime.utils.other.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;

import java.util.ArrayList;

public class SurvivalDub extends FlightValue {
    public SurvivalDub() {
        super("Survival_Dub");
    }

    private final ArrayList<Packet<?>> packets = new ArrayList<>();
    private final Timer timer = new Timer();
    private double lastDist, moveSpeed;
    private int stage;

    @Override
    public void onEnable() {
        stage = 0;
        for (int i = 0; i < 8; i++) {
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, false));
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }

        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        moveSpeed = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        for (Packet<?> packet : packets) {
            mc.getNetHandler().addToSendQueue(packet);
        }
        packets.clear();
        super.onDisable();
    }

    @Override
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            if(stage == 1) {
                e.setCanceled(true);
                packets.add(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                if(timer.hasReached(150)) {
                    for (Packet<?> packet : packets) {
                        mc.getNetHandler().sendPacketNoEvent(packet);
                    }
                    packets.clear();
                    timer.reset();
                }
            }
        }
    }

    @Override
    public void onMove(EventMove e) {
        mc.getNetHandler().sendPacketNoEvent(new C0CPacketInput((float) moveSpeed, (float) moveSpeed, false, false));
        if(stage == 0) {
            moveSpeed = 1.2;
            if(mc.thePlayer.hurtTime > 0) {
                MovementUtils.vClip(0.5 - 0.00160);
                timer.reset();
                ++stage;
            }
        } else {
            moveSpeed = Math.max(moveSpeed -= moveSpeed / 145, MovementUtils.getBaseMoveSpeed());
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12, mc.thePlayer.posZ);
            mc.thePlayer.fallDistance -= 1E-12;
            e.setY(mc.thePlayer.motionY = 0);
            MovementUtils.setSpeed(e, moveSpeed);
        }
    }

    @Override
    public void onPacket(EventPacket e) {
        super.onPacket(e);
    }

    /*
    if(e.isPre()) {
        e.setY(mc.thePlayer.posY - Math.random() * (0.001 - 0.0001) + 0.0001);
        mc.thePlayer.motionY = -0.0055;
        if(mc.thePlayer.onGround)
            mc.thePlayer.jump();
        mc.timer.timerSpeed = 1.0855f;
    }
     */
}
