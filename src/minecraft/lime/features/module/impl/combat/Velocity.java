package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.utils.other.ChatUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModuleData(name = "Velocity", category = Category.COMBAT)
public class Velocity extends Module {

    private final EnumValue mode = new EnumValue("Mode", this, "Packet", "Packet", "AAC");

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(mode.is("aac")) {
            if(!mc.thePlayer.onGround && mc.thePlayer.hurtTime > 0){
                mc.thePlayer.motionX *= 0.80f;
                mc.thePlayer.motionY *= 0.80f;
                mc.thePlayer.motionZ *= 0.80f;
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {
            if(mode.is("packet")) {
                e.setCanceled(true);
            }
        }
    }
}
