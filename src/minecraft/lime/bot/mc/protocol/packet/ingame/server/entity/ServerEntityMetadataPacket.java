package lime.bot.mc.protocol.packet.ingame.server.entity;

import lime.bot.mc.protocol.data.game.entity.metadata.EntityMetadata;
import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.mc.protocol.util.NetUtil;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerEntityMetadataPacket extends MinecraftPacket {
    private int entityId;
    private EntityMetadata metadata[];

    @SuppressWarnings("unused")
    private ServerEntityMetadataPacket() {
    }

    public ServerEntityMetadataPacket(int entityId, EntityMetadata metadata[]) {
        this.entityId = entityId;
        this.metadata = metadata;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public EntityMetadata[] getMetadata() {
        return this.metadata;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.entityId = in.readVarInt();
        this.metadata = NetUtil.readEntityMetadata(in);
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.entityId);
        NetUtil.writeEntityMetadata(out, this.metadata);
    }
}
