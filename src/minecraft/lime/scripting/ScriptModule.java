package lime.scripting;

import jdk.nashorn.api.scripting.AbstractJSObject;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.scripting.api.events.EventMotion;
import lime.utils.other.ChatUtils;

import java.util.HashMap;
import java.util.Map;

public class ScriptModule extends Module {
    private final HashMap<String, AbstractJSObject> events;

    public ScriptModule(String name, int key, Category category)
    {
        super(name, key, category);
        this.events = new HashMap<>();
    }

    public ScriptModule(String name, Category category)
    {
        this(name, -1, category);
    }

    public HashMap<String, AbstractJSObject> getEvents() {
        return events;
    }

    @Override
    public void onEnable() {
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onEnable")) {
                try {
                    stringAbstractJSObjectEntry.getValue().call(this, (Object) null);
                } catch (Exception exception) {
                    ChatUtils.sendMessage("Error while executing onEnable on the Script " + getName() + ": " + exception.getMessage());
                    this.toggle();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onDisable")) {
                try {
                    stringAbstractJSObjectEntry.getValue().call(this, (Object) null);
                } catch (Exception exception) {
                    ChatUtils.sendMessage("Error while executing onDisable on the Script " + getName() + ": " + exception.getMessage());
                    this.toggle();
                }
            }
        }
    }

    @EventTarget
    public void on2D(Event2D e)
    {
        if(!isToggled()) return;
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("on2D")) {
                try {
                    stringAbstractJSObjectEntry.getValue().call(this, new lime.scripting.api.events.Event2D(e));
                } catch (Exception exception) {
                    ChatUtils.sendMessage("Error while executing Event2D on the Script " + getName() + ": " + exception.getMessage());
                    this.toggle();
                }
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate e)
    {
        if(!isToggled()) return;
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onUpdate")) {
                try {
                    stringAbstractJSObjectEntry.getValue().call(this, (Object) null);
                } catch (Exception exception) {
                    ChatUtils.sendMessage("Error while executing EventUpdate on the Script " + getName() + ": " + exception.getMessage());
                    this.toggle();
                }
            }
        }
    }

    @EventTarget
    public void onMotion(lime.core.events.impl.EventMotion e)
    {
        if(!isToggled()) return;
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onMotion")) {
                try {
                    stringAbstractJSObjectEntry.getValue().call(this, new EventMotion(e));
                } catch (Exception exception) {
                    ChatUtils.sendMessage("Error while executing EventMotion on the Script " + getName() + ": " + exception.getMessage());
                    this.toggle();
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e)
    {
        if(!isToggled()) return;
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onPacket")) {
                try {
                    stringAbstractJSObjectEntry.getValue().call(this, new lime.scripting.api.events.EventPacket(e));
                } catch (Exception exception) {
                    ChatUtils.sendMessage("Error while executing EventPacket on the Script " + getName() + ": " + exception.getMessage());
                    this.toggle();
                }
            }
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(!isToggled()) return;
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onMove")) {
                try {
                    stringAbstractJSObjectEntry.getValue().call(this, new lime.scripting.api.events.EventMove(e));
                } catch (Exception exception) {
                    ChatUtils.sendMessage("Error while executing EventMove on the Script " + getName() + ": " + exception.getMessage());
                    this.toggle();
                }
            }
        }
    }
}
