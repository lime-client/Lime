package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventScoreboard;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;

@ModuleData(name = "No Scoreboard", category = Category.RENDER)
public class NoScoreboard extends Module {
    @EventTarget
    public void onScoreboard(EventScoreboard e) {
        e.setCanceled(true);
    }
}
