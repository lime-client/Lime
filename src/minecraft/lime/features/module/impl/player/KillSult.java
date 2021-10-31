package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.TextProperty;
import lime.utils.other.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class KillSult extends Module {

    private final TextProperty prefix = new TextProperty("Prefix", this, "@");
    public static ArrayList<String> killsults = new ArrayList<>();

    public KillSult() {
        super("KillSult", Category.PLAYER);
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

            if(killedSomeone(mc.session.getUsername(), ChatUtils.removeColors(message))) {
                // Get Entity name
                String killedEntity = getKilledEntity(message);


                if(!killedEntity.toLowerCase().contains(mc.getSession().getUsername().toLowerCase())) {
                    mc.thePlayer.sendChatMessage(prefix.getText() + getMessage(killedEntity));
                }
            }
        }
    }

    private boolean killedSomeone(String username, String message) {
        if(mc.getCurrentServerData() != null) {
            String serverIp = mc.getCurrentServerData().serverIP;
            if(serverIp.contains("funcraft")) {
                return message.contains("a été tué par " + username);
            }
            if(serverIp.contains("survivaldub")) {
                return message.contains("se cayó a un agujero negro por " + username) || message.contains("fue destrozado a manos de " + username);
            }
            if(serverIp.contains("mineplex") || serverIp.contains("tiddies.club")) {
                return serverIp.contains("killed by " + username);
            }
        }
        return false;
    }

    private String getKilledEntity(String message) {
        String[] splited = message.split(" ");
        for (String s : splited) {
            if(s.equals(mc.getSession().getUsername())) continue;
            List<EntityPlayer> entityPlayers = new ArrayList<>(mc.theWorld.playerEntities);
            AtomicReference<String> n = new AtomicReference<>("");
            entityPlayers.stream().filter(entityPlayer -> entityPlayer.getName().equals(s)).findFirst().ifPresent(e -> {
               n.set(e.getName());
            });
            if(!n.get().equals("")) {
                return n.get();
            }
        }
        return "";
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
