package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.ui.clickgui.frame2.ClickGUI;

@ModuleData(name = "ClickGUI2", category = Category.RENDER)
public class ClickGUI2 extends Module {
    @Override
    public void onEnable() {
        this.toggle();
        mc.displayGuiScreen(new ClickGUI());
    }
}
