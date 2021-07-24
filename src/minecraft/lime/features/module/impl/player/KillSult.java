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
        if(e.getPacket() instanceof S02PacketChat && mc.getIntegratedServer() == null) {
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
                    mc.thePlayer.sendChatMessage("@" + getMessage(killedEntity) + " https://discord.gg/88 R T S 7P R F T");
                }
            }
        }
    }

    private String getMessage(String entityName) {
        String[] messages = new String[] { "T'es un peu le seul mec à finir 3ème dans un 1vs1 " + entityName,
                "J'ai du mal à faire la différence entre " + entityName + " et un armor stand :/",
        entityName + ", ton skill c'est comme une eclipse, il est bien qu'une fois par an",
                "Savoir jouer est un art qui t'es inconnu " + entityName,
                entityName + ", ALT+F4 devrait résoudre tes problèmes <3",
        entityName + ", reviens ici j'ai pas fini de te demarrer",
                entityName + ", la seule erreur dans cette partie c'est toi",
        entityName + ", si les meilleurs partent en premier tu va finir par etre immortel",
                entityName +", ta mère elle a tellement tourné qu'on l'appelle pegasuce",
        "l'anticheat est ma seule crainte",
                entityName + ", clique au sud la prochaine fois",
                "se faire ban c'est pas cheater",
        "T'as le pc, le clavier, la souris, il te manque plus que le niveau",
                "Désolé mec j'ai oublié de désactivé le fast math d'optifine"};

        return messages[ThreadLocalRandom.current().nextInt(messages.length)];
    }
}
