package lime.utils.other;

import lime.utils.IUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PlayerUtils implements IUtil {
    public static void verusDamage() {
        double posY = mc.thePlayer.posY;

        double[] values = {0.41999998688697815, 0.33319999363422426, 0.24813599859093927, 0.1647732818260721};

        for(int i = 0; i < 3; ++i) {
            for (double value : values) {
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY += value, mc.thePlayer.posZ, false));
            }
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY += 0.08307781780646906, mc.thePlayer.posZ, true));
        }

        double prevPosY = posY - 0.07840000152587834;
        while(posY > mc.thePlayer.posY) {
            double lastDist = posY - prevPosY;
            prevPosY = posY;

            posY += (lastDist - 0.08) * 0.98;
            if(posY > mc.thePlayer.posY) {
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX , posY, mc.thePlayer.posZ, false));
            }
        }
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.41999998688697815, mc.thePlayer.posZ, false));
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer(false));
    }

    public static void hypixelDamage() {
        for (int i = 0; i < 56; i++) {
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

    public static void koksCraftDamage() {
        double[] jumpValues = new double[] {
                0,
                0.41999998688698,
                0.7531999805212,
                1.00133597911215,
                1.166109260938214,
                1.24918707874468,
                1.25220334025373,
                1.17675927506424,
                1.024424088213685,
                0.7967356006687,
                0.495200877005914,
                0.121296840539195,
                0
        };
        double startPosY = mc.thePlayer.posY;
        for (int i = 0;i < 3;i++) {
            for (double jumpValue : jumpValues) {
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, startPosY + jumpValue, mc.thePlayer.posZ, false));
            }
        }
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, startPosY, mc.thePlayer.posZ, true));
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer(true));
    }
}
