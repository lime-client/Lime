package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;

public class NoRender extends Module {
    public NoRender() {
        super("No Render", Category.RENDER);
    }

    public final BoolValue tnt = new BoolValue("TNT", this, true);
    public final BoolValue enchantmentTable = new BoolValue("Enchantment Table", this, true);
}
