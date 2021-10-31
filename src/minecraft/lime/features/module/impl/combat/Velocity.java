package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {

    public Velocity() {
        super("Velocity", Category.COMBAT);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Packet", "Packet");
    private final NumberProperty h = new NumberProperty("Horizontal", this, 0, 100, 0, 1);
    private final NumberProperty v = new NumberProperty("Vertical", this, 0, 100, 0, 1);

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
                if(e.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                    packet.setMotionX((int) (packet.getMotionX() * (h.intValue() / 100f)));
                    packet.setMotionZ((int) (packet.getMotionZ() * (h.intValue() / 100f)));
                    packet.setMotionY((int) (packet.getMotionY() * (v.intValue() / 100f)));
                }
                if(e.getPacket() instanceof S27PacketExplosion) {
                    S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
                    packet.setField_149152_f((int) (packet.func_149149_c() * (h.intValue() / 100f)));
                    packet.setField_149159_h((int) (packet.func_149144_d() * (h.intValue() / 100f)));
                    packet.setField_149153_g((int) (packet.func_149147_e() * (v.intValue() / 100f)));
                }
                if(h.intValue() == 0 && v.intValue() == 0) {
                    e.setCanceled(true);
                }
            }
        }
    }
}
