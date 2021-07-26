package lime.scripting.api.events;

import jdk.nashorn.api.scripting.AbstractJSObject;

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
                    return Integer.parseInt(eventPacket.getPacket().getClass().getSimpleName().toLowerCase().substring(1).split("packet")[0]);
                }
            };
        }
        return super.getMember(name);
    }
}
