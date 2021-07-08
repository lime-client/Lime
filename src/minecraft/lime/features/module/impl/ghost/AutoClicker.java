package lime.features.module.impl.ghost;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;

@ModuleData(name = "Auto Clicker", category = Category.GHOST)
public class AutoClicker extends Module {
    @EventTarget
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            System.out.println("hi");
        }
    }
}
