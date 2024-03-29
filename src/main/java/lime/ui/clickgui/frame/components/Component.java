package lime.ui.clickgui.frame.components;

import lime.features.setting.Setting;

public abstract class Component {
    protected int x, y, width, height;
    protected Setting setting;
    public Component(int x, int y, int width, int height, Setting setting) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setting = setting;
    }

    public abstract void drawComponent(int mouseX, int mouseY);
    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);
    public abstract void mouseReleased(int mouseX, int mouseY, int state);
    public abstract void onGuiClosed();

    public abstract void onKeyTyped(char typedChar, int key);

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
