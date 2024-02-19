package net.minecraft.network;

import java.io.IOException;

public abstract class RawPacket implements Packet {
    private final int packetID;
    private final EnumConnectionState direction;

    public RawPacket(int packetID, EnumConnectionState direction) {
        this.packetID = packetID;
        this.direction = direction;
    }

    public int getPacketID() {
        return packetID;
    }

    public EnumConnectionState getDirection() {
        return direction;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void processPacket(INetHandler handler) {

    }
}
