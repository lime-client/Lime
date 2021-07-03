package lime.features.module;

import lime.core.Lime;
import lime.core.events.EventBus;
import net.minecraft.client.Minecraft;

public abstract class Module {
    protected Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private final Category category;
    private int key;

    private boolean isToggled = false;

    public Module()
    {
        ModuleData moduleData = this.getClass().getAnnotation(ModuleData.class);
        this.name = moduleData.name();
        this.category = moduleData.category();
        this.key = moduleData.key();
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isToggled() {
        return isToggled;
    }

    public void toggle() {
        isToggled = !isToggled;

        if(isToggled)
            enable();
        else
            disable();
    }

    public final void enable() {
        EventBus.INSTANCE.register(this);
        onEnable();
    }

    public final void disable() {
        EventBus.INSTANCE.unregister(this);
        onDisable();
    }

    public void onEnable() { }
    public void onDisable() { }

    public boolean hasSettings() {
        return Lime.getInstance().getSettingsManager().getSettingsFromModule(this) != null && !Lime.getInstance().getSettingsManager().getSettingsFromModule(this).isEmpty();
    }
}
