package lime.ui.clickgui.test;

import lime.features.module.Category;
import lime.ui.clickgui.test.components.FrameCategory;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {
    private int x, y, width, height;
    private final ArrayList<FrameCategory> categories;
    private int index;

    private boolean init;

    private final Animate selectedCategoryAnimation;


    public ClickGUI() {
        x = 0;
        y = 0;
        width = Category.values().length * 32;
        height = 250;
        index = -1;
        selectedCategoryAnimation = new Animate().setEase(Easing.LINEAR).setSpeed(125);
        categories = new ArrayList<>();
        for (Category value : Category.values()) {
            categories.add(new FrameCategory(value));
        }
    }

    @Override
    public void initGui() {
        if(!init) {
            ScaledResolution sr = new ScaledResolution(this.mc);
            x = sr.getScaledWidth() / 2 - (width / 2);
            y = sr.getScaledHeight() / 2 - (height / 2);
            init = true;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Animations
        selectedCategoryAnimation.setSpeed(150).update();

        Gui.drawRect(x - 1, y - 1, x + width + 1, y + height + 1, new Color(25, 25, 25).getRGB());
        Gui.drawRect(x, y, x + width, y + height, new Color(41, 41, 41).getRGB());

        Gui.drawRect(x, y + 33, x + width, y + 34, new Color(60, 60, 60).getRGB());

        int i = 0;
        for (FrameCategory category : categories) {
            //RenderUtils.glColor(getColor(category.getCategory()));
            RenderUtils.drawImage(new ResourceLocation("lime/clickgui/test/" + category.getCategory().name().toLowerCase() +".png"),x + (i * 32), y, 32, 32, true);
            ++i;
        }

        //System.out.println(index);

        if(index != -1) {
            Gui.drawRect(x+selectedCategoryAnimation.getValue(), y + 32, x+selectedCategoryAnimation.getValue() + 32,  y + 33, getColor(categories.get(index).getCategory()));
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int getColor(Category category) {
        switch(category) {
            case COMBAT:
                return new Color(172, 40, 46, 255).getRGB();
            case EXPLOIT:
            case SCRIPT:
                return new Color(0, 0, 0, 255).getRGB();
            case PLAYER:
                return new Color(25, 67, 131, 255).getRGB();
            case MOVEMENT:
                return new Color(33, 120, 146, 255).getRGB();
            case WORLD:
                return new Color(27, 200, 35, 77).getRGB();
            case RENDER:
                return new Color(117, 36, 152, 255).getRGB();
        }
        // wtf
        return Color.BLACK.getRGB();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(int i = 0; i < Category.values().length; ++i) {
            if(GuiScreen.hover(x + (i * 32), y, mouseX, mouseY, 32, 32)) {
                selectedCategoryAnimation.setMin(0).setMax((i * 32));
                index = i;
                break;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
