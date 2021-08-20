package lime.bot.mc.protocol.packet.ingame.server.window;

import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerCloseWindowPacket extends MinecraftPacket {
    private int windowId;

    @SuppressWarnings("unused")
    private ServerCloseWindowPacket() {
    }

    public ServerCloseWindowPacket(int windowId) {
        this.windowId = windowId;
    }

    public int getWindowId() {
        return this.windowId;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.windowId = in.readUnsignedByte();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeByte(this.windowId);
    }
}
