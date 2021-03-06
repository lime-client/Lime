package lime.cgui.cgui2.component.components;

import lime.Lime;
import lime.cgui.cgui2.component.Component;
import lime.cgui.settings.Setting;
import lime.utils.render.RainbowUtil;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static lime.module.impl.render.OldHUD.fix;

public class Combo extends Component {
    public boolean opened = false;
    int x = 0, y = 0;
    public Combo(Setting set){
        super(set);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = hover(x + 255, y + rendered, mouseX, mouseY, 100, 22);
        if(flag && mouseButton == 0) opened = !opened;
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;

        fix();
        Util2D.drawRoundedRect(x + 255, y + rendered, x + 265 + 100, y + 20 + rendered + (opened ? set.getOptions().size() * 24 : 0), new Color(200,0, 0).getRGB(), new Color(200,0, 0).getRGB());
        Util2D.drawRoundedRect(x + 256, y + rendered + 1, x + 265 + 100 - 1, y + 19 + rendered + (opened ? set.getOptions().size() * 24 : 0), RainbowUtil.blend2colors(new Color(150, 0, 0), new Color(250, 0, 0), 10).getRGB(), RainbowUtil.blend2colors(new Color(150, 0, 0), new Color(250, 0, 0), 10).getRGB());
        Gui.drawRect(x + 265, y + rendered, x + 265 + Lime.fontManager.comfortaa_hud_sense.getStringWidth(set.getName()) + 10, y + 5 + rendered, new Color(20, 20, 20).getRGB());
        if(!opened)
            Util2D.drawImage(new ResourceLocation("lime/collapse-down.png"), x + 265 + Lime.fontManager.comfortaa_hud_sense.getStringWidth(set.getName()), y + rendered - 3, 10, 10);
        else
            Util2D.drawImage(new ResourceLocation("lime/collapse-up.png"), x + 265 + Lime.fontManager.comfortaa_hud_sense.getStringWidth(set.getName()), y + rendered - 3, 10, 10);
        fix();
        Lime.fontManager.comfortaa_hud_sense.drawString(set.getName(), x + 265, y + rendered, -1);
        Lime.fontManager.comfortaa_hud_sense.drawString(set.getValString(), x + 260, y + rendered + 10, -1);
        int yCount = 0;
        if(opened){

            for(String str : set.getOptions()){
                fix();
                Lime.fontManager.comfortaa_hud_sense.drawString(str, x + 265, y + 20 + rendered + yCount * 24, -1);
                yCount++;
            }
        }
        super.render(x, y, width, height, mouseX, mouseY);
    }
}
