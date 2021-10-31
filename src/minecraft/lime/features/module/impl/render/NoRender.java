package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;

public class NoRender extends Module {
    public NoRender() {
        super("No Render", Category.VISUALS);
    }

    public final BooleanProperty tnt = new BooleanProperty("TNT", this, true);
    public final BooleanProperty enchantmentTable = new BooleanProperty("Enchantment Table", this, true);
}
