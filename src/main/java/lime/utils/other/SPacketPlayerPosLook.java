package lime.utils.other;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.io.IOException;
import java.util.Set;

public class SPacketPlayerPosLook implements Packet {
    private double x, y, z;
    private float yaw, pitch;
    private Set<S08PacketPlayerPosLook.EnumFlags> flags;
    private int teleportID;

    public SPacketPlayerPosLook(double x, double y, double z, float yaw, float pitch, int teleportID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.teleportID = teleportID;
    }

    public int getTeleportID() {
        return teleportID;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void processPacket(INetHandler handler) {

    }
}
