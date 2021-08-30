package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumValue;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {

    public Criticals() {
        super("Criticals", Category.COMBAT);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Packet", "Packet");

    private final Timer timer = new Timer();

    @EventTarget
    public void onPacket(EventPacket e) {
        this.setSuffix(mode.getSelected());
        if(e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
            if(packet.getAction() == C02PacketUseEntity.Action.ATTACK && mc.thePlayer.onGround && timer.hasReached(450)) {
                double[] offsets = {.11, .1100013579, 1.3579E-6};
                for (double offset : offsets) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                }
                mc.thePlayer.onCriticalHit(packet.getEntityFromWorld(mc.theWorld));
                timer.reset();
            }
        }
    }
}
