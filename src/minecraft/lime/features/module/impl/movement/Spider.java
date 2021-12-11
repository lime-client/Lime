package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumProperty;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;

public class Spider extends Module {
    public Spider() {
        super("Spider", Category.MOVE);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Verus", "Verus");

    private boolean finished;

    @Override
    public void onEnable() {
        finished = false;
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        setSuffix(mode.getSelected());
        if(mode.is("verus") && mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled() && !Lime.getInstance().getModuleManager().getModuleC(LongJump.class).isToggled() && new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ).getBlock() instanceof BlockAir) {
            mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 2 == 0 ? 0.41999998688697815 : 0.33319999363422426;
            finished = false;
        } else if(!finished) {
            mc.thePlayer.motionY = -0.0784000015258789;
            finished = true;
        }
    }
}
