package lime.features.setting.impl;

import lime.features.module.Module;
import lime.features.setting.Setting;

public class BooleanProperty extends Setting {
    private boolean enabled;

    public BooleanProperty(String settingName, Module parentModule, boolean enabled) {
        super(settingName, parentModule);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
