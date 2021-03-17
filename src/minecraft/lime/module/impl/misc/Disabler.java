package lime.module.impl.misc;

import lime.settings.impl.ListValue;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventPacket;
import lime.module.Module;
import lime.utils.Timer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class Disabler extends Module {

    ListValue mode = new ListValue("Mode", this, "Erisium", "Ghostly-Movement");

    public Disabler(){
        super("Disabler", 0, Category.MISC);
    }
    boolean shouldCancel = false;

    @Override
    public void onEnable() {
        timer.reset();
        shouldCancel = true;
        super.onEnable();
    }

    Timer timer = new Timer();
    @EventTarget
    public void onPacket(EventPacket e){
        switch(mode.getValue()){
            case "Erisium":
                if (mc.theWorld != null && mc.thePlayer != null) {
                    if (e.getPacket() instanceof C0FPacketConfirmTransaction || e.getPacket() instanceof C00PacketKeepAlive || e.getPacket() instanceof S00PacketKeepAlive || e.getPacket() instanceof S32PacketConfirmTransaction) {
                        e.setCancelled(true);
                    }
                    if (e.getPacket() instanceof C00PacketKeepAlive && shouldCancel) {
                        e.setCancelled(true);
                        if (timer.hasReached(6000)) {
                            C00PacketKeepAlive keepAlivePacket = (C00PacketKeepAlive) e.getPacket();

                            shouldCancel = false;
                            mc.thePlayer.sendQueue.addToSendQueue(new C00PacketKeepAlive(keepAlivePacket.getKey()));
                            shouldCancel = true;
                            timer.reset();
                        }
                    }
                    if (e.getPacket() instanceof C0FPacketConfirmTransaction && shouldCancel) {
                        C0FPacketConfirmTransaction transactionPacket = (C0FPacketConfirmTransaction) e.getPacket();

                        e.setCancelled(true);
                        if (timer.hasReached(6000)) {
                            shouldCancel = false;
                            mc.thePlayer.sendQueue.addToSendQueue(new C0FPacketConfirmTransaction(transactionPacket.getWindowId(), transactionPacket.getUid(), transactionPacket.accepted));
                            shouldCancel = true;
                            timer.reset();
                        }
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onMotion(EventMotion e){
        switch(mode.getValue()){
            case "Ghostly-Movement":
                if(e.isPre()){
                    mc.getNetHandler().addToSendQueue(new C0CPacketInput());
                    mc.getNetHandler().addToSendQueue(new C18PacketSpectate(mc.thePlayer.getUniqueID()));
                }
                break;
        }
    }
}
