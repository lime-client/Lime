package lime.settings.impl;

import lime.Lime;
import lime.settings.Setting;
import lime.settings.Value;
import lime.module.Module;

public class ComboBooleanValue extends Value {
    String name;
    Module mod;
    public ComboBooleanValue(String name, Module parent){
        this.name = name;
        this.mod = parent;
        Lime.setmgr.rSetting(new Setting(name, parent));
    }
    public Setting getSet(){
        return Lime.setmgr.getSettingByNameAndMod(name, mod);
    }
}
