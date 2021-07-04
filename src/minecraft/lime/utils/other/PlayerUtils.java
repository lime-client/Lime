package lime.utils.other;

import lime.utils.IUtil;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PlayerUtils implements IUtil {
    public static MovingObjectPosition getTargetedBlock() {
        Vec3 eyeEight = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        Vec3 lookVec = mc.thePlayer.getLook(mc.timer.renderPartialTicks);
        Vec3 vec = eyeEight.addVector(lookVec.xCoord * 70, lookVec.yCoord * 70, lookVec.zCoord * 70);
        return mc.thePlayer.worldObj.rayTraceBlocks(eyeEight, vec, false, false, true);
    }
}
