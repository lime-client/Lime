package lime.events.impl;

import lime.events.Event;
import net.minecraft.entity.Entity;

public class EventNameTags extends Event {
    Entity ent;
    public EventNameTags(Entity ent){
        this.ent = ent;
    }

    public Entity getEntity() {
        return ent;
    }
}
