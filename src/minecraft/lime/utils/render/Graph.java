package lime.utils.render;

import lime.managers.FontManager;
import lime.utils.movement.MovementUtils;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Graph {
    private final int x, y;

    private final String graphName;

    private final ArrayList<Float> values;


    public Graph(int x, int y, String graphName) {
        this.x = x;
        this.y = y;
        this.graphName = graphName;
        this.values = new ArrayList<>();
    }

    public void renderGraphs(){
        FontManager.ProductSans20.getFont().drawString(graphName + " | " + MovementUtils.getBPS() + " / Sec", x, y - FontManager.ProductSans20.getFont().getFontHeight(), -1);

        GL11.glPushMatrix();
        RenderUtils.prepareScissorBox(x, y, x + 125, y + 48);
        GL11.glEnable(3089);

        Gui.drawRect(x, y, x + 125, y + 48, 0x90000000);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glLineWidth(1.5f);
        int space = 8;

        for (int i = 0; i < values.size(); i++) {
            GL11.glBegin(3);
            GL11.glColor4f(255, 255, 255, 255);
            GL11.glVertex2f(x + i * space, (y + 43) - values.get(i) / 4);
            if (i + 1 < values.size())
                GL11.glVertex2f(x + (i + 1) * space, (y + 43) - values.get(i + 1) / 4);
            GL11.glEnd();
        }

        if (values.size() > 20) {
            values.remove(0);
        }
        GL11.glLineWidth(1.5f);
        if(values.size() > 1) {
            GL11.glBegin(3);
            GL11.glColor4f(0, 255, 0, 255);
            GL11.glVertex2f(x + 105, (y + 43) - values.get(values.size() - 1) / 4);
            GL11.glVertex2f(x + 125, (y + 43) - values.get(values.size() - 1) / 4);
            GL11.glEnd();
        }

        //bottom
        GL11.glBegin(3);
        GL11.glColor4f(255, 255, 255, 255);
        GL11.glVertex2f(x, y + 48);
        GL11.glVertex2f(x + 125, y + 48);
        GL11.glEnd();

        //top
        GL11.glBegin(3);
        GL11.glColor4f(255, 255, 255, 255);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + 125, y);
        GL11.glEnd();


        //left
        GL11.glBegin(3);
        GL11.glColor4f(255, 255, 255, 255);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + 48);
        GL11.glEnd();

        //right
        GL11.glBegin(3);
        GL11.glColor4f(255, 255, 255, 255);
        GL11.glVertex2f(x + 125, y);
        GL11.glVertex2f(x + 125, y + 48);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    public void addValue(float i) {
        this.values.add(i);
    }
}
