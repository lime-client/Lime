package lime.cguinew.components.buttons;

import lime.Lime;
import lime.cguinew.components.Component;
import lime.settings.Setting;
import lime.utils.render.Util2D;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class Checkbox extends Component {
    public Checkbox(Setting set){
        super(set);
    }
    int x, y, height;

    @Override
    public void draw(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.height = height;
        Util2D.drawRoundedRect(x, y, 10, 10, 3, new Color(34, 32, 32, 255).getRGB(), false);
        Lime.fontManager.roboto_sense.drawString(this.set.getName(), x + 15, y + 3, -1);
        if(set.getValBoolean())
            Util2D.drawImage(new ResourceLocation("lime/cgui/checkmark.png"), x + 2, y + 2, 8, 8);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = hover(x, y, mouseX, mouseY, 10, 12);
        if(flag && mouseButton == 0 && mouseY > y && mouseY < y + height)
            this.set.setValBoolean(!this.set.getValBoolean());
    }
}
