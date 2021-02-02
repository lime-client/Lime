package lime.cgui.component.button;

import lime.Lime;
import lime.cgui.component.Component;
import lime.cgui.settings.Setting;
import lime.utils.render.Util2D;

import java.awt.*;

public class Checkbox extends Component {
    public Checkbox(Setting set){
        super(set);
    }
    int checkboxY = 0;
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton){
        boolean isHover = hover(x + (width / 2 ) + 5, y + 60 + checkboxY, mouseX, mouseY, 10, 10);
        if(isHover && mouseButton == 0){
            set.setValBoolean(!set.getValBoolean());
        }
    }
    int x, y, width, height;
    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        calculateY();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        Util2D.drawRoundedRect(x + (width / 2 ) + 5, y + 60 + checkboxY, x + (width / 2 ) + 15, y + 70 + checkboxY, new Color(50, 50, 50).getRGB(), new Color(25, 25, 25).getRGB());
        if(set.getValBoolean())
            Util2D.DrawCroix(x + (width / 2 ) + 5, y + 60 + checkboxY, 9, new Color(50, 50, 50).getRGB());
        Lime.fontManager.comfortaa_hud.drawString(set.getName(), x + (width / 2 ) + 17, y + 60 + checkboxY + 2, -1);
    }
    public void calculateY(){
        checkboxY = 0;
        for(Component c : Lime.clickgui.components){
            if(c.set.getParentMod() == set.getParentMod()){
                checkboxY = 12 * Lime.clickgui.rendered;
            }
        }

    }
    public boolean hover(int x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
