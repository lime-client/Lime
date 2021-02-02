package lime.module.impl.render;

import lime.Lime;
import lime.cgui.ClickGui;
import lime.cgui.settings.Setting;
import lime.module.Module;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public ClickGUI(){
        super("ClickGUI", Keyboard.KEY_RSHIFT, Category.RENDER);
        Lime.setmgr.rSetting(new Setting("Blur", this, true));
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Lime.clickgui);
        this.toggle();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
