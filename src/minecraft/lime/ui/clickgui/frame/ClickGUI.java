package lime.ui.clickgui.frame;

import lime.features.module.Category;
import lime.ui.clickgui.frame.components.FrameCategory;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {
    private final ArrayList<FrameCategory> frames;

    public ClickGUI() {
        frames = new ArrayList<>();

        int i = 0;
        for(Category category : Category.values()) {
            this.frames.add(new FrameCategory(category, 10 + (i * 155), 10, 135, 250));
            i++;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean cancel = false;
        for(FrameCategory frame : frames) {
            if(!cancel)
                cancel = frame.keyTyped(keyCode);
        }
        if(!cancel) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (FrameCategory frame : frames) {
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
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
