package lime.features.module.impl.player;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.movement.LongJump;
import lime.features.setting.impl.EnumValue;
import org.apache.commons.lang3.StringUtils;

@ModuleData(name = "No Fall", category = Category.PLAYER)
public class NoFall extends Module {

    private EnumValue mode = new EnumValue("Mode", this, "Vanilla", "Vanilla", "Verus");

    @EventTarget
    public void onUpdate(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(e.isPre()) {
            if(mc.thePlayer.fallDistance > 2.5) {
                if(mode.is("vanilla")) {
                    e.setGround(true);
                    mc.thePlayer.fallDistance = 0;
                } else if(mode.is("verus")) {
                    if(Lime.getInstance().getModuleManager().getModuleC(LongJump.class).isToggled() && ((EnumValue) Lime.getInstance().getSettingsManager().getSetting("Mode", Lime.getInstance().getModuleManager().getModuleC(LongJump.class))).is("verus_bow")) return;
                    e.setGround(true);
                    mc.thePlayer.motionY = -0.0784000015258789;
                    mc.thePlayer.fallDistance = 0;
                }
            }
        }
    }
}
