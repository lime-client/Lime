package lime.features.command.impl;

import lime.core.events.EventBus;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.command.Command;
import lime.utils.other.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StaffChecker extends Command {

    public StaffChecker() {
        // TODO: Get all moderators with website
        moderators.add("Cacciello");
        moderators.add("Declane");
        moderators.add("Zerkoy");
        moderators.add("C_far");
        moderators.add("Acenox");
        moderators.add("Mohul_");
        moderators.add("Cocoa689");
        moderators.add("Speccy");
        moderators.add("TheChess");
        moderators.add("Victoiire");
        moderators.add("ToInOuZe");
        moderators.add("Alexiiaa");
        moderators.add("Will14");
        moderators.add("MonsterKiid");
        moderators.add("moyoguichard1");
        moderators.add("Killbiz");
        moderators.add("Bassirou");
        moderators.add("Mamak_");
        moderators.add("Walidoow");
        moderators.add("NextSap");
        moderators.add("UnTitusSauvage");
        moderators.add("Astroow");
        moderators.add("Khassym");
        moderators.add("Kardiox");
        moderators.add("Failancy");
        moderators.add("Sxgo");
        moderators.add("SkyWarZzeur");
    }

    private final List<String> moderators = new ArrayList<>();
    private final List<String> onlineModerators = new ArrayList<>();

    @Override
    public String getUsage() {
        return "staffchecker";
    }

    @Override
    public String[] getPrefixes() {
        return new String[]{"staffchecker", "staff", "stafflist"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        onlineModerators.clear();
        EventBus.INSTANCE.register(this);
        new Thread(() -> {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                Queue<String> mods = new LinkedList<>(moderators);
                while(mods.size() > 0) {
                    mc.getNetHandler().addToSendQueue(new C01PacketChatMessage("/f add " + mods.poll()));
                    Thread.sleep(850);
                }
            } catch (Exception ignored){}
        }).start();
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S02PacketChat) {
            String message = ((S02PacketChat) e.getPacket()).getChatComponent().getFormattedText();
            if(message.toLowerCase().contains("amis") || message.contains("connecté")) {
                if(message.contains("envoyée à ")) {
                    onlineModerators.add(message.split("à ")[1].split(" !")[0]);
                }
                e.setCanceled(true);

                if(message.toLowerCase().contains(moderators.get(moderators.size() - 1).toLowerCase())) {
                    ChatUtils.sendMessageWithoutWatermark("§7Detected §a" + onlineModerators.size() + " §7staffs: ");
                    for (String onlineModerator : onlineModerators) {
                        ChatUtils.sendMessageWithoutWatermark("§7 - §a" + onlineModerator.replace("§r§b", ""));
                    }

                    EventBus.INSTANCE.unregister(this);
                }
            }
        }
    }
}
