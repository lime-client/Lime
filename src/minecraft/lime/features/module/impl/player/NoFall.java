package lime.features.module.impl.player;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.movement.HighJump;
import lime.features.module.impl.movement.LongJump;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.utils.movement.MovementUtils;

public class NoFall extends Module {

    public NoFall() {
        super("No Fall", Category.PLAYER);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Vanilla", "Vanilla", "Collide");
    private final BooleanProperty voidCheck = new BooleanProperty("Void Check", this, true);
    private int ticks;

    @Override
    public void onEnable() {
        ticks = 0;
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        if(e.isPre()) {
            if(MovementUtils.isVoidUnder()) {
                ++ticks;
            } else {
                ticks = 0;
            }
        }

        if(ticks > 10 && voidCheck.isEnabled() && !Lime.getInstance().getModuleManager().getModuleC(HighJump.class).isToggled()) {
            return;
        }
        this.setSuffix(mode.getSelected());
        if(e.isPre()) {
            if(mc.thePlayer.fallDistance > 2.5) {
                if(mode.is("vanilla")) {
                    e.setGround(true);
                    mc.thePlayer.fallDistance = 0;
                } else if(mode.is("collide")) {
                    if(Lime.getInstance().getModuleManager().getModuleC(LongJump.class).isToggled() && ((EnumProperty) Lime.getInstance().getSettingsManager().getSetting("Mode", Lime.getInstance().getModuleManager().getModuleC(LongJump.class))).is("verus_bow")) return;
                    e.setGround(true);
                    e.setY((int)mc.thePlayer.posY);
                    mc.thePlayer.motionY = -0.0784000015258789;
                    mc.thePlayer.fallDistance = 0;
                }
            }
        }
    }
}
