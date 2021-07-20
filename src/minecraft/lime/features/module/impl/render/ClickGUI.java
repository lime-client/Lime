package lime.features.module.impl.render;

import lime.core.Lime;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;

@ModuleData(name = "ClickGUI", key = 54, category = Category.RENDER)
public class ClickGUI extends Module {

    private enum Mode {
        FRAME, FRAMENEW
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.FRAME);

    @Override
    public void onEnable() {
        if(mode.is("frame"))
            mc.displayGuiScreen(Lime.getInstance().getClickGUI());
        if(mode.is("framenew"))
            mc.displayGuiScreen(Lime.getInstance().getClickGUI2());
        this.toggle();
    }
}
