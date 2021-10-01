package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {

    public Criticals() {
        super("Criticals", Category.COMBAT);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Packet", "Packet");
    private final SlideValue delay = new SlideValue("Delay", this, 100, 1000, 750, 50);

    private final Timer timer = new Timer();

    private int groundTicks = 0;

    @EventTarget
    public void onUpdate(EventUpdate e) {
        groundTicks = mc.thePlayer.onGround ? groundTicks + 1 : 0;
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        this.setSuffix(mode.getSelected());
        if(e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
            if(packet.getAction() == C02PacketUseEntity.Action.ATTACK && mc.thePlayer.onGround && groundTicks > 1 && timer.hasReached(delay.intValue())) {
                double[] offsets = {0.06252f, 0.0f};
                for (double offset : offsets) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset + (Math.random() * 0.0003F), mc.thePlayer.posZ, false));
                }
                mc.thePlayer.onCriticalHit(packet.getEntityFromWorld(mc.theWorld));
                timer.reset();
            }
        }
    }
}
