package lime.cgui.settings.impl;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.cgui.settings.Value;
import lime.module.Module;

public class BooleanValue extends Value {
    public String name;
    public boolean bool;
    public Module parentMod;
    public BooleanValue(String name, Module mod, boolean bool){
        this.name = name;
        this.bool = bool;
        this.parentMod = mod;
        Lime.setmgr.rSetting(new Setting(name, mod, bool));
    }

    public boolean getValue(){
        return Lime.setmgr.getSettingByNameAndMod(name, parentMod).getValBoolean();
    }

    public void setValue(boolean b){
        Lime.setmgr.getSettingByNameAndMod(name, parentMod).setValBoolean(b);
    }

}
