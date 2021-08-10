package lime.ui.altmanager;

import com.thealtening.auth.TheAlteningAuthentication;
import lime.core.Lime;
import lime.managers.FontManager;
import lime.ui.fields.ButtonField;
import lime.ui.fields.PasswordField;
import lime.ui.notifications.Notification;
import lime.ui.realaltmanager.AltManager;
import lime.utils.other.WebUtils;
import lime.utils.render.GLSLSandboxShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;

public class AltLoginScreen extends GuiScreen {

    private GuiTextField username;
    private PasswordField password;
    private AltLoginThread runningThread;
    private final GuiScreen parentScreen;

    private final long initTime;


    public AltLoginScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.initTime = System.currentTimeMillis();
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
        this.buttonList.add(new GuiButton(20, this.width - 105, 3, 100, 20, "The Altening"));
        this.buttonList.add(new GuiButton(69, 3, 3, 100, 20, "FunCraft Ban"));

        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "h", 3, 40, 100, 20, new Color(25, 25, 25), () -> {
            mc.displayGuiScreen(new AltManager().getAltManagerScreen());
        }));

        for (GuiButton guiButton : buttonList) {
            if(guiButton.id == 20) {
                if(Lime.getInstance().theAltening) {
                    guiButton.displayString = "Mojang";
                    TheAlteningAuthentication.mojang();
                } else {
                    guiButton.displayString = "The Altening";
                    TheAlteningAuthentication.theAltening();
                }
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
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        Minecraft.getMinecraft().getShader().useShader(this.width + 935, this.height + 500, mouseX, mouseY, (System.currentTimeMillis() - initTime) / 1000F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

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
        if(button.id == 69) {
            if(WebUtils.getSource("https://www.funcraft.net/fr/joueurs?q=" + Minecraft.getMinecraft().getSession().getUsername()).toLowerCase().contains("ce joueur est banni")) {
                Lime.getInstance().getNotificationManager().addNotification(new Notification("Alt", "Alt is banned on funcraft", Notification.Type.WARNING));
            } else {
                Lime.getInstance().getNotificationManager().addNotification(new Notification("Alt", "Alt is unbanned on funcraft", Notification.Type.SUCCESS));
            }
        }
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
            if(Lime.getInstance().theAltening) {
                TheAlteningAuthentication.theAltening();
            } else {
                TheAlteningAuthentication.mojang();
            }
            this.runningThread = new AltLoginThread(username.getText(), !Lime.getInstance().theAltening ? password.getText() : "aaa");
            this.runningThread.start();
        }
        if(button.id == 20) {
            Lime.getInstance().theAltening = !Lime.getInstance().theAltening;
            if(Lime.getInstance().theAltening) {
                button.displayString = "The Altening";
                TheAlteningAuthentication.theAltening();
            } else {
                button.displayString = "Mojang";
                TheAlteningAuthentication.mojang();
            }
            //mc.displayGuiScreen(new TheAlteningLoginScreen(this));
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
        if(keyCode == 1) mc.displayGuiScreen(parentScreen);
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
