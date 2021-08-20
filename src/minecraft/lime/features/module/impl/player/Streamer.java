package lime.features.module.impl.player;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.TextValue;

public class Streamer extends Module {

    public Streamer() {
        super("Streamer", Category.PLAYER);
    }

    public final TextValue playerName = new TextValue("Name", this, "slt");
}
