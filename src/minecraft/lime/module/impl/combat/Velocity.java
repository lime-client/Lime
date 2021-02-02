package lime.module.impl.combat;

import lime.events.EventTarget;
import lime.events.impl.EventPacket;
import lime.module.Module;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {
    public Velocity(){
        super("Velocity", 0, Category.COMBAT);
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
        if(e.getPacketType() == EventPacket.PacketType.RECEIVE && (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion)){
            e.setCancelled(true);
        }
    }
}
