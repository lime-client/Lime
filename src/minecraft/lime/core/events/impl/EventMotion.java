package lime.core.events.impl;

import lime.core.events.Event;

public class EventMotion extends Event {
    private State type;
    private double x, y, z;
    private float yaw, pitch;
    private boolean ground, sprint, sneak;

    public EventMotion(State type, double x, double y, double z, float yaw, float pitch, boolean ground, boolean sprint, boolean sneak) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
        this.sprint = sprint;
        this.sneak = sneak;
    }

    public boolean isPre(){ return getState() == State.PRE; }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isGround() {
        return ground;
    }

    public void setGround(boolean ground) {
        this.ground = ground;
    }

    public boolean isSprint() {
        return sprint;
    }

    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }

    public boolean isSneaking() {
        return sneak;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public State getState() {
        return type;
    }

    public enum State {
        PRE, POST
    }
}
