package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.Timer;

@ModuleData(name = "Anti Void", category = Category.PLAYER)
public class AntiVoid extends Module {

    private enum Mode {
        Motion
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.Motion);
    private final SlideValue pullBack = new SlideValue("Pullback", this, 500, 3000, 500, 500);

    private final Timer timer = new Timer();

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected().name());
        if(!mc.thePlayer.onGround && mc.thePlayer.isEntityAlive() && MovementUtils.isVoidUnder()) {
            if(mode.is("motion")) {
                if(timer.hasReached((int) pullBack.getCurrent())) {
                    timer.reset();
                    mc.thePlayer.motionY = 1;
                }
            }
        } else timer.reset();
    }
}
