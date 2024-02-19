package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastEat extends Module {

    public FastEat() {
        super("Fast Eat", Category.PLAYER);
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            if(mc.thePlayer.isEating()) {
                for (int i = 0; i < 35; i++) {
                    mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer(mc.thePlayer.onGround));
                }
            }
        }
    }
}
