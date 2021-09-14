package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;

public class AAC extends FlightValue {
    public AAC() {
        super("AAC");
    }
    private boolean aac5nextFlag;
    private final Timer timer = new Timer();
    @Override
    public void onEnable() {
        aac5nextFlag = true;
        timer.reset();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        sendLeFunny();
        super.onDisable();
    }


    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C03PacketPlayer) {
            mc.thePlayer.motionY = 0;
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(2);
            } else {
                MovementUtils.setSpeed(0);
            }
            if(mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.motionY += 1;
            } else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.thePlayer.motionY -= 1;
            }
            C03PacketPlayer packetPlayer = (C03PacketPlayer) e.getPacket();
            double f=mc.thePlayer.width/2.0;
            if(aac5nextFlag || !mc.theWorld.checkBlockCollision(new AxisAlignedBB(packetPlayer.getPositionX() - f, packetPlayer.getPositionY(), packetPlayer.getPositionZ() - f, packetPlayer.getPositionX() + f, packetPlayer.getPositionY() + mc.thePlayer.height, packetPlayer.getPositionZ() + f))){
                aac5C03List.add(packetPlayer);
                aac5nextFlag=false;
                e.setCanceled(true);
                if(aac5C03List.size() >= 7) {
                    sendLeFunny();
                }
            }
        }
        super.onPacket(e);
    }

    private final ArrayList<C03PacketPlayer> aac5C03List = new ArrayList<>();

    public void sendLeFunny() {
        float yaw = mc.thePlayer.rotationYaw, pitch = mc.thePlayer.rotationPitch;
        for (C03PacketPlayer packet : aac5C03List) {
            if(packet.isMoving()) {
                mc.getNetHandler().sendPacketNoEvent(packet);
                if(packet.getRotating()) {
                    yaw = packet.getYaw();
                    pitch = packet.getPitch();
                }
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getPositionX(), -1e+159, packet.getPositionZ() + 10, yaw, pitch, true));
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getPositionX(), packet.getPositionY(), packet.getPositionZ(), yaw, pitch, true));
            }
        }
        aac5C03List.clear();
    }
}
