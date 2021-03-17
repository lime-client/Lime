package lime.module.impl.render;

import lime.events.EventTarget;
import lime.events.impl.EventScoreboard;
import lime.module.Module;

public class NoScoreboard extends Module {
    public NoScoreboard(){
        super("NoScoreboard", 0, Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onScore(EventScoreboard e){
        e.setCancelled(true);
    }
}
