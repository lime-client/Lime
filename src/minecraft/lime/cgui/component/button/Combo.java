package lime.cgui.component.button;

import lime.Lime;
import lime.cgui.component.Component;
import lime.cgui.settings.Setting;
import lime.utils.render.Util2D;

import java.awt.*;

public class Combo extends Component {
    public Combo(Setting set){
        super(set);
    }
    int comboY = 0;

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton){
        boolean isHover = hover(x + (width / 2 ) + 5, y + 60 + comboY, mouseX, mouseY, 110, 10);
        if(isHover && mouseButton == 0){
            set.setValString(set.getComboNextOption());
        }
    }
    public void calculateY(){
        comboY = 12 * Lime.clickgui.rendered;
    }
    int x, y, width, height;
    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height =height;
        calculateY();
        Util2D.drawRoundedRect(x + (width / 2 ) + 5, y + 60 + comboY, x + (width / 2 ) + 115, y + 70 + comboY, new Color(50, 50, 50).getRGB(), new Color(25, 25, 25).getRGB());
        Lime.fontManager.comfortaa_hud.drawString(set.getName() + ": " + set.getValString(), x + (width / 2 ) + 5 + 2, y + 62 + comboY, -1);
    }
}
