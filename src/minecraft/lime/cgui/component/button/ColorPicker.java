package lime.cgui.component.button;

import lime.Lime;
import lime.cgui.component.Component;
import lime.settings.Setting;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.IntBuffer;

public class ColorPicker extends Component {
    public ColorPicker(Setting set){
        super(set);
    }
    Color color = new Color(set.getValColor());
    @Override
    public void render(int x, int y, int width, int height, int mouseX, int mouseY) {
        y = y + 44;
        x = x + 4;
        this.rendered = rendered - 110;
        GlStateManager.color(1, 1, 1);
        Util2D.drawImage(new ResourceLocation("textures/icons/colorpicker.png"), x + width / 2 + 2, y + 70 + this.rendered, 112, 64);
        Gui.drawRect(x + width / 2 + 2, y + this.rendered + 55, x + width /2 + 12, y + this.rendered + 65, color.getRGB());
        Lime.fontManager.roboto_sense.drawString(set.getName(), x + width / 2 + 14, y + 57 + this.rendered, -1);
        if(set.posXColor != -1){
            Util2D.drawFullCircle(set.posXColor, set.posYColor, 3, 3, new Color(200, 200, 200).getRGB());
        }
        if(Mouse.isButtonDown(0) && hover(x + width / 2 + 2, y + this.rendered + 70, mouseX, mouseY, 111, 60)){
            color = new Color(getColorUnderMouse());
            set.setColor(getColorUnderMouse());
            set.posXColor = mouseX;
            set.posYColor = mouseY;
        }
        super.render(x, y, width, height, mouseX, mouseY);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public int getColorUnderMouse() {
        final IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
        final int[] ints = { 0 };
        GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, 32993, 33639, intbuffer);
        intbuffer.get(ints);
        return ints[0];
    }
}
