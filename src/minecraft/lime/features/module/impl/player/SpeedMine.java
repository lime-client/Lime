package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;

@ModuleData(name = "Speed Mine", category = Category.PLAYER)
public class SpeedMine extends Module {
    @EventTarget
    public void onUpdate(EventMotion e) {
        mc.playerController.blockHitDelay = 0;
        if(mc.playerController.curBlockDamageMP >= 0.7) {
            mc.playerController.curBlockDamageMP = 1;
        }
    }
}
