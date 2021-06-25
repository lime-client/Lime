package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;

@ModuleData(name = "Animations", category = Category.RENDER)
public class Animations extends Module {
    private enum Mode {
        VANILLA, SWANG, SWANK, SWING, SWONG, SWAING
    }

    public final EnumValue mode = new EnumValue("Mode", this, Mode.SWING);
}
