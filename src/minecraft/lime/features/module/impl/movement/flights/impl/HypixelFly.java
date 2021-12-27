package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.SPacketPlayerPosLook;
import lime.utils.other.Timer;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.RawPacket;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HypixelFly extends FlightValue {
    public HypixelFly() {
        super("Hypixel");
    }

    private final Queue<Packet<?>> packetQueue = new LinkedList<>();
    private final List<Integer> teleports = new ArrayList<>();
    private final Timer timer = new Timer();

    @Override
    public void onEnable() {
        timer.reset();
        packetQueue.clear();
    }

    @Override
    public void onDisable() {
        MovementUtils.setSpeed(0);
    }

    @Override
    public void onMotion(EventMotion e) {
        if (!e.isPre()) return;

        if (timer.hasReached(600)) {
            for (Packet<?> packet : packetQueue) {
                mc.getNetHandler().sendPacketNoEvent(packet);
            }
            packetQueue.clear();
            timer.reset();

            for (Integer teleport : teleports) {
                mc.getNetHandler().sendPacketNoEvent(new RawPacket(0x00,EnumConnectionState.PLAY) {
                    @Override
                    public void writePacketData(PacketBuffer buf) throws IOException {
                        buf.writeVarIntToBuffer(getPacketID());
                        buf.writeVarIntToBuffer(teleport);
                    }
                });
            }
            teleports.clear();
        }
        packetQueue.add(new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY(), e.getZ(), e.isGround()));
        e.setCanceled(true);

        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.42 : mc.gameSettings.keyBindSneak.isKeyDown() ? -.42 : 0;
        MovementUtils.setSpeed(mc.thePlayer.isMoving() ? 2 : 0);
    }

    @Override
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S01PacketJoinGame) {
            getFlight().toggle();
        }

        if(e.getPacket() instanceof SPacketPlayerPosLook) {
            teleports.add(((SPacketPlayerPosLook) e.getPacket()).getTeleportID());
        }
        super.onPacket(e);
    }
}
