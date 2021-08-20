package lime.bot.mc.protocol.packet.ingame.server.world;

import lime.bot.mc.protocol.data.game.entity.metadata.Position;
import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.mc.protocol.util.NetUtil;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerSpawnPositionPacket extends MinecraftPacket {
    private Position position;

    @SuppressWarnings("unused")
    private ServerSpawnPositionPacket() {
    }

    public ServerSpawnPositionPacket(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.position = NetUtil.readPosition(in);
    }

    @Override
    public void write(NetOutput out) throws IOException {
        NetUtil.writePosition(out, this.position);
    }
}
