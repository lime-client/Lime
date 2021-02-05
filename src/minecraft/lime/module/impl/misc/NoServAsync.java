package lime.module.impl.misc;

import lime.events.EventTarget;
import lime.events.impl.EventPacket;
import lime.module.Module;
import net.minecraft.network.play.server.S14PacketEntity;

public class NoServAsync extends Module {
    public NoServAsync(){
        super("NoServAsync", 0, Category.MISC);
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
        if(e.getPacket() instanceof S14PacketEntity.S16PacketEntityLook){
            e.setCancelled(true);
        }
    }
}
