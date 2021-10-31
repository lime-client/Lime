package lime.core.events.impl;

import lime.core.events.Event;
import net.minecraft.entity.Entity;

public class EventAttack extends Event {

    private final Entity entity;

    public EventAttack(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
