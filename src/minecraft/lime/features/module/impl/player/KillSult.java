package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.TextValue;
import lime.utils.other.ChatUtils;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@ModuleData(name = "KillSult", category = Category.PLAYER)
public class KillSult extends Module {

    private final TextValue prefix = new TextValue("Prefix", this, "@");
    public static ArrayList<String> killsults = new ArrayList<>();

    public KillSult() {
        if(!new File("Lime" + File.separator + "killsults.txt").exists()) {
            try {
                FileUtils.write(new File("Lime" + File.separator + "killsults.txt"), String.join("\n", getMessages()));
            } catch (IOException e) {
                System.out.println("Failed to save KillSults");
            }
        }

        try {
            loadKillsults(killsults);
        } catch (Exception ignored) { }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S02PacketChat && mc.getIntegratedServer() == null) {
            String message = ((S02PacketChat) e.getPacket()).getChatComponent().getUnformattedText();

            if(message.toLowerCase().contains(mc.session.getUsername().toLowerCase()) && (message.toLowerCase().contains("tué") || message.toLowerCase().contains("slain") || message.toLowerCase().contains("killed") || message.toLowerCase().contains("rekt"))) {
                // Get Entity name
                String killedEntity = "";

                if(message.contains("§")) {
                    killedEntity = ChatUtils.removeColors(message).split(" ")[1];
                } else {
                    killedEntity = message.split(" ")[1];
                }


                if(!killedEntity.toLowerCase().contains(mc.getSession().getUsername().toLowerCase())) {
                    mc.thePlayer.sendChatMessage(prefix.getText() + getMessage(killedEntity));
                }
            }
        }
    }

    private String getMessage(String entityName) {
        return killsults.get(ThreadLocalRandom.current().nextInt(killsults.size())).replace("{entityName}", entityName);
    }

    private String[] getMessages() {
        String entityName = "{entityName}";
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

        return messages;
        //return messages[ThreadLocalRandom.current().nextInt(messages.length)];
    }

    public static void loadKillsults(ArrayList<String> arrayList) throws IOException {
        arrayList.clear();
        String[] loadedKillsults = FileUtils.readFileToString(new File("Lime" + File.separator + "killsults.txt")).split("\n");
        arrayList.addAll(Arrays.asList(loadedKillsults));
    }
}
