package lime.cgui.component;

import lime.cgui.settings.Setting;

public class Component {
    public Setting set;
    public Component(Setting set){
        this.set = set;
    }
    public void render(int x, int y, int width, int height, int mouseX, int mouseY){

    }
    public void mouseClicked(int mouseX, int mouseY, int mouseButton){

    }
    public boolean hover(double x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    public void mouseReleased(int mouseX, int mouseY, int mouseButton){

    }
}
