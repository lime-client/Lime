package lime.scripting.api.events;

import jdk.nashorn.api.scripting.AbstractJSObject;

public class EventMotion extends AbstractJSObject {
    private final lime.core.events.impl.EventMotion eventMotion;

    public EventMotion(lime.core.events.impl.EventMotion e)
    {
        this.eventMotion = e;
    }

    @Override
    public Object getMember(String name) {
        if(name.equals("isPre")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return eventMotion.isPre();
                }
            };
        }
        if(name.equals("setCanceled")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    eventMotion.setCanceled(Boolean.parseBoolean(args[0] + ""));
                    return null;
                }
            };
        }
        if(name.equals("setOnGround")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    eventMotion.setGround(Boolean.parseBoolean(args[0] + ""));
                    return null;
                }
            };
        }
        return super.getMember(name);
    }
}
