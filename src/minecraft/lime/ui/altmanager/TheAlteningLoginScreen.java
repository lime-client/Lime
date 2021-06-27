package lime.ui.altmanager;

import com.thealtening.auth.TheAlteningAuthentication;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TheAlteningLoginScreen extends GuiScreen {

    private GuiTextField tokenTextField;
    private AltLoginThread runningThread;

    private final GuiScreen parentScreen;

    public TheAlteningLoginScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public GuiScreen getParentScreen() {
        return parentScreen;
    }

    @Override
    public void initGui() {
        tokenTextField = new GuiTextField(0, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 10, 200, 20);

        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + 40, 200, 20, "Log In"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 2 + 40 + 24, 200, 20, "Copy Token"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + 40 + 24 + 24, 200, 20, "Back"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        tokenTextField.drawTextBox();
        this.drawCenteredString(mc.fontRendererObj, "Alt Login (Username: " + mc.session.getUsername() + ")", this.width / 2, 10, new Color(75, 75, 75).getRGB());
        this.drawCenteredString(mc.fontRendererObj, this.runningThread == null || this.runningThread.getStatus().contains("wait") ? "Waiting" : this.runningThread.getStatus(), this.width / 2, 20, new Color(75, 75, 75).getRGB());
        if(!tokenTextField.isFocused() && tokenTextField.getText().isEmpty())
            mc.fontRendererObj.drawStringWithShadow("Token", this.width / 2 - 95, this.height / 2 - 10 + 6, new Color(75, 75, 75).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 2) {
            TheAlteningAuthentication.theAltening();
            this.runningThread = new AltLoginThread(tokenTextField.getText(), "cccv");
            this.runningThread.start();
        }
        if(button.id == 3) {
            mc.displayGuiScreen(((AltLoginScreen) this.parentScreen).getParentScreen());
        }
        if(button.id == 4) {
            try {
                String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                TheAlteningAuthentication.theAltening();
                if(clipboard.contains("@alt.com")) {
                    this.tokenTextField.setText(clipboard.trim());
                    this.runningThread = new AltLoginThread(tokenTextField.getText(), "cccv");
                    this.runningThread.start();
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            }
        }
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        tokenTextField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        tokenTextField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
}
