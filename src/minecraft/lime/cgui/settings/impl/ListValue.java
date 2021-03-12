package lime.cgui.settings.impl;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.cgui.settings.Value;
import lime.module.Module;

import java.util.ArrayList;
import java.util.Arrays;

public class ListValue extends Value {
    public String name;
    public Module mod;
    public String defaultVal;
    public ArrayList<String> arrayList = new ArrayList<>();
    public ListValue(String name, Module mod, String defaultVal, String... array){
        this.name = name;
        this.mod = mod;
        this.defaultVal = defaultVal;
        this.arrayList.addAll(Arrays.asList(array));
        Lime.setmgr.rSetting(new Setting(name, mod, defaultVal, arrayList));
    }
    public ListValue(String name, Module mod, String defaultVal, ArrayList<String> array){
        this.name = name;
        this.mod = mod;
        this.defaultVal = defaultVal;
        this.arrayList = array;
        Lime.setmgr.rSetting(new Setting(name, mod, defaultVal, arrayList));
    }

    public String getValue(){
        return Lime.setmgr.getSettingByNameAndMod(name, mod).getValString();
    }
    public void setValue(String s){
        Lime.setmgr.getSettingByNameAndMod(name, mod).setValString(s);
    }
}
