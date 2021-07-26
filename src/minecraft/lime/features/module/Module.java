package lime.features.module;

import lime.core.Lime;
import lime.core.events.EventBus;
import lime.ui.notifications.Notification;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.Minecraft;

public abstract class Module {
    protected Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private final Category category;
    private int key;

    // Animation (for HUD)
    public final Animate hudAnimation = new Animate();

    private String suffix;

    private boolean isToggled = false;

    public Module()
    {
        ModuleData moduleData = this.getClass().getAnnotation(ModuleData.class);
        this.name = moduleData.name();
        this.category = moduleData.category();
        this.key = moduleData.key();
        this.hudAnimation.setEase(Easing.CUBIC_OUT);
        hudAnimation.setSpeed(125);
    }

    public Module(String name, int key, Category category)
    {
        this.name = name;
        this.key = key;
        this.category = category;
        this.hudAnimation.setEase(Easing.CUBIC_OUT);
        hudAnimation.setSpeed(125);
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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

    public void enableModule() {
        if(!isToggled())
            toggle();
    }

    public void disableModule() {
        if(isToggled())
            toggle();
    }

    public void toggle() {
        isToggled = !isToggled;

        if(isToggled)
            enable();
        else
            disable();
    }

    private void enable() {
        EventBus.INSTANCE.register(this);
        onEnable();
    }

    private void disable() {
        EventBus.INSTANCE.unregister(this);
        onDisable();
    }

    public void onEnable() { }
    public void onDisable() { }

    public boolean hasSettings() {
        return Lime.getInstance().getSettingsManager().getSettingsFromModule(this) != null && !Lime.getInstance().getSettingsManager().getSettingsFromModule(this).isEmpty();
    }
}
