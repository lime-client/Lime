package lime.bot.mc.protocol.packet.ingame.server.entity;

import lime.bot.mc.protocol.data.MagicValues;
import lime.bot.mc.protocol.data.game.entity.EntityStatus;
import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.packetlib.io.NetInput;
import lime.bot.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerEntityStatusPacket extends MinecraftPacket {
    protected int entityId;
    protected EntityStatus status;

    @SuppressWarnings("unused")
    private ServerEntityStatusPacket() {
    }

    public ServerEntityStatusPacket(int entityId, EntityStatus status) {
        this.entityId = entityId;
        this.status = status;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public EntityStatus getStatus() {
        return this.status;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.entityId = in.readInt();
        this.status = MagicValues.key(EntityStatus.class, in.readByte());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeInt(this.entityId);
        out.writeByte(MagicValues.value(Integer.class, this.status));
    }
}
