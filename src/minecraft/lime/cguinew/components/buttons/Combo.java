package lime.cguinew.components.buttons;

import lime.Lime;
import lime.cguinew.components.Component;
import lime.settings.Setting;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Combo extends Component {
    public Combo(Setting set){
        super(set);
    }
    int x, y, height;
    boolean opened = false;

    @Override
    public void draw(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.height = height;
        Util2D.drawRoundedRect(x, y, 100, 20, 5, new Color(34, 32, 32).getRGB(), false);
        GlStateManager.color(1, 1, 1);
        Gui.drawRect(x + 4, y + 4, x + 99, y + 19, new Color(24, 22, 22).getRGB());
        Lime.fontManager.roboto_sense.drawCenteredString(set.getValString(), x + 50, y + 8, -1);
        if(opened){
            this.rendered = (set.getOptions().size() - 1) * 24;
            int i = 1;
            for(String a : set.getOptions()){
                if(a.equalsIgnoreCase(set.getValString())) continue;
                Lime.fontManager.roboto_sense.drawString(a, x + 3, y + (i * 24), -1);
                i++;
            }
        } else
            this.rendered = 0;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = hover(x, y, mouseX, mouseY, 100, 20);
        if(flag && mouseButton == 0 && mouseY > y && mouseY < y + height){
            opened = !opened;
        } else if(!flag && mouseButton == 0 && !hover(x, y + 20, mouseX, mouseY, 100, this.rendered)){
            opened = false;
        }
    }
}
