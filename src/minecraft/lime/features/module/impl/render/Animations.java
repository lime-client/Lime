package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;

public class Animations extends Module {

    public Animations() {
        super("Animations", Category.VISUALS);
    }

    public final EnumValue mode = new EnumValue("Mode", this, "Swing", "Vanilla", "Swang", "Swank", "Swing", "Swong", "Swaing", "Spin", "Astolfo", "Nuf", "oHare", "Lucky", "Exhibition", "Exhibition2");
    public final SlideValue swingSlowdown = new SlideValue("Swing Slowdown", this, 0.1, 3, 1, 0.1);
    public final SlideValue x = new SlideValue("X", this, -1, 1, 0, 0.05);
    public final SlideValue y = new SlideValue("Y", this, -1, 1, 0, 0.05);
    public final SlideValue z = new SlideValue("Z", this, -1, 1, 0, 0.05);
}
