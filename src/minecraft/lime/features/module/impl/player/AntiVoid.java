package lime.features.module.impl.player;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.movement.Flight;
import lime.features.module.impl.movement.Spider;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.ui.gui.ProcessBar;
import lime.utils.movement.MovementUtils;
import lime.utils.other.PlayerUtils;
import lime.utils.other.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class AntiVoid extends Module {

    public AntiVoid() {
        super("Anti Void", Category.PLAYER);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Motion", "Motion", "Funcraft", "Hypixel", "Verus");
    private final NumberProperty pullBack = new NumberProperty("Pullback", this, 500, 3000, 500, 500);
    private ProcessBar processBar;

    private final Timer timer = new Timer(), timer1 = new Timer();
    private boolean waitingForPacket;
    private boolean received, waiting;


    @Override
    public void onEnable() {
        ScaledResolution sr = new ScaledResolution(mc);
        processBar = new ProcessBar((sr.getScaledWidth() / 2) - 25, (sr.getScaledHeight() / 2) + 20, 500);
        waitingForPacket = false;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(!mc.thePlayer.onGround && mc.thePlayer.isEntityAlive() && ((mc.thePlayer.fallDistance > 3 && !mode.is("verus")) || (mc.thePlayer.fallDistance > 4 && !Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled() && (!mc.thePlayer.isCollidedHorizontally || Lime.getInstance().getModuleManager().getModuleC(Spider.class).isToggled()))) && MovementUtils.isVoidUnder()) {
            if(mode.is("motion")) {
                if(timer.hasReached((int) pullBack.getCurrent())) {
                    timer.reset();
                    mc.thePlayer.motionY = 1;
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
            if(mode.is("verus") && e.isPre() && mc.thePlayer.fallDistance > 4) {
                if(!waiting) {
                    e.setGround(true);
                    MovementUtils.vClip(0.42);
                    received = false;
                    waiting = true;
                }

                if(received) {
                    mc.thePlayer.motionY = 1;
                    if(timer.hasReached(1250)) {
                        waiting = false;
                        mc.thePlayer.fallDistance = 0;
                        timer.reset();
                    }
                }
            }
        } else {
            timer.reset();
            ScaledResolution sr = new ScaledResolution(mc);
            processBar = new ProcessBar((sr.getScaledWidth() / 2) - 25, (sr.getScaledHeight() / 2) + 20, 500);
            waiting = false;
            received = false;
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(mode.is("verus") && mc.thePlayer.fallDistance > 4 && MovementUtils.isVoidUnder() && !timer.hasReached(500)) {
            e.setCanceled(true);
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

        if(e.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
            received = true;
        }
    }
}
