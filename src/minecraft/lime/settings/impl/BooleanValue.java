package lime.settings.impl;

import lime.Lime;
import lime.settings.Setting;
import lime.settings.Value;
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
    public BooleanValue(String name, Module mod, boolean bool, Setting parentSet){
        this.name = name;
        this.bool = bool;
        this.parentMod = mod;
        Lime.setmgr.rSetting(new Setting(name, mod, bool, parentSet));
    }

    public boolean getValue(){
        return Lime.setmgr.getSettingByNameAndMod(name, parentMod).getValBoolean();
    }

    public void setValue(boolean b){
        Lime.setmgr.getSettingByNameAndMod(name, parentMod).setValBoolean(b);
    }

}
