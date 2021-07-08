package lime.features.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import lime.core.Lime;
import lime.features.module.Module;
import lime.features.setting.SettingValue;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.features.setting.impl.TextValue;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public class FileSaver {
    public void saveModules(String path, String author, boolean keyBinds) {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(path));
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            // Config Informations
            jsonWriter.name("informations");
            jsonWriter.beginArray();
            jsonWriter.beginObject();
            jsonWriter.name("author");
            jsonWriter.value(author);
            jsonWriter.name("time");
            jsonWriter.value(Instant.now().getEpochSecond());
            jsonWriter.endObject();
            jsonWriter.endArray();

            jsonWriter.name("modules");
            jsonWriter.beginArray();
            for(Module module : Lime.getInstance().getModuleManager().getModules()) {
                jsonWriter.beginObject();
                jsonWriter.name("moduleName");
                jsonWriter.value(module.getName());
                if(keyBinds) {
                    jsonWriter.name("key");
                    jsonWriter.value(module.getKey());
                }
                jsonWriter.name("toggled");
                jsonWriter.value(module.isToggled());
                if(Lime.getInstance().getSettingsManager().getSettingsFromModule(module) != null && !Lime.getInstance().getSettingsManager().getSettingsFromModule(module).isEmpty()) {
                    jsonWriter.name("settings");
                    jsonWriter.beginArray();
                    for(SettingValue settingValue : Lime.getInstance().getSettingsManager().getSettingsFromModule(module)) {
                        jsonWriter.beginObject();
                        jsonWriter.name("name");
                        jsonWriter.value(settingValue.getSettingName());
                        jsonWriter.name("type");
                        jsonWriter.value(this.getSettingType(settingValue).name().toLowerCase());
                        jsonWriter.name("value");
                        if(this.getSettingType(settingValue) == SettingType.ENUM) {
                            jsonWriter.value(((EnumValue) settingValue).getSelected().name().toLowerCase());
                        } else if(this.getSettingType(settingValue) == SettingType.SLIDER) {
                            jsonWriter.value(((SlideValue) settingValue).getCurrent());
                        } else if(this.getSettingType(settingValue) == SettingType.BOOL) {
                            jsonWriter.value(((BoolValue) settingValue).isEnabled());
                        } else if(this.getSettingType(settingValue) == SettingType.TEXT) {
                            jsonWriter.value(((TextValue) settingValue).getText());
                        }
                        jsonWriter.endObject();
                    }
                    jsonWriter.endArray();
                }
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();

            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean applyJson(String path, boolean keyBinds) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new FileReader(path));
            JsonArray modules = jsonElement.getAsJsonObject().getAsJsonArray("modules");
            for (JsonElement module : modules) {
                try {
                    Module m = Lime.getInstance().getModuleManager().getModule(module.getAsJsonObject().get("moduleName").getAsString());
                    if(m == null) continue;
                    if(module.getAsJsonObject().get("toggled").getAsBoolean())
                        m.enableModule();
                    else
                        m.disableModule();
                    if(keyBinds) {
                        m.setKey(module.getAsJsonObject().get("key").getAsInt());
                    }
                    if(module.getAsJsonObject().get("settings") == null) continue;
                    JsonArray settings = module.getAsJsonObject().get("settings").getAsJsonArray();

                    for(JsonElement setting : settings) {
                        try {
                            Enum type = SettingType.valueOf(setting.getAsJsonObject().get("type").getAsString().toUpperCase());

                            if(type == SettingType.TEXT) {
                                TextValue textValue = (TextValue) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                textValue.setText(setting.getAsJsonObject().get("value").getAsString());
                            }

                            // Bool Setting
                            if(type == SettingType.BOOL) {
                                BoolValue boolSetting = (BoolValue) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                boolSetting.setEnabled(setting.getAsJsonObject().get("value").getAsBoolean());
                            }

                            // Slider Setting
                            if(type == SettingType.SLIDER) {
                                SlideValue slideSetting = (SlideValue) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                slideSetting.setCurrentValue(setting.getAsJsonObject().get("value").getAsDouble());
                            }

                            // Enum Setting
                            if(type == SettingType.ENUM) {
                                EnumValue enumSetting = (EnumValue) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                Enum selected = null;
                                for(Enum _enum : enumSetting.getModes()) {
                                    if(_enum.name().equalsIgnoreCase(setting.getAsJsonObject().get("value").getAsString())) selected = _enum;
                                }
                                if(selected == null) continue;
                                enumSetting.setSelected(selected);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public Enum getSettingType(SettingValue setting) {
        if(setting instanceof EnumValue)
            return SettingType.ENUM;
        if(setting instanceof SlideValue)
            return SettingType.SLIDER;
        if(setting instanceof BoolValue)
            return SettingType.BOOL;
        if(setting instanceof TextValue)
            return SettingType.TEXT;

        // wtf?
        return null;
    }

    private enum SettingType {
        ENUM, SLIDER, BOOL, TEXT
    }
}
