package lime.features.setting;

import lime.features.module.Module;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsManager {
    private final HashMap<Module, ArrayList<SettingValue>> settings = new HashMap<>();

    public HashMap<Module, ArrayList<SettingValue>> getHashMapSettings() {
        return this.settings;
    }

    public SettingValue getSetting(String name, Module module) {
        for(SettingValue setting : getSettingsFromModule(module)) {
            if(name.equalsIgnoreCase(setting.getSettingName())) return setting;
        }

        return null;
    }

    public ArrayList<SettingValue> getSettings() {
        ArrayList<SettingValue> settings = new ArrayList<>();
        for(ArrayList<SettingValue> sets : this.settings.values()) {
            for(SettingValue set : sets) {
                settings.add(set);
            }
        }

        return settings;
    }

    public ArrayList<SettingValue> getSettingsFromModule(Module module) {
        return settings.get(module);
    }

    public void putSetting(SettingValue setting, Module parentModule) {
        this.settings.putIfAbsent(parentModule, new ArrayList<>());
        this.settings.get(parentModule).add(setting);
    }
}
