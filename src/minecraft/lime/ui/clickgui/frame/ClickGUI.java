package lime.ui.clickgui.frame;

import lime.core.Lime;
import lime.features.module.Category;
import lime.management.FontManager;
import lime.ui.clickgui.frame.components.FrameCategory;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {
    private final ArrayList<FrameCategory> frames;
    private boolean configMenuOpened;

    private final Animate animate = new Animate();

    public ClickGUI() {
        frames = new ArrayList<>();

        int i = 0;
        for(Category category : Category.values()) {
            this.frames.add(new FrameCategory(category, 10 + (i * 125), 10, 120));
            i++;
        }

        configMenuOpened = true;
    }

    @Override
    public void initGui() {
        animate.setSpeed(175);
        animate.setMax(151);
        animate.setMin(0);
        animate.setReversed(false);
        animate.setEase(Easing.CUBIC_OUT);
        animate.reset();

        for (FrameCategory frame : frames) {
            frame.initGui();
        }
        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean cancel = false;
        for(FrameCategory frame : frames) {
            if(!cancel)
                cancel = frame.keyTyped(typedChar, keyCode);
        }
        if(!cancel) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if(configMenuOpened) {
            animate.update();
            this.drawConfigMenu(mouseX, mouseY);
        }

        for (FrameCategory frame : frames) {
            if(frame.getCategory() == Category.SCRIPT && Lime.getInstance().getModuleManager().getModulesFromCategory(frame.getCategory()).isEmpty()) continue;
            frame.drawFrame(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (FrameCategory frame : frames) {
            if(frame.mouseClicked(mouseX, mouseY, mouseButton))
                break;
        }

        File configPath = new File("Lime" + File.separator + "configs");

        ScaledResolution sr = new ScaledResolution(this.mc);
        double width = sr.getScaledWidth_double();
        double height = sr.getScaledHeight_double() - animate.getValue();
        int i = 0;
        for (String s : configPath.list()) {
            if(hover((int) width - 25, (int) height - 76 + 15 + (i * 16), mouseX, mouseY, 25, 16) && mouseButton == 0) {
                Lime.getInstance().getCommandManager().callCommand(".config load " + s.replace(".json", ""));
            }
            ++i;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (FrameCategory frame : frames) {
            frame.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onGuiClosed() {
        for (FrameCategory frame : frames) {
            frame.onGuiClosed();
        }
        mc.gameSettings.guiScale = lime.features.module.impl.render.ClickGUI.guiScale;
        super.onGuiClosed();
    }


    public void drawConfigMenu(int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(mc);
        double width = sr.getScaledWidth_double();
        double height = sr.getScaledHeight_double() - animate.getValue();

        Color arroundColor = new Color(25, 25, 25);

        Gui.drawRect(width - 135, height - 75, width, height + 76, new Color(41, 41, 41).getRGB());

        Gui.drawRect(width - 135, height - 76, width, height - 75, arroundColor.getRGB());
        Gui.drawRect(width - 135, height - 76, width - 134, height + 76, arroundColor.getRGB());
        Gui.drawRect(width - 135, height + 75, width, height + 76, new Color(25, 25, 25).getRGB());

        Gui.drawRect(width - 135, height - 76, width, height - 76 + 15, arroundColor.getRGB());

        Gui.drawRect(width - 135, height + 25, width, height + 26, arroundColor.getRGB());

        FontManager.ProductSans18.getFont().drawStringWithShadow("Config Menu", (float) width - 135 + 3, (float) height - 74, -1);

        File configPath = new File("Lime" + File.separator + "configs");
        int i = 0;
        for (String s : configPath.list()) {
            FontManager.ProductSans20.getFont().drawStringWithShadow(s.replace(".json", ""), (float) width - 132, (float) height - 76 + 15 + (i * 16), -1);
            Gui.drawRect(width - 25, height - 76 + 15 + (i * 16), width, height - 76 + 15 + (i * 16) + 16, hover((int) width - 25, (int) height - 76 + 15 + (i * 16), mouseX, mouseY, 25, 16) ? arroundColor.darker().getRGB() : arroundColor.getRGB());
            Gui.drawRect(width - 25 - 15, height - 76 + 15 + (i * 16), width - 25, height - 76 + 15 + (i * 16) + 16, hover((int) width  - 25 - 15, (int) height - 76 + 15 + (i * 16), mouseX, mouseY, 15, 16) ? new Color(255, 0, 0).darker().getRGB() : new Color(255, 0, 0).getRGB());
            FontManager.ProductSans18.getFont().drawStringWithShadow("Load", (float) width - 23.5f,(float) height - 76 + 15 + (i * 16) + 1, -1);
            ++i;
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(new ResourceLocation("lime/clickgui/frame/expand.png"));
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Gui.drawModalRectWithCustomSizedTexture(((int) width - 12) * 2, ((int) height - 75 + 4) * 2, 0, 0, 16, 10, 16, 10);
        GL11.glPopMatrix();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
