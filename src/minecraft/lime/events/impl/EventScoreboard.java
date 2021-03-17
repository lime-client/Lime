package lime.events.impl;

import lime.events.Event;

public class EventScoreboard extends Event {
    int x = 0;
    int y = 0;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }
}
