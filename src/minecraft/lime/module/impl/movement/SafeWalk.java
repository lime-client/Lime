package lime.module.impl.movement;

import lime.events.EventTarget;
import lime.events.impl.EventSafeWalk;
import lime.module.Module;

public class SafeWalk extends Module {
    public SafeWalk(){
        super("SafeWalk", 0, Category.MOVEMENT);
    }
    @EventTarget
    public void onSafeWalk(EventSafeWalk e){
        e.setCancelled(true);
    }
}
