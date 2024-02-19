package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;

public class FastPlace extends Module {

    public FastPlace() {
        super("Fast Place", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        mc.rightClickDelayTimer = 0;
    }
}
