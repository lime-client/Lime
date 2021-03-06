package lime.cgui.cgui2.component.components;

import lime.Lime;
import lime.cgui.cgui2.component.Component;
import lime.cgui.settings.Setting;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class Checkbox extends Component {
    public Checkbox(Setting set){
        super(set);
    }
    int x, y = 0;

    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        Lime.clickgui2.fix();
        Gui.drawRect(x + 255, y + 10 + rendered, x + 265, y + 20 + rendered, new Color(25, 25, 25).getRGB());
        if(set.getValBoolean())
            Util2D.DrawCroix(x + 255, y + 10 + rendered, 10, new Color(150, 20, 20).getRGB());
        Lime.fontManager.comfortaa_hud.drawString(set.getName(), x + 268, y + 12 + rendered, -1);
        super.render(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = hover(x + 255, y + 10 + rendered, mouseX, mouseY, 11, 11);
        if(flag && mouseButton == 0)
            set.setValBoolean(!set.getValBoolean());
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
