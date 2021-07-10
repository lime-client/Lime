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

    public static String removeColors(String s) {
        return s.replaceAll("§1", "").replaceAll("§2", "").replaceAll("§3", "")
                .replaceAll("§4", "").replaceAll("§5", "").replaceAll("§6", "")
                .replaceAll("§7", "").replaceAll("§8", "").replaceAll("§9", "")
                .replaceAll("§0", "").replaceAll("§a", "").replaceAll("§b", "")
                .replaceAll("§c", "").replaceAll("§d", "").replaceAll("§e", "")
                .replaceAll("§f", "");
    }
}
