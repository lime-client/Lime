package lime.scripting;

import jdk.nashorn.api.scripting.AbstractJSObject;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.scripting.api.events.EventMotion;

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
                stringAbstractJSObjectEntry.getValue().call(this, (Object) null);
            }
        }
    }

    @Override
    public void onDisable() {
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onDisable")) {
                stringAbstractJSObjectEntry.getValue().call(this, (Object) null);
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate e)
    {
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onUpdate")) {
                stringAbstractJSObjectEntry.getValue().call(this, (Object) null);
            }
        }
    }

    @EventTarget
    public void onMotion(lime.core.events.impl.EventMotion e)
    {
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onMotion")) {
                stringAbstractJSObjectEntry.getValue().call(this, new EventMotion(e));
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e)
    {
        for (Map.Entry<String, AbstractJSObject> stringAbstractJSObjectEntry : events.entrySet()) {
            if(stringAbstractJSObjectEntry.getKey().equals("onPacket")) {
                stringAbstractJSObjectEntry.getValue().call(this, new lime.scripting.api.events.EventPacket(e));
            }
        }
    }
}
