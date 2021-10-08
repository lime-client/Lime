package lime.features.module.impl.render;

import lime.core.Lime;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumValue;
import org.lwjgl.opengl.Display;

public class ClickGUI extends Module {

    public ClickGUI() {
        super("ClickGUI", 54, Category.VISUALS);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "frame", "frame", "framenew");

    public static int guiScale;

    @Override
    public void onEnable() {
        guiScale = mc.gameSettings.guiScale;
        if(Display.getWidth() < (10 + Category.values().length * 125) + 125) {
            mc.gameSettings.guiScale = 1;
        }
        if(mode.is("frame"))
            mc.displayGuiScreen(Lime.getInstance().getClickGUI());
        if(mode.is("framenew"))
            mc.displayGuiScreen(Lime.getInstance().getClickGUI2());
        this.toggle();
    }
}
