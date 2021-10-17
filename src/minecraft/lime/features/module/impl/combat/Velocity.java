package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {

    public Velocity() {
        super("Velocity", Category.COMBAT);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Packet", "Packet");

    @EventTarget
    public void onUpdate(EventUpdate e) {
        setSuffix(mode.getSelected());
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {
            if(mode.is("packet")) {
                if(mc.thePlayer.capabilities.isFlying && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.contains("funcraft")) {
                    return;
                }
                e.setCanceled(true);
            }
        }
    }
}
