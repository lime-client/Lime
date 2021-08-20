package lime.bot.mc.protocol.packet.ingame.server;

import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerSwitchCameraPacket extends MinecraftPacket {
    private int cameraEntityId;

    @SuppressWarnings("unused")
    private ServerSwitchCameraPacket() {
    }

    public ServerSwitchCameraPacket(int cameraEntityId) {
        this.cameraEntityId = cameraEntityId;
    }

    public int getCameraEntityId() {
        return this.cameraEntityId;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.cameraEntityId = in.readVarInt();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.cameraEntityId);
    }
}
