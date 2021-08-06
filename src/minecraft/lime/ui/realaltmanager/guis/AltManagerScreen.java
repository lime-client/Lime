package lime.ui.realaltmanager.guis;

import lime.ui.realaltmanager.AltManager;
import net.minecraft.client.gui.GuiScreen;

public class AltManagerScreen extends GuiScreen {
    private final AltManager altManager;

    public AltManagerScreen(AltManager altManager) {
        this.altManager = altManager;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
