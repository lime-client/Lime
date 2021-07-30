package lime.ui.clickgui.frame2;

import java.awt.Color;

public class Priority {
    public static int stringColor = -1;

    public static int defaultWidth = 120;
    public static int defaultHeight = 300;


    public static int outlineWidth = 1;
    public static int categoryNameHeight = 15;

    public static int moduleHeight = 15;

    public static boolean hoveredColor = true;

    public static int getMainColor() {
        return new Color(41, 41, 41).getRGB();
    }
    public static int getDarkerMainColor() {
        return new Color(25, 25, 25).getRGB();
    }
    public static int getEnabledColor() {
        return new Color(30, 30, 30).getRGB();
    }
}
