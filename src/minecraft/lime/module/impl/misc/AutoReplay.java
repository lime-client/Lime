package lime.module.impl.misc;

import lime.settings.impl.BooleanValue;
import lime.settings.impl.SlideValue;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventPacket;
import lime.module.Module;
import lime.utils.Timer;
import net.minecraft.network.play.server.S02PacketChat;

public class AutoReplay extends Module {
    SlideValue delay = new SlideValue("Delay", this, 100, 1, 1000, true);
    BooleanValue autoGG = new BooleanValue("Auto GG", this, true);
    public AutoReplay(){
        super("AutoReplay", 0, Category.MISC);
    }
    boolean send = false;
    Timer timer = new Timer();

    @Override
    public void onEnable() {
        send = false;
        timer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onMotion(EventMotion e){
        if(send){
            if(timer.hasReached(delay.getIntValue())){
                if(autoGG.getValue()) mc.thePlayer.sendChatMessage("@gg");
                mc.thePlayer.sendChatMessage("/re");
                timer.reset();
                send = false;
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e){
        if(e.getPacket() instanceof S02PacketChat){
            S02PacketChat packet = (S02PacketChat) e.getPacket();
            if(mc.getIntegratedServer() == null){
                if(mc.getCurrentServerData().serverIP.toLowerCase().contains("funcraft")){
                    if(packet.getChatComponent().getUnformattedText().contains("Afficher vos statistiques") && packet.getChatComponent().getUnformattedText().contains("Retourner au lobby") && !send){
                        send = true;
                    } else if(packet.getChatComponent().getUnformattedText().contains("Yeah !") && packet.getChatComponent().getUnformattedText().contains("Temps de jeu") && packet.getChatComponent().getUnformattedText().contains("Vous remportez") && !send){
                        send = true;
                    }
                }
            }
        }
    }
}
