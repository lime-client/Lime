package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ModuleData(name = "Chat Bypass", category = Category.PLAYER)
public class ChatBypass extends Module {
    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage) e.getPacket();
            String message = packet.getMessage();
            
            if(!message.startsWith("/") && !message.startsWith(".")) {
                StringBuilder bypassedChatMessage = new StringBuilder();
                for (char c : message.toCharArray()) {
                    bypassedChatMessage.append(c + (ThreadLocalRandom.current().nextBoolean() ? "\u061D" : ""));
                }

                packet.setMessage(bypassedChatMessage.toString());
            }
        }

    }
}
