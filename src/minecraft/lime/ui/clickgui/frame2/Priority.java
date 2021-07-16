package lime.ui.clickgui.frame2;

import java.awt.Color;

public interface Priority {
    int stringColor = -1;

    int defaultWidth = 125;
    int defaultHeight = 300;

    int enabledColor = new Color(174, 28, 28).getRGB();

    int mainColor = new Color(25, 25, 25).getRGB();
    int darkerMainColor = new Color(mainColor).darker().getRGB();

    int outlineWidth = 1;
    int categoryNameHeight = 20;

    int moduleHeight = 15;

    boolean hoveredColor = false;
}
