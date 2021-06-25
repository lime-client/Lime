package lime.features.module.impl.world;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleData(name = "No Rotate", category = Category.WORLD)
public class NoRotate extends Module {
    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.isEntityAlive()) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            packet.setYaw(mc.thePlayer.rotationYaw);
            packet.setPitch(mc.thePlayer.rotationPitch);
            e.setPacket(packet);
        }
    }
}
