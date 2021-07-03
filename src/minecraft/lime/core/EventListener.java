package lime.core;

import lime.core.events.EventBus;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventKey;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.file.FileSaver;
import lime.features.module.Module;
import lime.utils.other.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

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

    private final Timer autoSave = new Timer();
    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(Lime.getInstance().getUserCheckThread() == null || !Lime.getInstance().getUserCheckThread().isAlive() || !Lime.getInstance().getUser().getHwid().equalsIgnoreCase(Minecraft.getHardwareID())) {
            System.out.println("Please contact Wykt#0001 with the error code \"9\"");
            Minecraft.getMinecraft().shutdown();
            Lime.getInstance().setUserCheckThread(null);
            Lime.getInstance().setUser(null);
            try {
                Field field = Lime.class.getDeclaredField("instance");
                field.setAccessible(true);
                field.set(Lime.getInstance(), null);
            } catch (Exception ignored) {}
        }
        if(autoSave.hasReached(30 * 1000)) {
            Lime.getInstance().getFileSaver().saveModules("Lime" + java.io.File.separator + "modules.json", "Lime Auto Save");
            autoSave.reset();
        }
    }
}
