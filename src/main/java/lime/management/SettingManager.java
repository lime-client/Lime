package lime.management;

import lime.features.module.Module;
import lime.features.setting.Setting;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingManager {
    private final HashMap<Module, ArrayList<Setting>> settings = new HashMap<>();

    public HashMap<Module, ArrayList<Setting>> getHashMapSettings() {
        return this.settings;
    }

    public Setting getSetting(String name, Module module) {
        for(Setting setting : getSettingsFromModule(module)) {
            if(name.equalsIgnoreCase(setting.getSettingName())) return setting;
        }

        return null;
    }

    public ArrayList<Setting> getSettings() {
        ArrayList<Setting> settings = new ArrayList<>();
        for(ArrayList<Setting> sets : this.settings.values()) {
            for(Setting set : sets) {
                settings.add(set);
            }
        }

        return settings;
    }

    public ArrayList<Setting> getSettingsFromModule(Module module) {
        return settings.get(module);
    }

    public void putSetting(Setting setting, Module parentModule) {
        this.settings.putIfAbsent(parentModule, new ArrayList<>());
        this.settings.get(parentModule).add(setting);
    }
}
