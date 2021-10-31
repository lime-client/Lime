package lime.features.module.impl.render;

import lime.core.Lime;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumProperty;
import org.lwjgl.opengl.Display;

public class ClickGUI extends Module {

    public ClickGUI() {
        super("ClickGUI", 54, Category.VISUALS);
    }

    //private final EnumProperty mode = new EnumProperty("Mode", this, "frame", "frame", "framenew");

    public static int guiScale;

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Lime.getInstance().getClickGUI2());
        this.toggle();
    }
}
