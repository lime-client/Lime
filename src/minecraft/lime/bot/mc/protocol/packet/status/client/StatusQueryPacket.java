package lime.bot.mc.protocol.packet.status.client;

import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class StatusQueryPacket extends MinecraftPacket {
    public StatusQueryPacket() {
    }

    @Override
    public void read(NetInput in) throws IOException {
    }

    @Override
    public void write(NetOutput out) throws IOException {
    }
}
