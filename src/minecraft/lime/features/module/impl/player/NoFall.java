package lime.features.module.impl.player;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.movement.LongJump;
import lime.features.module.impl.movement.Speed;
import lime.features.setting.impl.EnumValue;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {

    public NoFall() {
        super("No Fall", Category.PLAYER);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Vanilla", "Vanilla", "Verus", "Verus2");

    @EventTarget
    public void onUpdate(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(e.isPre()) {
            if(mc.thePlayer.fallDistance > 2.5) {
                if(mode.is("vanilla")) {
                    e.setGround(true);
                    mc.thePlayer.fallDistance = 0;
                } else if(mode.is("verus2")) {
                    if(Lime.getInstance().getModuleManager().getModuleC(LongJump.class).isToggled() && ((EnumValue) Lime.getInstance().getSettingsManager().getSetting("Mode", Lime.getInstance().getModuleManager().getModuleC(LongJump.class))).is("verus_bow")) return;
                    e.setGround(true);
                    mc.thePlayer.motionY = -0.0784000015258789;
                    mc.thePlayer.fallDistance = 0;
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(mode.is("verus")) {
            if(e.getPacket() instanceof C03PacketPlayer && !Lime.getInstance().getModuleManager().getModuleC(Speed.class).isToggled() && !((Speed) Lime.getInstance().getModuleManager().getModuleC(Speed.class)).mode.is("verus2")) {
                C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
                packet.onGround = mc.thePlayer.onGround || (mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.fallDistance > 2.75);
                e.setPacket(packet);
            }
        }
    }
}
