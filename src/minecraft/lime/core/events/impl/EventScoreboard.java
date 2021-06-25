package lime.core.events.impl;

import lime.core.events.Event;

public class EventScoreboard extends Event {
    private int x, y;

    public EventScoreboard() {
        this.x = 0;
        this.y = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
