package lime.utils.other;

import lime.utils.IUtil;
import net.minecraft.util.ChatComponentText;

public class ChatUtils implements IUtil {

    public static void sendMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText("§aLime §7» " + message));
    }

    public static void sendMessageWithoutWatermark(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
