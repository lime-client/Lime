package lime.events.impl;

import lime.events.Event;

public class EventSlow extends Event {
    public float moveStafeSlow, moveForwardSlow;
    public EventSlow(float moveStrafe, float moveForward){
        this.moveForwardSlow = moveForward;
        this.moveStafeSlow = moveStrafe;
    }

    public float getMoveForwardSlow() {
        return moveForwardSlow;
    }

    public float getMoveStafeSlow() {
        return moveStafeSlow;
    }

    public void setMoveForwardSlow(float moveForwardSlow) {
        this.moveForwardSlow = moveForwardSlow;
    }

    public void setMoveStafeSlow(float moveStafeSlow) {
        this.moveStafeSlow = moveStafeSlow;
    }
}
