package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;

@ModuleData(name = "No Web", category = Category.MOVEMENT)
public class NoWeb extends Module {
    @EventTarget
    public void onMotion(EventMotion e) {
    }
}
