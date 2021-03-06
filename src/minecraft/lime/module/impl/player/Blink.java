package lime.module.impl.player;

import lime.events.EventTarget;
import lime.events.impl.EventPacket;
import lime.module.Module;
import net.minecraft.network.Packet;

import java.util.ArrayList;

public class Blink extends Module {
    public Blink(){
        super("Blink", 0, Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(!mc.isIntegratedServerRunning()){
            for(Packet packet : packetsToSend){
                mc.thePlayer.sendQueue.addToSendQueue(packet);
            }
        }

        super.onDisable();

    }
    ArrayList<Packet> packetsToSend = new ArrayList<>();

    @EventTarget
    public void onPacket(EventPacket e){
        if(e.getPacketType() == EventPacket.PacketType.SEND){
            packetsToSend.add(e.getPacket());
            e.setCancelled(true);
        }
    }
}
