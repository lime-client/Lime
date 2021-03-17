package lime.cguinew.components;

import lime.settings.Setting;

import java.io.IOException;

public class Component {
    public Setting set;
    public int rendered = 0;
    public Component(Setting set){
        this.set = set;
    }

    public void draw(int x, int y, int width, int height, int mouseX, int mouseY){

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton){

    }

    public void mouseReleased(int mouseX, int mouseY, int state){

    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {

    }

    public boolean hover(int x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
