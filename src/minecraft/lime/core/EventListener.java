package lime.core;

import lime.core.events.EventBus;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventKey;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.file.File;
import lime.features.module.Module;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C01PacketChatMessage;

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
        if(autoSave.hasReached(30 * 1000)) {
            new File().saveModules();
            autoSave.reset();
        }
    }
}
