package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumValue;
import lime.utils.movement.MovementUtils;

public class Glide extends Module {

    public Glide() {
        super("Glide", Category.MOVEMENT);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Chunk", "Chunk");

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(!MovementUtils.isOnGround(2) && mode.is("chunk") && !mc.thePlayer.isOnLadder()) {
            mc.thePlayer.motionY = -0.0784000015258789;
        }
    }
}
