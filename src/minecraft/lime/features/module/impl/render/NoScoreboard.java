package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventScoreboard;
import lime.features.module.Category;
import lime.features.module.Module;

public class NoScoreboard extends Module {

    public NoScoreboard() {
        super("No Scoreboard", Category.RENDER);
    }

    @EventTarget
    public void onScoreboard(EventScoreboard e) {
        e.setCanceled(true);
    }
}
