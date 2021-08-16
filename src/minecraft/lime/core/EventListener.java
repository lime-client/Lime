package lime.core;

import lime.core.events.EventBus;
import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Module;
import lime.scripting.ScriptModule;
import lime.utils.other.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class EventListener {
    public EventListener() {
        EventBus.INSTANCE.register(this);
    }

    @EventTarget
    public void onKey(EventKey e) {
        Lime.getInstance().getModuleManager().getModules().stream().filter(module -> module.getKey() == e.getKey()).forEach(Module::toggle);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C01PacketChatMessage) {
            String message = ((C01PacketChatMessage) e.getPacket()).getMessage();
            if(message.startsWith(".")) {
                Lime.getInstance().getCommandManager().callCommand(message);
                e.setCanceled(true);
            }
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        Lime.getInstance().getNotificationManager().renderNotifications(e);
    }

    private final Timer autoSave = new Timer();
    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(Lime.getInstance().getUserCheckThread() == null || !Lime.getInstance().getUserCheckThread().isAlive()) {
            try {
                Field field = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
                field.setAccessible(true);
                Object unsafe = field.get(null);
                unsafe.getClass().getDeclaredMethod("getByte", long.class).invoke(unsafe, 0);
            } catch (Exception ignored){}
        }
        if(autoSave.hasReached(30 * 1000)) {
            Lime.getInstance().getFileSaver().saveModules("Lime" + java.io.File.separator + "modules.json", "Lime Auto Save", true);
            autoSave.reset();
        }
    }
}
