package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;

public class SpeedMine extends Module {

    public SpeedMine() {
        super("Speed Mine", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        mc.playerController.blockHitDelay = 0;
        if(mc.playerController.curBlockDamageMP >= 0.7) {
            mc.playerController.curBlockDamageMP = 1;
        }
    }
}
