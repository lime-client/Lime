package lime.events.impl;

import lime.events.Event;

public class EventKey extends Event {
    public EventKey(int key){
        this.key = key;
    }
    public int key;
}
