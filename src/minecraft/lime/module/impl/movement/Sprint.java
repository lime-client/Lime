package lime.module.impl.movement;

import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import net.minecraft.potion.Potion;

public class Sprint extends Module {
    public Sprint(){
        super("Sprint", 0, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(false);
        super.onDisable();
    }

    @EventTarget
    public void onMotion(EventMotion e){
        if((mc.thePlayer.getFoodStats().getFoodLevel() > 6 || mc.thePlayer.capabilities.isCreativeMode) && mc.thePlayer.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isPotionActive(Potion.blindness)){
            mc.thePlayer.setSprinting(true);
        }
    }
}
