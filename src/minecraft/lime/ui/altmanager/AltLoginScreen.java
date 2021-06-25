package lime.ui.altmanager;

import lime.ui.fields.PasswordField;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;

public class AltLoginScreen extends GuiScreen {

    private GuiTextField username;
    private PasswordField password;
    private AltLoginThread runningThread;
    private final GuiScreen parentScreen;

    public AltLoginScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.username = new GuiTextField(0, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 20 - 40, 200, 20);
        this.password = new PasswordField(1, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 20 - 16, 200, 20);
        this.username.setMaxStringLength(32);
        this.password.setMaxStringLength(256);
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + 40, 200, 20, "Log In"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 2 + 40 + 24, 200, 20, "Copy Mail:Pass"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + 40 + 24 + 24, 200, 20, "Back"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.buttonList.get(0).enabled = !username.getText().isEmpty();

        username.drawTextBox();
        password.drawTextBox();

        if(username.getText().isEmpty())
            this.drawString(mc.fontRendererObj, "Username", this.width / 2 - 95, this.height / 2 - 20 - 34, new Color(75, 75, 75).getRGB());
        if(password.getText().isEmpty())
            this.drawString(mc.fontRendererObj, "Password", this.width / 2 - 95, this.height / 2 - 20 - 10, new Color(75, 75, 75).getRGB());

        this.drawCenteredString(mc.fontRendererObj, "Alt Login (Username: " + mc.session.getUsername() + ")", this.width / 2, 10, new Color(75, 75, 75).getRGB());
        this.drawCenteredString(mc.fontRendererObj, this.runningThread == null || this.runningThread.getStatus().contains("wait") ? this.username.getText().length() > 16 && !this.username.getText().contains("@") && this.password.getText().isEmpty() ? "§4Warning: You can't use a cracked account with more than 16 characters" : "Waiting" : this.runningThread.getStatus(), this.width / 2, 20, new Color(75, 75, 75).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 3) mc.displayGuiScreen(parentScreen);
        if(button.id == 4) {
            try {
                String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                if(!clipboard.contains(":")) return;
                this.username.setText(clipboard.split(":")[0]);
                this.password.setText(clipboard.split(":")[1]);
                this.runningThread = new AltLoginThread(username.getText(), password.getText());
                this.runningThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(button.id == 2 && button.enabled) {
            this.runningThread = new AltLoginThread(username.getText(), password.getText());
            this.runningThread.start();
        }
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        username.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 15) {
            username.setFocused(!username.isFocused());
            password.setFocused(!password.isFocused());
        }
        if(keyCode == 28) this.actionPerformed(this.buttonList.get(0));
        if(keyCode == 1) mc.displayGuiScreen(parentScreen);
        username.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
}