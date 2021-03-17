package lime.cgui.component.button;

import lime.Lime;
import lime.cgui.component.Component;
import lime.settings.Setting;
import lime.utils.render.Util2D;

import java.awt.*;

public class Checkbox extends Component {
    public Checkbox(Setting set){
        super(set);
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton){
        boolean isHover = hover(x + (width / 2 ) + 5, y + 60 + rendered, mouseX, mouseY, 10, 10);
        if(isHover && mouseButton == 0){
            set.setValBoolean(!set.getValBoolean());
        }
    }
    int x, y, width, height;
    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        Util2D.drawRoundedRect(x + (width / 2 ) + 5, y + 60 + rendered, 9, 10, 3, new Color(10, 10, 10).getRGB(), false);
        if(set.getValBoolean())
            Util2D.DrawCroix(x + (width / 2 ) + 6, y + 61 + rendered, 9, new Color(50, 50, 50).getRGB());
        Lime.fontManager.comfortaa_hud.drawString(set.getName(), x + (width / 2 ) + 19, y + 60 + rendered + 3, -1);
    }
    public boolean hover(int x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
