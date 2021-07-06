package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.concurrent.ThreadLocalRandom;

@ModuleData(name = "KillSult", category = Category.PLAYER)
public class KillSult extends Module {
    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S02PacketChat) {
            String message = ((S02PacketChat) e.getPacket()).getChatComponent().getUnformattedText();

            if(message.toLowerCase().contains(mc.session.getUsername().toLowerCase()) && (message.toLowerCase().contains("tué") || message.toLowerCase().contains("slain") || message.toLowerCase().contains("killed") || message.toLowerCase().contains("rekt"))) {
                // Get Entity name
                String killedEntity = "";

                if(message.contains("§")) {
                    killedEntity = message.replaceAll("§1", "").replaceAll("§2", "").replaceAll("§3", "")
                            .replaceAll("§4", "").replaceAll("§5", "").replaceAll("§6", "")
                            .replaceAll("§7", "").replaceAll("§8", "").replaceAll("§9", "")
                            .replaceAll("§0", "").replaceAll("§a", "").replaceAll("§b", "")
                            .replaceAll("§c", "").replaceAll("§d", "").replaceAll("§e", "")
                            .replaceAll("§f", "").split(" ")[1];
                } else {
                    killedEntity = message.split(" ")[1];
                }


                if(!killedEntity.toLowerCase().contains(mc.getSession().getUsername().toLowerCase())) {
                    mc.thePlayer.sendChatMessage("@" + getMessage(killedEntity));
                }
            }
        }
    }

    private String getMessage(String entityName) {
        String[] messages = new String[] { "Windows 11 > " + entityName, entityName + ", désolé du viol, tu veux de la ventoline ?",
        entityName + ", tu préfères le chocolat ou les noirs ?", entityName + ", fly pas trop tu vas te faire kick chacal", entityName + ", vise l'ennemi la prochaine fois",
        "Imposteur éliminé: " + entityName + " !", "Désolé " + entityName + ", j'ai glissé sur ma touche pour te violer!", "Honnêtement, t'étais claqué cette game " + entityName,
        "J'ai cru t'étais un bot " + entityName + "...", "J't'ai tapé autant vite que eminem rape " + entityName, "Dégustation en live de " + entityName + " !",
        "Un billet vers le goulag a été offert à " + entityName + " !"};

        return messages[ThreadLocalRandom.current().nextInt(messages.length)];
    }
}
