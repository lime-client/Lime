package lime.bot.mc.protocol.packet.ingame.server;

import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerResourcePackSendPacket extends MinecraftPacket {
    private String url;
    private String hash;

    @SuppressWarnings("unused")
    private ServerResourcePackSendPacket() {
    }

    public ServerResourcePackSendPacket(String url, String hash) {
        this.url = url;
        this.hash = hash;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.url = in.readString();
        this.hash = in.readString();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeString(this.url);
        out.writeString(this.hash);
    }
}
