package lime.utils.other;

import lime.utils.IUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PlayerUtils implements IUtil {

    public static MovingObjectPosition getTargetedBlock() {
        Vec3 eyeEight = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        Vec3 lookVec = mc.thePlayer.getLook(mc.timer.renderPartialTicks);
        Vec3 vec = eyeEight.addVector(lookVec.xCoord * 70, lookVec.yCoord * 70, lookVec.zCoord * 70);
        return mc.thePlayer.worldObj.rayTraceBlocks(eyeEight, vec, false, false, true);
    }

    public static void verusDamage() {
        double posY = mc.thePlayer.posY;
        double prevPosY = mc.thePlayer.prevPosY;

        double[] values = {0.41999998688697815, 0.33319999363422426, 0.24813599859093927, 0.1647732818260721};

        for(int i = 0; i < 3; ++i) {
            // Simulating that we jumping with collision fly
            for (double value : values) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY += value, mc.thePlayer.posZ, false));
            }
            // Reached the ground here.
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY += 0.08307781780646906, mc.thePlayer.posZ, true));
        }


        // Packet fall
        prevPosY = posY - 0.07840000152587834;
        while(posY > mc.thePlayer.posY) {
            double lastDist = posY - prevPosY;
            prevPosY = posY;

            posY += (lastDist - 0.08) * 0.98;
            if(posY > mc.thePlayer.posY) {
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX , posY, mc.thePlayer.posZ, false));
            }
        }


        // Saying that we reached the ground
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        // Simulating velocity so verus accept Velocity Packet
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
    }
}
