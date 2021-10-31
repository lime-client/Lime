package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;

public class Animations extends Module {

    public Animations() {
        super("Animations", Category.VISUALS);
    }

    public final EnumProperty mode = new EnumProperty("Mode", this, "Swing", "Vanilla", "Swang", "Swank", "Swing", "Swong", "Swaing", "Spin", "Astolfo", "Nuf", "oHare", "Lucky", "Exhibition", "Exhibition2");
    public final NumberProperty swingSlowdown = new NumberProperty("Swing Slowdown", this, 0.1, 3, 1, 0.1);
    public final NumberProperty x = new NumberProperty("X", this, -1, 1, 0, 0.05);
    public final NumberProperty y = new NumberProperty("Y", this, -1, 1, 0, 0.05);
    public final NumberProperty z = new NumberProperty("Z", this, -1, 1, 0, 0.05);
}
