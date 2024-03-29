package lime.ui.clickgui.frame2.components;

import lime.core.Lime;
import lime.features.module.Category;
import lime.management.FontManager;
import lime.utils.render.RenderUtils;
import lime.utils.render.animation.Translate;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static lime.ui.clickgui.frame2.Priority.*;

public class FrameCategory {

    // Stuff
    private int x, y, xDrag, yDrag;
    private int width, height;

    private int offset; // Used to scroll

    private boolean drag;

    private final Category category;

    private final ArrayList<FrameModule> modules;

    // Smooth animation
    private final Animate animation;
    private Translate translate;

    // Asking x and y so categories are not on themself
    public FrameCategory(Category category, int x, int y)
    {
        this.category = category;
        this.modules = new ArrayList<>();
        this.animation = new Animate().setEase(Easing.CUBIC_OUT).setSpeed(250).setMin(0).setMax(defaultWidth / 2F);

        this.x = x;
        this.y = y;
        this.xDrag = 0;
        this.yDrag = 0;
        this.offset = 0;

        this.drag = false;

        this.width = defaultWidth;
        this.height = defaultHeight;

        Lime.getInstance().getModuleManager().getModulesFromCategory(category).forEach(module -> this.modules.add(new FrameModule(module, this, 0, 0)));
    }

    public void initGui()
    {
        this.animation.setSpeed(100).reset();
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        this.translate = new Translate(scaledResolution.getScaledWidth() / 2F - (width / 2F), scaledResolution.getScaledHeight() / 2F - (height / 2F));
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            translate.interpolate(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 5);
            double x = scaledResolution.getScaledWidth() / 2F - (translate.getX() / 2);
            double y = scaledResolution.getScaledHeight() / 2F - (translate.getY() / 2);
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(translate.getX() / scaledResolution.getScaledWidth(), translate.getY() / scaledResolution.getScaledHeight(), 1);
        }

        AtomicInteger offCat = new AtomicInteger();
        this.modules.forEach(module -> offCat.addAndGet(module.getOffset()));

        // Calculate height
        height = Math.min(categoryNameHeight + offCat.get(), defaultHeight);

        if(Mouse.hasWheel() && GuiScreen.hover(x, y, mouseX, mouseY, defaultWidth, height))
        {
            int offsetMultiplier = 15;
            int wheel = Mouse.getDWheel();
            if(wheel > 0 && offset - (5 - 1) > 0) {
                offset -= offsetMultiplier;
            } else if(wheel < 0 && offset + (5 - 1) <= (offCat.get()) - height + categoryNameHeight) {
                offset += offsetMultiplier;
            }

            offset = Math.max(Math.min(offset, offCat.get()), 0);
        }

        if((offCat.get()) - height + categoryNameHeight < offset) {
            offset = (offCat.get()) - height + categoryNameHeight;
        }

        // Drawing category base
        Gui.drawRect(getX(), getY(), getX() + width, getY() + getHeight(), getMainColor());

        // Drawing category name section
        Gui.drawRect(getX(), getY(), getX() + width, getY() + categoryNameHeight, getDarkerMainColor());

        // Outline category base
        {
            Gui.drawRect(getX() - outlineWidth, getY(), getX(), getY() + getHeight(), getDarkerMainColor());
            Gui.drawRect(getX() + width, getY(), getX() + width + outlineWidth, getY() + getHeight(), getDarkerMainColor());
            Gui.drawRect(getX() - outlineWidth, y + getHeight(), getX() + width + outlineWidth, getY() + getHeight() + outlineWidth, getDarkerMainColor());
        }

        // Drag ClickGUI
        if(drag) {
            setX(this.xDrag + mouseX);
            setY(this.yDrag + mouseY);
        }

        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.resetColor();
        RenderUtils.drawImage(new ResourceLocation("lime/clickgui/frame/" + category.name().toLowerCase() + ".png"),getX() + width - 12, y + 3, 8, 8, true);
        GlStateManager.resetColor();
        GL11.glColor4f(1, 1, 1, 1);
        // Drawing category name
        FontManager.ProductSans18.getFont().drawString(category.name(), x + 3, y + ((categoryNameHeight / 2F) - FontManager.ProductSans18.getFont().getFontHeight() / 2F), stringColor, true);

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtils.prepareScissorBox(getX() + (width / 2F) - defaultWidth, getY() + categoryNameHeight, x + (width / 2F) + defaultWidth, y + getHeight());


        // Drawing modules
        int i = 0;
        for (FrameModule module : this.modules)
        {
            module.setX(x);
            module.setY(y + categoryNameHeight + i - offset);
            module.drawScreen(mouseX, mouseY);
            i += module.getOffset();
        }

        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // I really need to explain?
        for (FrameModule module : this.modules)
        {
            if(module.mouseClicked(mouseX, mouseY, mouseButton)) {
                setDrag(false);
                return;
            }
        }

        if(GuiScreen.hover(x, y, mouseX, mouseY, width, height) && mouseButton == 0)
        {
            setDrag(true);
            setXDrag(getX() - mouseX);
            setYDrag(getY() - mouseY);
        } else
            setDrag(false);
    }

    public void keyTyped(char typedChar, int key) {
        for (FrameModule module : this.modules) {
            module.keyTyped(typedChar, key);
        }
    }

    @SuppressWarnings("unused")
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.drag = false;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY()
    {
        return y;
    }

    public void setXDrag(int xDrag)
    {
        this.xDrag = xDrag;
    }

    public void setYDrag(int yDrag)
    {
        this.yDrag = yDrag;
    }

    public void setDrag(boolean drag)
    {
        this.drag = drag;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
