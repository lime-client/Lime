package lime.core.events.impl;

import lime.core.events.Event;

public class EventMove extends Event {
    private double x, y, z;

    public EventMove(double x, double y, double z) {
        this.x = x;
        this.z = z;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
