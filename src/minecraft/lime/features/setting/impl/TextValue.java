package lime.features.setting.impl;

import lime.features.module.Module;
import lime.features.setting.SettingValue;

public class TextValue extends SettingValue {
    private String text;

    public TextValue(String name, Module module, String defaultText) {
        super(name, module);
        this.text = defaultText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
