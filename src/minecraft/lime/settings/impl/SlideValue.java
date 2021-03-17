package lime.settings.impl;

import lime.Lime;
import lime.settings.Setting;
import lime.settings.Value;
import lime.module.Module;

public class SlideValue extends Value {
    public String name;
    public Module mod;
    public double defaultValue;
    public double maxValue;
    public double minValue;
    public SlideValue(String name, Module mod, double defaultValue, double minValue, double maxValue, boolean onlyInt){
        this.name = name;
        this.mod = mod;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
        Lime.setmgr.rSetting(new Setting(name, mod, defaultValue, minValue, maxValue, onlyInt));
    }

    public double getValue(){
        return Lime.setmgr.getSettingByNameAndMod(name, mod).getValDouble();
    }
    public int getIntValue(){
        return (int) Lime.setmgr.getSettingByNameAndMod(name, mod).getValDouble();
    }
    public float getFloatValue(){
        return (float) Lime.setmgr.getSettingByNameAndMod(name, mod).getValDouble();
    }
}
