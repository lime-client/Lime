package lime.features.setting.impl;

import lime.features.module.Module;
import lime.features.setting.Setting;

import java.util.Arrays;
import java.util.List;

public class SubOptionProperty extends Setting {
    private final List<Setting> settings;
    private boolean opened;

    public SubOptionProperty(String settingName, Module module, Setting... settings) {
        super(settingName, module);
        this.settings = Arrays.asList(settings);

        this.settings.forEach(setting -> setting.setOwned(true));
        this.opened = false;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isOpened() {
        return opened;
    }
}
