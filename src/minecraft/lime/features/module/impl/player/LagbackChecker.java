package lime.features.module.impl.player;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.movement.Flight;
import lime.features.module.impl.movement.LongJump;
import lime.features.module.impl.movement.Speed;
import lime.ui.notifications.Notification;
import lime.utils.other.Timer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class LagbackChecker extends Module {

    public LagbackChecker() {
        super("Lagback Checker", Category.PLAYER);
    }

    private final Timer timer = new Timer();

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer != null && mc.theWorld != null) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();

            double diffX  = mc.thePlayer.posX - packet.getX();
            double diffY = mc.thePlayer.posY - packet.getY();
            double diffZ = mc.thePlayer.posZ - packet.getZ();
            Module[] modules = {Lime.getInstance().getModuleManager().getModuleC(Flight.class), Lime.getInstance().getModuleManager().getModuleC(Speed.class), Lime.getInstance().getModuleManager().getModuleC(LongJump.class)};
            boolean toggled = false;

            for (Module module : modules) {
                if(module.isToggled()) {
                    toggled = true;
                    break;
                }
            }

            if(diffX + diffZ < 100 && timer.hasReached(1500) && toggled) {
                Lime.getInstance().getNotificationManager().addNotification("Lagback", "Seems like you got rollback, disabled all movements modules", Notification.Type.WARNING);
                for (Module module : modules) {
                    module.disableModule();
                }
                timer.reset();
            }
        }
    }
}
