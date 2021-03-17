package lime.cgui.component.button;

import lime.Lime;
import lime.cgui.component.Component;
import lime.settings.Setting;
import lime.utils.render.Util2D;

import java.awt.*;

public class Combo extends Component {
    public Combo(Setting set){
        super(set);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton){
        boolean isHover = hover(x + (width / 2 ) + 5, y + 60 + rendered, mouseX, mouseY, 110, 10);
        if(isHover && mouseButton == 0){
            set.setValString(set.getComboNextOption());
        }
    }
    int x, y, width, height;
    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height =height;
        Util2D.drawRoundedRect(x + (width / 2 ) + 5, y + 60 + rendered, 110, 10, 3, new Color(10, 10, 10).getRGB(), false);
        Lime.fontManager.comfortaa_hud.drawString(set.getName() + ": " + set.getValString(), x + (width / 2 ) + 5 + 2, y + 63 + rendered, -1);
    }
}
