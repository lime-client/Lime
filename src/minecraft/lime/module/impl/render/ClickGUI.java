package lime.module.impl.render;

import lime.Lime;
import lime.cgui.ClickGui;
import lime.cgui.settings.Setting;
import lime.module.Module;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public ClickGUI(){
        super("ClickGUI", Keyboard.KEY_RSHIFT, Category.RENDER);
        Lime.setmgr.rSetting(new Setting("ClickGUI Mode", this, "Lime", new String[]{"Lime", "LimeNew"}));
        Lime.setmgr.rSetting(new Setting("Blur", this, true));
    }

    @Override
    public void onEnable() {
        switch(getSettingByName("ClickGUI Mode").getValString()){
            case "Lime":
                mc.displayGuiScreen(Lime.clickgui);
                break;
            case "LimeNew":
                mc.displayGuiScreen(Lime.clickgui2);
                break;
        }
        this.toggle();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
