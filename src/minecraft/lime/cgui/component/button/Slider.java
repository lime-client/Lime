package lime.cgui.component.button;

import lime.Lime;
import lime.cgui.component.Component;
import lime.settings.Setting;
import lime.utils.render.Util2D;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    public Slider(Setting set){
        super(set);
    }
    private boolean dragging = false;
    private double renderWidth;
    int x, y, width, height;
    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height =height;
        Util2D.drawRoundedRect(x + (width / 2 ) + 5, y + 62 + rendered, x + (width / 2 ) + 115, y + 60 + rendered + 5, new Color(50, 50, 50).getRGB(), new Color(50, 50, 50).getRGB());
        Lime.fontManager.comfortaa_slider.drawString(set.getName() + ": " + set.getValDouble(), x + (width / 2 ) + 5, y + 64 + rendered - 8, -1);

        double diff = Math.min(115, Math.max(0, mouseX - this.x - 130));

        double min = set.getMin();
        double max = set.getMax();

        renderWidth = (115) * (set.getValDouble() - min) / (max - min);
        Util2D.drawdCircle(x + (width / 2 ) + 5 + renderWidth, y + 64 + rendered - 1, 5, 5, new Color(50, 50, 50).getRGB());
        if (dragging) {
            if (diff == 0) {
                set.setValDouble(set.getMin());
            }
            else {

                double newValue = roundToPlace(((diff / 115) * (max - min) + min), 2);
                set.setValDouble(newValue);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0 && hover(x + (width / 2 ) + 3 + renderWidth, y + 64 + rendered - 3, mouseX, mouseY, 5, 5)){
            dragging = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }
    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
