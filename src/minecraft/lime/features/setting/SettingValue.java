package lime.features.setting;

import lime.core.Lime;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;

public abstract class SettingValue {
    private final String settingName;
    private final Module parentModule;
    private boolean hideSetting;

    // Only if Method
    private String settingNameIf, type;
    private String[] input;

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

    public <T extends SettingValue> T onlyIf(String settingName, String type, String... input) {
        this.settingNameIf = settingName;
        this.input = input;
        this.type = type;
        return (T) this;
    }

    public void constantCheck() {
        if(settingNameIf != null && input != null && type != null) {
            switch(type.toLowerCase()) {
                case "enum":
                    boolean shouldHide = true;
                    for (String s : input) {
                        if(((EnumValue) Lime.getInstance().getSettingsManager().getSetting(settingNameIf, parentModule)).is(s)) {
                            shouldHide = false;
                        }
                    }

                    if(shouldHide)
                        hideSetting();
                    else
                        unHideSetting();
                    break;
                case "bool":
                    if(!((BoolValue) Lime.getInstance().getSettingsManager().getSetting(settingNameIf, parentModule)).isEnabled() && Boolean.parseBoolean(input[0])) {
                        hideSetting();
                    } else {
                        unHideSetting();
                    }
                    break;
            }
        }
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
