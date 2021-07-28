package lime.features.module.impl.render;

import lime.core.Lime;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.ui.clickgui.frame.components.FrameCategory;
import org.lwjgl.opengl.Display;

@ModuleData(name = "ClickGUI", key = 54, category = Category.RENDER)
public class ClickGUI extends Module {

    private enum Mode {
        FRAME, FRAMENEW
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
