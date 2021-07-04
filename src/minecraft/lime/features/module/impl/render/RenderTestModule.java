package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.movement.pathfinder.CustomVec;
import lime.utils.movement.pathfinder.utils.PathComputer;
import lime.utils.other.PlayerUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import lime.utils.time.DeltaTime;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

@ModuleData(name = "Render Test", category = Category.RENDER)
public class RenderTestModule extends Module {


    @Override
    public void onEnable() {
        if(mc.thePlayer != null) {
            BlockPos blockPos = PlayerUtils.getTargetedBlock().getBlockPos();
            CustomVec to = new CustomVec(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
            ArrayList<CustomVec> path = PathComputer.computePath(new CustomVec(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), to);
            for (CustomVec customVec : path) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(customVec.getX(), customVec.getY(), customVec.getZ(), false));
            }

            mc.thePlayer.setPosition(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
        }
    }

    @EventTarget
    public void onRender(Event2D e) {

    }
}
