package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;

@ModuleData(name = "Fast Place", category = Category.PLAYER)
public class FastPlace extends Module {
    @EventTarget
    public void onUpdate(EventUpdate e) {
        mc.rightClickDelayTimer = 0;
    }
}
