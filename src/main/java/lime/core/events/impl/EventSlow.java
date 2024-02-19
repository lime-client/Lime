package lime.core.events.impl;

import lime.core.events.Event;

public class EventSlow extends Event {
    private float moveForward, moveStrafing;

    public EventSlow(float moveForward, float moveStrafing) {
        this.moveForward = moveForward;
        this.moveStrafing = moveStrafing;
    }

    public float getMoveForward() {
        return moveForward;
    }

    public float getMoveStrafing() {
        return moveStrafing;
    }

    public void setMoveForward(float moveForward) {
        this.moveForward = moveForward;
    }

    public void setMoveStrafing(float moveStrafing) {
        this.moveStrafing = moveStrafing;
    }
}
