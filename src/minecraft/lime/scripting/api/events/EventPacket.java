package lime.scripting.api.events;

import jdk.nashorn.api.scripting.AbstractJSObject;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class EventPacket extends AbstractJSObject {

    private final lime.core.events.impl.EventPacket eventPacket;

    public EventPacket(lime.core.events.impl.EventPacket eventPacket)
    {
        this.eventPacket = eventPacket;
    }

    @Override
    public Object getMember(String name) {
        if(name.equals("isSending")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return eventPacket.getMode() == lime.core.events.impl.EventPacket.Mode.SEND;
                }
            };
        }
        if(name.equals("setCanceled")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    eventPacket.setCanceled(Boolean.parseBoolean(args[0] + ""));
                    return null;
                }
            };
        }
        if(name.equals("getPacketID")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    //retard but works
                    Packet<?> packet = eventPacket.getPacket();
                    if(packet instanceof C00PacketKeepAlive) {
                        return 0x00;
                    }
                    if(packet instanceof C01PacketChatMessage) {
                        return 0x01;
                    }
                    if(packet instanceof C02PacketUseEntity) {
                        return 0x02;
                    }
                    if(packet instanceof C03PacketPlayer) {
                        if(packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                            return 0x04;
                        }
                        if(packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
                            return 0x05;
                        }
                        if(packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                            return 0x06;
                        }
                        return 0x03;
                    }
                    if(packet instanceof C07PacketPlayerDigging) {
                        return 0x07;
                    }
                    if(packet instanceof C08PacketPlayerBlockPlacement) {
                        return 0x08;
                    }
                    if(packet instanceof C09PacketHeldItemChange) {
                        return 0x09;
                    }
                    // client side packet ^^
                    if(packet instanceof S08PacketPlayerPosLook) {
                        return 0x08;
                    }
                    if(packet instanceof S12PacketEntityVelocity) {
                        return 0x12;
                    }
                    return 0x69; // funni packet
                }
            };
        }
        return super.getMember(name);
    }
}
