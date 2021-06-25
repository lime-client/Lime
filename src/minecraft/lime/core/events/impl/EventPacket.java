package lime.core.events.impl;

import lime.core.events.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
    public enum Mode {
        RECEIVE, SEND
    }

    private Packet packet;
    private final Mode mode;

    public EventPacket(Packet packet, Mode mode) {
        this.packet = packet;
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
