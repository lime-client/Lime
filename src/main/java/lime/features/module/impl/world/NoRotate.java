package lime.features.module.impl.world;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {

    public NoRotate() {
        super("No Rotate", Category.WORLD);
    }

    private final BooleanProperty clientSide = new BooleanProperty("Client Side", this, false);

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.isEntityAlive()) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
            packet.setYaw(mc.thePlayer.rotationYaw);
            packet.setPitch(mc.thePlayer.rotationPitch);
            e.setPacket(packet);
        }
    }
}
