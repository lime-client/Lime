package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.potion.Potion;

@ModuleData(name = "Sprint", category = Category.MOVEMENT)
public class Sprint extends Module {
    @EventTarget
    public void onMotion(EventMotion e) {
        if((mc.thePlayer.getFoodStats().getFoodLevel() > 6 || mc.thePlayer.capabilities.isCreativeMode) && mc.thePlayer.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isPotionActive(Potion.blindness)){
            mc.thePlayer.setSprinting(true);
        }
    }
}
