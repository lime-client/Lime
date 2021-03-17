package lime.utils.other;

import lime.events.impl.EventMotion;
import net.minecraft.client.Minecraft;

public class OtherUtil {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static void doRotationsInThirdPerson(EventMotion eventMotion){
        if(mc.gameSettings.thirdPersonView != 0){
            mc.thePlayer.rotationYawHead = eventMotion.getYaw();
            mc.thePlayer.renderYawOffset = eventMotion.getYaw();
            mc.thePlayer.renderArmYaw = eventMotion.getYaw();
            mc.thePlayer.renderArmPitch = eventMotion.getPitch();
            mc.thePlayer.rotationPitchHead = eventMotion.getPitch();

        }
    }
}
