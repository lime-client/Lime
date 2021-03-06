package lime.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatUtils {
    public static String limeChat = "§8[§cLime§8] §7";
    public static void sendMsg(String message){
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§8[§cLime§8] §7" + message));
    }
    public static void sendMsgWithout(String message){
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
