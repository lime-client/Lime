package lime.utils.other.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import lime.core.Lime;
import lime.features.module.Module;
import lime.features.setting.Setting;
import lime.features.setting.impl.*;

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
                    for(Setting settingValue : Lime.getInstance().getSettingsManager().getSettingsFromModule(module)) {
                        if(settingValue instanceof SubOptionProperty) continue;
                        jsonWriter.beginObject();
                        jsonWriter.name("name");
                        jsonWriter.value(settingValue.getSettingName());
                        jsonWriter.name("type");
                        jsonWriter.value(this.getSettingType(settingValue).name().toLowerCase());
                        jsonWriter.name("value");
                        if(this.getSettingType(settingValue) == SettingType.ENUM) {
                            jsonWriter.value(((EnumProperty) settingValue).getSelected().toLowerCase());
                        } else if(this.getSettingType(settingValue) == SettingType.SLIDER) {
                            jsonWriter.value(((NumberProperty) settingValue).getCurrent());
                        } else if(this.getSettingType(settingValue) == SettingType.BOOL) {
                            jsonWriter.value(((BooleanProperty) settingValue).isEnabled());
                        } else if(this.getSettingType(settingValue) == SettingType.TEXT) {
                            jsonWriter.value(((TextProperty) settingValue).getText());
                        } else if(this.getSettingType(settingValue) == SettingType.COLOR) {
                            jsonWriter.value(((ColorProperty) settingValue).getColor());
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
                                TextProperty textValue = (TextProperty) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                textValue.setText(setting.getAsJsonObject().get("value").getAsString());
                            }

                            if(type == SettingType.COLOR) {
                                ColorProperty colorValue = (ColorProperty) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                colorValue.setColor(setting.getAsJsonObject().get("value").getAsInt());
                            }

                            // Bool Setting
                            if(type == SettingType.BOOL) {
                                BooleanProperty boolSetting = (BooleanProperty) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                boolSetting.setEnabled(setting.getAsJsonObject().get("value").getAsBoolean());
                            }

                            // Slider Setting
                            if(type == SettingType.SLIDER) {
                                NumberProperty slideSetting = (NumberProperty) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                slideSetting.setCurrentValue(setting.getAsJsonObject().get("value").getAsDouble());
                            }

                            // Enum Setting
                            if(type == SettingType.ENUM) {
                                EnumProperty enumSetting = (EnumProperty) Lime.getInstance().getSettingsManager().getSetting(setting.getAsJsonObject().get("name").getAsString(), m);
                                String selected = null;
                                for(String str : enumSetting.getModes()) {
                                    if(str.equalsIgnoreCase(setting.getAsJsonObject().get("value").getAsString())) selected = str;
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


    public Enum getSettingType(Setting setting) {
        if(setting instanceof EnumProperty)
            return SettingType.ENUM;
        if(setting instanceof NumberProperty)
            return SettingType.SLIDER;
        if(setting instanceof BooleanProperty)
            return SettingType.BOOL;
        if(setting instanceof TextProperty)
            return SettingType.TEXT;
        if(setting instanceof ColorProperty)
            return SettingType.COLOR;

        // wtf?
        return null;
    }

    private enum SettingType {
        ENUM, SLIDER, BOOL, TEXT, COLOR
    }
}
