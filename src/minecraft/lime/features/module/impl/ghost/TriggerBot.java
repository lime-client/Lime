package lime.features.module.impl.ghost;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.SlideValue;
import lime.utils.other.Timer;

@ModuleData(name = "Trigger Bot", category = Category.GHOST)
public class TriggerBot extends Module {

    private final SlideValue cps = new SlideValue("CPS", this, 1, 20, 8, 1);

    private final Timer cpsTimer = new Timer();

    @EventTarget
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            if(mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
                if(cpsTimer.hasReached(20 / cps.intValue() * 50)) {
                    mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                    mc.thePlayer.swingItem();
                    cpsTimer.reset();
                }
            }
        }
    }
}
