package lime.core.events.impl;

import lime.core.events.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class EventWorldChange extends Event {
    private final WorldClient world;

    public EventWorldChange(WorldClient world)
    {
        this.world = world;
    }

    public WorldClient getWorld() {
        return world;
    }
}
