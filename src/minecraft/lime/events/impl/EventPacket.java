package lime.events.impl;

import lime.events.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
    Packet packet;
    PacketType packetType;
    public EventPacket(PacketType packetType, Packet packet){
        this.packetType = packetType;
        this.packet = packet;
    }
    public enum PacketType{
        RECEIVE, SEND
    }

    public Packet getPacket() {
        return packet;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

}