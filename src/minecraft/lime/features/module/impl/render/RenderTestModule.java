package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.managers.FontManager;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.ColorValue;
import lime.utils.movement.MovementUtils;
import lime.utils.movement.pathfinder.CustomVec;
import lime.utils.movement.pathfinder.utils.PathComputer;
import lime.utils.other.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;

@ModuleData(name = "Render Test", category = Category.RENDER)
public class RenderTestModule extends Module {

    @Override
    public void onEnable() {
        MovementUtils.vClip(-0.085);
        CustomVec currentPath = new CustomVec(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        Vec3 eyeEight = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        Vec3 lookVec = mc.thePlayer.getLook(mc.timer.renderPartialTicks);
        Vec3 vec = eyeEight.addVector(lookVec.xCoord * 70, lookVec.yCoord * 70, lookVec.zCoord * 70);
        MovingObjectPosition mop =  mc.thePlayer.worldObj.rayTraceBlocks(eyeEight, vec, false, false, true);
        CustomVec targetPath = new CustomVec(mop.getBlockPos().getX(), mop.getBlockPos().getY(), mop.getBlockPos().getZ());
        ArrayList<CustomVec> paths = PathComputer.computePath(currentPath, targetPath, 1000, 1, 3);

        for (CustomVec path : paths) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(path.getX(), path.getY(), path.getZ(), false));
        }

        mc.thePlayer.setPosition(targetPath.getX(), targetPath.getY(), targetPath.getZ());
    }
}
