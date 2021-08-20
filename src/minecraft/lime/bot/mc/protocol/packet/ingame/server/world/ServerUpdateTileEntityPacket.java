package lime.bot.mc.protocol.packet.ingame.server.world;

import lime.bot.mc.protocol.data.MagicValues;
import lime.bot.mc.protocol.data.game.entity.metadata.Position;
import lime.bot.mc.protocol.data.game.world.block.UpdatedTileType;
import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.mc.protocol.util.NetUtil;
import lime.bot.opennbt.tag.builtin.CompoundTag;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerUpdateTileEntityPacket extends MinecraftPacket {
    private Position position;
    private UpdatedTileType type;
    private CompoundTag nbt;

    @SuppressWarnings("unused")
    private ServerUpdateTileEntityPacket() {
    }

    public ServerUpdateTileEntityPacket(Position position, UpdatedTileType type, CompoundTag nbt) {
        this.position = position;
        this.type = type;
        this.nbt = nbt;
    }

    public Position getPosition() {
        return this.position;
    }

    public UpdatedTileType getType() {
        return this.type;
    }

    public CompoundTag getNBT() {
        return this.nbt;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.position = NetUtil.readPosition(in);
        this.type = MagicValues.key(UpdatedTileType.class, in.readUnsignedByte());
        this.nbt = NetUtil.readNBT(in);
    }

    @Override
    public void write(NetOutput out) throws IOException {
        NetUtil.writePosition(out, this.position);
        out.writeByte(MagicValues.value(Integer.class, this.type));
        NetUtil.writeNBT(out, this.nbt);
    }
}
