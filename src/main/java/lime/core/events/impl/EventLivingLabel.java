package lime.core.events.impl;

import lime.core.events.Event;
import net.minecraft.entity.Entity;

public class EventLivingLabel extends Event {
    private final Entity entity;

    public EventLivingLabel(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
