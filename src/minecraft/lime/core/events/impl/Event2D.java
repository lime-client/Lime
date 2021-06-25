package lime.core.events.impl;

import lime.core.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Event2D extends Event {

    private final ScaledResolution sr;
    private final float partialTicks;

    public Event2D(ScaledResolution sr, float partialTicks)
    {
        this.sr = sr;
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return sr;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
