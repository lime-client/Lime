package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;
import net.minecraft.potion.Potion;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", Category.MOVE);
    }

    private final BoolValue omni = new BoolValue("Omni", this, false);

    @EventTarget
    public void onMotion(EventMotion e) {
        if((mc.thePlayer.getFoodStats().getFoodLevel() > 6 || mc.thePlayer.capabilities.isCreativeMode) && ((mc.thePlayer.isMoving() && omni.isEnabled()) || mc.thePlayer.moveForward > 0) && !mc.thePlayer.isSneaking() && !mc.thePlayer.isEating() && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isPotionActive(Potion.blindness)){
            mc.thePlayer.setSprinting(true);
        }
    }
}
