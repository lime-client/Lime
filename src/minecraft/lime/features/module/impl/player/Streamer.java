package lime.features.module.impl.player;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.TextValue;

@ModuleData(name = "Streamer", category = Category.PLAYER)
public class Streamer extends Module {

    public final TextValue playerName = new TextValue("Name", this, "slt");
}
