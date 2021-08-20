package lime.bot.mc.protocol.packet.ingame.server.scoreboard;

import lime.bot.mc.protocol.data.MagicValues;
import lime.bot.mc.protocol.data.game.scoreboard.ScoreboardPosition;
import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerDisplayScoreboardPacket extends MinecraftPacket {
    private ScoreboardPosition position;
    private String name;

    @SuppressWarnings("unused")
    private ServerDisplayScoreboardPacket() {
    }

    public ServerDisplayScoreboardPacket(ScoreboardPosition position, String name) {
        this.position = position;
        this.name = name;
    }

    public ScoreboardPosition getPosition() {
        return this.position;
    }

    public String getScoreboardName() {
        return this.name;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.position = MagicValues.key(ScoreboardPosition.class, in.readByte());
        this.name = in.readString();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeByte(MagicValues.value(Integer.class, this.position));
        out.writeString(this.name);
    }
}
