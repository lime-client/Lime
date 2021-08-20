package lime.bot.mc.protocol.packet;

import lime.bot.mc.protocol.util.ObjectUtil;
import lime.bot.packetlib.packet.Packet;

public abstract class MinecraftPacket implements Packet {
    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return ObjectUtil.toString(this);
    }
}
