package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;

@ModuleData(name = "Animations", category = Category.RENDER)
public class Animations extends Module {
    private enum Mode {
        VANILLA, SWANG, SWANK, SWING, SWONG, SWAING, SPIN, ASTOLFO, NUF, OHARE, LUCKY, EXHIBITION, EXHIBITION2
    }

    public final EnumValue mode = new EnumValue("Mode", this, Mode.SWING);
    public final SlideValue swingSlowdown = new SlideValue("Swing Slowdown", this, 0.1, 3, 1, 0.1);
}
