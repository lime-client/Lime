package lime.features.setting;

import lime.core.Lime;
import lime.features.module.Module;
import lime.features.setting.impl.EnumValue;

public abstract class SettingValue {
    private final String settingName;
    private final Module parentModule;
    private boolean hideSetting;

    // Only if Method
    private String settingNameIf, input;

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

    public <T extends SettingValue> T onlyIf(String settingName, String input) {
        this.settingNameIf = settingName;
        this.input = input;
        return (T) this;
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
