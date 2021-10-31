package lime.features.setting.impl;

import lime.features.module.Module;
import lime.features.setting.Setting;

public class TextProperty extends Setting {
    private String text;

    public TextProperty(String name, Module module, String defaultText) {
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
