package lime.ui.altmanager.guis;

import com.thealtening.auth.TheAlteningAuthentication;
import lime.core.Lime;
import lime.ui.altmanager.AltLoginThread;
import lime.ui.fields.PasswordField;
import lime.ui.notifications.Notification;
import lime.utils.other.WebUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;

public class DirectLoginScreen extends GuiScreen {

    private GuiTextField username;
    private PasswordField password;
    private AltLoginThread runningThread;
    private final GuiScreen parentScreen;
    private boolean microsoft;


    public DirectLoginScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.username = new GuiTextField(0, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 20 - 40, 200, 20);
        this.password = new PasswordField(1, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 20 - 16, 200, 20);
        this.username.setMaxStringLength(48);
        this.password.setMaxStringLength(256);
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + 40, 200, 20, "Log In"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 2 + 40 + 24, 200, 20, "Copy Mail:Pass"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + 40 + 24 + 24, 200, 20, "Back"));
        this.buttonList.add(new GuiButton(20, this.width - 105, 3, 100, 20, "Mojang"));
        microsoft = false;

        for (GuiButton guiButton : buttonList) {
            if(guiButton.id == 20) {
                guiButton.displayString = Lime.getInstance().theAltening ? "The Altening" : "Mojang";
            }
        }

        if(Lime.getInstance().theAltening) {
            TheAlteningAuthentication.theAltening();
        } else {
            TheAlteningAuthentication.mojang();
        }

        Keyboard.enableRepeatEvents(true);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(true);
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //this.drawDefaultBackground();
        GuiScreen.drawRect(0, 0, mc.displayWidth, mc.displayHeight, new Color(21, 21, 21).getRGB());

        this.buttonList.get(0).enabled = !username.getText().isEmpty();

        username.drawTextBox();
        if(!Lime.getInstance().theAltening) {
            password.drawTextBox();
        }

        if(username.getText().isEmpty())
            this.drawString(mc.fontRendererObj, !Lime.getInstance().theAltening ? "Username" : "Token", this.width / 2 - 95, this.height / 2 - 20 - 34, new Color(75, 75, 75).getRGB());
        if(password.getText().isEmpty() && !Lime.getInstance().theAltening)
            this.drawString(mc.fontRendererObj, "Password", this.width / 2 - 95, this.height / 2 - 20 - 10, new Color(75, 75, 75).getRGB());

        this.drawCenteredString(mc.fontRendererObj, "Alt Login (Username: " + mc.session.getUsername() + ")", this.width / 2, 10, new Color(75, 75, 75).getRGB());
        this.drawCenteredString(mc.fontRendererObj, this.runningThread == null || this.runningThread.getStatus().contains("wait") ? this.username.getText().length() > 16 && !this.username.getText().contains("@") && this.password.getText().isEmpty() ? "§4Warning: You can't use a cracked account with more than 16 characters" : "Waiting" : this.runningThread.getStatus(), this.width / 2, 20, new Color(75, 75, 75).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 3) mc.displayGuiScreen(parentScreen);
        if(button.id == 4) {
            String clipboard = GuiScreen.getClipboardString();
            if(!clipboard.contains(":")) return;
            this.username.setText(clipboard.split(":")[0]);
            this.password.setText(clipboard.split(":")[1]);
            this.runningThread = new AltLoginThread(username.getText(), password.getText(), microsoft);
            this.runningThread.start();
        }
        if(button.id == 2 && button.enabled) {
            if(Lime.getInstance().theAltening) {
                TheAlteningAuthentication.theAltening();
            } else {
                TheAlteningAuthentication.mojang();
            }
            this.runningThread = new AltLoginThread(username.getText(), !Lime.getInstance().theAltening ? password.getText() : "aaa", microsoft);
            this.runningThread.start();
        }
        if(button.id == 20) {
            if(button.displayString.equalsIgnoreCase("Microsoft")) {
                button.displayString = "Mojang";
                TheAlteningAuthentication.mojang();
                microsoft = false;
                Lime.getInstance().theAltening = false;
            } else if(button.displayString.equalsIgnoreCase("mojang")) {
                button.displayString = "The Altening";
                TheAlteningAuthentication.theAltening();
                Lime.getInstance().theAltening = true;
            } else if(button.displayString.equalsIgnoreCase("the altening")) {
                microsoft = true;
                TheAlteningAuthentication.mojang();
                button.displayString = "Microsoft";
                Lime.getInstance().theAltening = false;
            }
        }
        if(button.id == 21) {
            if(!microsoft) {
                button.displayString = "Microsoft";
            } else {
                button.displayString = "Mojang";
            }
            microsoft = !microsoft;
        }
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        username.mouseClicked(mouseX, mouseY, mouseButton);
        if(!Lime.getInstance().theAltening) {
            password.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 15) {
            username.setFocused(!username.isFocused());
            password.setFocused(!password.isFocused());
        }
        if(keyCode == 28) this.actionPerformed(this.buttonList.get(0));
        if(keyCode == 1) {
            mc.displayGuiScreen(parentScreen);
            return;
        }
        username.textboxKeyTyped(typedChar, keyCode);
        if(!Lime.getInstance().theAltening) {
            password.textboxKeyTyped(typedChar, keyCode);

        }
        super.keyTyped(typedChar, keyCode);
    }

    public GuiScreen getParentScreen() {
        return parentScreen;
    }
}
