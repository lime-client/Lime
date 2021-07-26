package lime.scripting.api.events;

import jdk.nashorn.api.scripting.AbstractJSObject;

public class EventMove extends AbstractJSObject {

    private final lime.core.events.impl.EventMove eventMove;

    public EventMove(lime.core.events.impl.EventMove e) {
        this.eventMove = e;
    }

    public lime.core.events.impl.EventMove getEventMove() {
        return eventMove;
    }

    @Override
    public Object getMember(String name) {
        if(name.equals("getY")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return eventMove.getY();
                }
            };
        }
        if(name.equals("setY")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    getEventMove().setY(Double.parseDouble(args[0] + ""));
                    return null;
                }
            };
        }
        return super.getMember(name);
    }
}
