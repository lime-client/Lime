package lime.bot.mc.protocol.packet.ingame.server.entity.player;

import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerPlayerChangeHeldItemPacket extends MinecraftPacket {
    private int slot;

    @SuppressWarnings("unused")
    private ServerPlayerChangeHeldItemPacket() {
    }

    public ServerPlayerChangeHeldItemPacket(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.slot = in.readByte();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeByte(this.slot);
    }
}
