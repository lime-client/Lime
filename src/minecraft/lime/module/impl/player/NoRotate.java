package lime.module.impl.player;

import lime.events.EventTarget;
import lime.events.impl.EventPacket;
import lime.module.Module;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {
    public NoRotate(){
        super("NoRotate", 0, Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    @EventTarget
    public void onPacketReceive(EventPacket e){
        if(e.getPacket() instanceof S08PacketPlayerPosLook){
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            packet.setYaw(mc.thePlayer.rotationYaw);
            packet.setPitch(mc.thePlayer.rotationPitch);
            e.setPacket(packet);
        }
    }
}
