package lime.features.setting.impl;

import lime.features.module.Module;
import lime.features.setting.SettingValue;

public class EnumValue extends SettingValue {
    private Enum selected;
    private final Enum[] modes;

    public EnumValue(String settingName, Module parentModule, Enum _enum) {
        super(settingName, parentModule);
        this.selected = _enum;
        this.modes = (Enum[]) _enum.getDeclaringClass().getEnumConstants();
    }

    public void setSelected(Enum selected) {
        this.selected = selected;
    }

    public Enum getSelected() {
        return selected;
    }

    public Enum[] getModes() {
        return modes;
    }

    public boolean is(String name) {
        return name.equalsIgnoreCase(selected.name());
    }
}
