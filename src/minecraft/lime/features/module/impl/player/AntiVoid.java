package lime.features.module.impl.player;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.movement.Flight;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class AntiVoid extends Module {

    public AntiVoid() {
        super("Anti Void", Category.PLAYER);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Motion", "Motion", "Blink", "Funcraft", "Hypixel");
    private final SlideValue pullBack = new SlideValue("Pullback", this, 500, 3000, 500, 500);

    private final Timer timer = new Timer(), timer1 = new Timer();
    private boolean waitingForPacket;

    @Override
    public void onEnable() {
        waitingForPacket = false;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(!mc.thePlayer.onGround && mc.thePlayer.isEntityAlive() && MovementUtils.isVoidUnder()) {
            if(mode.is("motion")) {
                if(timer.hasReached((int) pullBack.getCurrent())) {
                    timer.reset();
                    mc.thePlayer.motionY = 1;
                }
            }
            if(mode.is("blink")) {
                if(mc.thePlayer.fallDistance > 1 && !Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) {
                    e.setCanceled(true);
                    if(timer1.hasReached(500)) {
                        e.setCanceled(false);
                        timer1.reset();
                    }
                }
            }
            if(mode.is("funcraft")) {
                if(mc.thePlayer.fallDistance > 3 && timer1.hasReached(1000) && !Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled() && !waitingForPacket) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ, false));
                    waitingForPacket = true;
                    timer1.reset();
                }
            }
            if(mode.is("hypixel") && timer.hasReached((int) pullBack.getCurrent())) {
                if(mc.thePlayer.fallDistance > 3) {
                    e.setX(-9999);
                    e.setY(-9999);
                    e.setZ(-9999);
                    mc.thePlayer.fallDistance = 0;
                }
            }
        } else {
            timer.reset();
        }
    }
    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            timer1.reset();
            timer.reset();
            if(waitingForPacket && mode.is("funcraft")) {
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
                packet.setYaw(0);
                packet.setPitch(0);
                waitingForPacket = false;
                timer.reset();
            }
        }
    }
}
