package lime.features.setting;

import lime.core.Lime;
import lime.features.module.Module;

public abstract class SettingValue {
    private final String settingName;
    private final Module parentModule;
    private boolean hideSetting;

    public SettingValue(String settingName, Module parentModule, boolean hideSetting) {
        this.settingName = settingName;
        this.parentModule = parentModule;
        this.hideSetting = hideSetting;
        Lime.getInstance().getSettingsManager().putSetting(this, parentModule);
    }

    public SettingValue(String settingName, Module parentModule) {
        this(settingName, parentModule, false);
    }

    public String getSettingName() {
        return settingName;
    }

    public Module getParentModule() {
        return parentModule;
    }

    public boolean isHide() {
        return hideSetting;
    }

    public void hideSetting() {
        this.hideSetting = true;
    }

    public void unHideSetting() {
        this.hideSetting = false;
    }
}
