package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.ui.clickgui.test.ClickGUI;

@ModuleData(name = "ClickGUI Test", category = Category.RENDER)
public class ClickGUITest extends Module {
    @Override
    public void onEnable() {
        mc.displayGuiScreen(new ClickGUI());
        this.toggle();
    }
}
