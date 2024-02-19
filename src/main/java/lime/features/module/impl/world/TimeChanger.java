package lime.features.module.impl.world;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.NumberProperty;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class TimeChanger extends Module {

    public TimeChanger() {
        super("Time Changer", Category.WORLD);
    }

    private final NumberProperty time = new NumberProperty("Time", this, 0, 24000, 21000, 1000);
    private final BooleanProperty rain = new BooleanProperty("Rain", this, false);

    @EventTarget
    public void onMotion(EventMotion e) {
        mc.theWorld.setWorldTime((long) time.getCurrent());
        if(rain.isEnabled())
            mc.theWorld.setRainStrength(0);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S03PacketTimeUpdate) {
            e.setCanceled(true);
        }
    }
}
