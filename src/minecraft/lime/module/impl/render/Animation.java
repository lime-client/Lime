package lime.module.impl.render;

import lime.settings.impl.ListValue;
import lime.settings.impl.SlideValue;
import lime.module.Module;

public class Animation extends Module {
    public ListValue blockAnimation = new ListValue("Animation", this, "1.7", "1.7", "Lime", "Spin");
    public SlideValue swingSlowdown = new SlideValue("Swing Slowdown", this, 1, 1, 10, true);
    public Animation(){
        super("Animation", 0, Category.RENDER);
    }
}
