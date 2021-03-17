package lime.cgui.component.button;

import lime.Lime;
import lime.cgui.component.Component;
import lime.settings.Setting;
import lime.utils.render.Util2D;

import java.awt.*;

public class ComboBoolean extends Component {
    Setting[] sets;
    int index = 0;
    public ComboBoolean(Setting set, Setting[] sets){
        super(set);
        this.sets = sets;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton){
        boolean isHover = hover(x + (width / 2 ) + 5, y + 60 + rendered, mouseX, mouseY, 110, 10);
        if(isHover && mouseButton == 0){
            if(index + 1 >= sets.length)
                index = 0;
            else
                index++;
        } else if(isHover && mouseButton == 1)
            sets[index].setValBoolean(!sets[index].getValBoolean());
    }
    int x, y, width, height;
    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height =height;
        Util2D.drawRoundedRect(x + (width / 2 ) + 5, y + 60 + rendered, 110, 10, 3, new Color(10, 10, 10).getRGB(), false);
        Lime.fontManager.comfortaa_hud.drawString(set.getName() + ": " + sets[index].getName(), x + (width / 2 ) + 5 + 2, y + 63 + rendered, -1);
        if(sets[index].getValBoolean())
            Util2D.DrawCroix(x + (width / 2 ) + 105, y + 60 + rendered, 10, new Color(25, 25, 25).getRGB());
    }
}
