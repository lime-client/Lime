package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;

@ModuleData(name = "Chams", category = Category.RENDER)
public class Chams extends Module {
    private enum Mode {
        THROUGH, COLORED
    }
    private final EnumValue mode = new EnumValue("Mode", this, Mode.COLORED);
}
