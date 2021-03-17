package lime.module.impl.render;

import lime.settings.impl.BooleanValue;
import lime.events.EventTarget;
import lime.events.impl.Event2D;
import lime.module.Module;

public class ChestESP extends Module {
    public BooleanValue customColor = new BooleanValue("Custom Color", this, true);

    public ChestESP(){
        super("ChestESP", 0, Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    @EventTarget
    public void on2D(Event2D e){
    }
}
