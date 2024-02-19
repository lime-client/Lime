package lime.core.events.impl;

import lime.core.events.Event;
import net.minecraft.client.Minecraft;

public class EventEntityAction extends Event {
    private boolean shouldJump;
    private boolean shouldSneak;
    private boolean shouldSprint;

    public EventEntityAction() {
        Minecraft mc = Minecraft.getMinecraft();
        this.shouldJump = mc.gameSettings.keyBindJump.isKeyDown();
        this.shouldSneak = mc.gameSettings.keyBindSneak.isKeyDown();
        this.shouldSprint = true;
    }

    public boolean isShouldJump() {
        return shouldJump;
    }

    public boolean isShouldSneak() {
        return shouldSneak;
    }

    public boolean isShouldSprint() {
        return shouldSprint;
    }

    public void setShouldJump(boolean shouldJump) {
        this.shouldJump = shouldJump;
    }

    public void setShouldSneak(boolean shouldSneak) {
        this.shouldSneak = shouldSneak;
    }

    public void setShouldSprint(boolean shouldSprint) {
        this.shouldSprint = shouldSprint;
    }
}
