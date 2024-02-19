package lime.features.module.impl.world;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoC03 extends Module {
    public NoC03() {
        super("No C03", Category.WORLD);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer p = (C03PacketPlayer) e.getPacket();
            if(!p.isMoving() && !p.getRotating()) {
                e.setCanceled(true);
            }
        }
    }
}
