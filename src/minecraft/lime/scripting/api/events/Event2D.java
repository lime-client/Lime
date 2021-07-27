package lime.scripting.api.events;

import jdk.nashorn.api.scripting.AbstractJSObject;

public class Event2D extends AbstractJSObject {

    private final lime.core.events.impl.Event2D event2D;
    public Event2D(lime.core.events.impl.Event2D e) {
        this.event2D = e;
    }

    public lime.core.events.impl.Event2D getEvent2D() {
        return event2D;
    }

    @Override
    public Object getMember(String name) {
        if(name.equals("getWidth")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return getEvent2D().getScaledResolution().getScaledWidth();
                }
            };
        }
        if(name.equals("getHeight")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return getEvent2D().getScaledResolution().getScaledHeight();
                }
            };
        }
        return super.getMember(name);
    }
}
