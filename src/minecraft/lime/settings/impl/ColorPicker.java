package lime.settings.impl;

import lime.Lime;
import lime.settings.Setting;
import lime.settings.Value;
import lime.module.Module;

import java.awt.*;

public class ColorPicker extends Value {
    String name;
    Module module;
    int color;
    public ColorPicker(String name, Module parent, int color){
        this.name = name;
        this.module = parent;
        this.color = color;
        Lime.setmgr.rSetting(new Setting(name, module, color));
    }
    public int getValue(){
        return Lime.setmgr.getSettingByNameAndMod(name, module).getValColor();
    }
    public Color getColorValue() { return new Color(Lime.setmgr.getSettingByNameAndMod(name, module).getValColor());}
    public void setValue(int value){
        Lime.setmgr.getSettingByNameAndMod(name, module).setColor(value);
    }

}
