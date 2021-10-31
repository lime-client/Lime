package lime.features.setting.impl;

import lime.features.module.Module;
import lime.features.setting.Setting;

public class EnumProperty extends Setting {
    private String selected;
    private final String[] modes;

    public EnumProperty(String settingName, Module parentModule, String _default, String... modes) {
        super(settingName, parentModule);
        this.selected = _default;
        this.modes = modes;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getSelected() {
        return selected;
    }

    public String[] getModes() {
        return modes;
    }

    public boolean is(String name) {
        return name.equalsIgnoreCase(selected);
    }
}
