package lime.features.module.impl.player;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.TextProperty;

public class Streamer extends Module {

    public Streamer() {
        super("Streamer", Category.PLAYER);
    }

    public final TextProperty playerName = new TextProperty("Name", this, "slt");
}
