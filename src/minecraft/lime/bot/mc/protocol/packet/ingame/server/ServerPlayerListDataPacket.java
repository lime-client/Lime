package lime.bot.mc.protocol.packet.ingame.server;

import lime.bot.mc.protocol.data.message.Message;
import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerPlayerListDataPacket extends MinecraftPacket {
    private Message header;
    private Message footer;

    @SuppressWarnings("unused")
    private ServerPlayerListDataPacket() {
    }

    public ServerPlayerListDataPacket(Message header, Message footer) {
        this.header = header;
        this.footer = footer;
    }

    public Message getHeader() {
        return this.header;
    }

    public Message getFooter() {
        return this.footer;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.header = Message.fromString(in.readString());
        this.footer = Message.fromString(in.readString());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeString(this.header.toJsonString());
        out.writeString(this.footer.toJsonString());
    }
}
