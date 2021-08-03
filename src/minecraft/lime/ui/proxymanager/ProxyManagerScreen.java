package lime.ui.proxymanager;

import com.thealtening.auth.TheAlteningAuthentication;
import lime.core.Lime;
import lime.ui.fields.PasswordField;
import lime.ui.notifications.Notification;
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
import java.net.Proxy;

public class ProxyManagerScreen extends GuiScreen {

    private GuiTextField username;
    private PasswordField password;
    private ProxyCheckThread runningThread;
    private final GuiScreen parentScreen;

    private final long initTime;

    public ProxyManagerScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.initTime = System.currentTimeMillis();
    }

    @Override
    public void initGui() {
        this.username = new GuiTextField(0, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 20 - 40, 200, 20);
        this.password = new PasswordField(1, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 20 - 16, 200, 20);
        this.username.setMaxStringLength(32);
        this.password.setMaxStringLength(256);
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + 40, 200, 20, "Connect"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 2 + 40 + 24, 200, 20, "No Proxy"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + 40 + 24 + 24, 200, 20, "Back"));

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
        password.drawTextBox();

        if(username.getText().isEmpty())
            this.drawString(mc.fontRendererObj, "Username", this.width / 2 - 95, this.height / 2 - 20 - 34, new Color(75, 75, 75).getRGB());
        if(password.getText().isEmpty())
            this.drawString(mc.fontRendererObj, "Password", this.width / 2 - 95, this.height / 2 - 20 - 10, new Color(75, 75, 75).getRGB());

        this.drawCenteredString(mc.fontRendererObj, "Proxy Manager", this.width / 2, 10, new Color(75, 75, 75).getRGB());
        this.drawCenteredString(mc.fontRendererObj, this.runningThread == null || this.runningThread.getStatus().contains("wait") ? this.username.getText().length() > 16 && !this.username.getText().contains("@") && this.password.getText().isEmpty() ? "§4Warning: You can't use a cracked account with more than 16 characters" : "Waiting" : this.runningThread.getStatus(), this.width / 2, 20, new Color(75, 75, 75).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if(button.id == 4) {
            Lime.getInstance().setProxy(Proxy.NO_PROXY);
        }
        if(button.id == 3) mc.displayGuiScreen(parentScreen);
        if(button.id == 2 && button.enabled) {
            this.runningThread = new ProxyCheckThread(username.getText(), password.getText(), ProxyVersion.SOCKSv4);
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

    public GuiScreen getParentScreen() {
        return parentScreen;
    }
}
