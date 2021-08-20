package lime.bot.mc.protocol.packet.ingame.server.world;

import lime.bot.mc.protocol.data.game.world.block.BlockChangeRecord;
import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.mc.protocol.util.NetUtil;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerBlockChangePacket extends MinecraftPacket {
    private BlockChangeRecord record;

    @SuppressWarnings("unused")
    private ServerBlockChangePacket() {
    }

    public ServerBlockChangePacket(BlockChangeRecord record) {
        this.record = record;
    }

    public BlockChangeRecord getRecord() {
        return this.record;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.record = new BlockChangeRecord(NetUtil.readPosition(in), NetUtil.readBlockState(in));
    }

    @Override
    public void write(NetOutput out) throws IOException {
        NetUtil.writePosition(out, this.record.getPosition());
        NetUtil.writeBlockState(out, this.record.getBlock());
    }
}
