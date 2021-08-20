package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.concurrent.ThreadLocalRandom;

public class ChatBypass extends Module {

    public ChatBypass() {
        super("Chat Bypass", Category.PLAYER);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage) e.getPacket();
            String message = packet.getMessage();

            // \u001D
            
            if(!message.startsWith("/") && !message.startsWith(".")) {
                StringBuilder bypassedChatMessage = new StringBuilder();
                for (char c : message.toCharArray()) {
                    bypassedChatMessage.append(c).append(ThreadLocalRandom.current().nextBoolean() ? "\u061D" : "");
                }

                packet.setMessage(bypassedChatMessage.toString());
            }
        }
    }
}
