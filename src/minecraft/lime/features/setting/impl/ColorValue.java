package lime.features.setting.impl;

import lime.features.module.Module;
import lime.features.setting.SettingValue;

public class ColorValue extends SettingValue {

    private int color;

    public ColorValue(String settingName, Module parentModule, int defaultColor) {
        super(settingName, parentModule);
        this.color = defaultColor;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
