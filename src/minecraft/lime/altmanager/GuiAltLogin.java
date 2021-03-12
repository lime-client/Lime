
package lime.altmanager;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.thealtening.auth.TheAlteningAuthentication;
import lime.Lime;
import lime.file.impl.ApiKeySaver;
import lime.utils.render.Util2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public final class GuiAltLogin
extends GuiScreen {
    private PasswordField password;
    private PasswordField apikey;
    private final GuiScreen previousScreen;
    private AltLoginThread thread;
    private GuiTextField username;

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }
    public static String getContentURL(String string) throws IOException
    {
        URL urlObject = new URL(string);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        return toStringURL(urlConnection.getInputStream());
    }

    private static String toStringURL(InputStream inputStream) throws IOException
    {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")))
        {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 0: {
                if(this.username.getText().equals("") && !this.apikey.getText().equals("")){
                    try{
                        String oof = getContentURL("http://api.thealtening.com/v2/generate?key=" + apikey.getText());
                        if(oof.contains("token")){
                            System.out.println(oof.split("\"token\":\"")[1].split("\",\"password\":\"anything\"")[0]);
                            username.setText(oof.split("\"token\":\"")[1].split("\",\"password\":\"anything\"")[0]);
                            password.setText("jevousaimetous");
                            Lime.altManager.theAlteningAuthentication = TheAlteningAuthentication.theAltening();
                            this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                            this.thread.start();
                            if(!((ApiKeySaver) Lime.fileManager.getFileByClass(ApiKeySaver.class)).load().equals(apikey.getText())){
                                ((ApiKeySaver) Lime.fileManager.getFileByClass(ApiKeySaver.class)).save(apikey.getText());
                            }
                            return;
                        } else {
                            return;
                        }
                    } catch (Exception e){

                    }
                } else if(!this.username.getText().equals("")){
                    if(username.getText().contains("@alt.com")){
                        Lime.altManager.theAlteningAuthentication = TheAlteningAuthentication.theAltening();
                    } else {
                        Lime.altManager.theAlteningAuthentication = TheAlteningAuthentication.mojang();
                    }
                    this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                    this.thread.start();
                    return;
                }

                break;
            }
            case 2:{
                try{
                    String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if(data.contains(":")){
                        this.username.setText(data.split(":")[0]);
                        this.password.setText(data.split(":")[1]);
                    }
                } catch (Exception ignored){}
                break;
            }
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        this.drawDefaultBackground();
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.apikey.drawTextBox();
        this.drawCenteredString(this.mc.fontRendererObj, "Alt Login", width / 2, 20, -1);
        this.drawCenteredString(this.mc.fontRendererObj, this.thread == null ? (Object)((Object)EnumChatFormatting.GRAY) + "Idle..." : this.thread.getStatus(), width / 2, 29, -1);
        if(this.thread != null && thread.getStatus().contains("Logging")){
            Util2D.drawImage(new ResourceLocation("lime/logging.png"), width / 2 - 18, 28 + this.fontRendererObj.FONT_HEIGHT + 5, 36, 36);
        }
        if(this.thread != null && thread.getStatus().contains("Logged")){
            Util2D.drawImage(new ResourceLocation("lime/done.png"), width / 2 - 18, 28 + this.fontRendererObj.FONT_HEIGHT + 5, 36, 36);
        }
        if (this.username.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Username / E-Mail", width / 2 - 96, 96, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Password", width / 2 - 96, 136, -7829368);
        }
        if(this.apikey.getText().isEmpty()){
            this.drawString(this.mc.fontRendererObj, "TheAltening API Key", width / 2 - 96, 176, -7829368);
        }

        super.drawScreen(x2, y2, z2);
    }

    @Override
    public void initGui() {

        int var3 = height / 4 + 24;
        this.buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, var3 + 72 + 12 + 24, "Copy Email:Pass"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24 + 24, "Back"));
        this.username = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 100, 90, 200, 20);
        this.password = new PasswordField(this.mc.fontRendererObj, width / 2 - 100, 130, 200, 20);
        this.apikey = new PasswordField(this.mc.fontRendererObj, width / 2 - 100, 170, 200, 20);
        apikey.setText(((ApiKeySaver) Lime.fileManager.getFileByClass(ApiKeySaver.class)).load());
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!this.username.isFocused() && !this.password.isFocused()) {
                this.username.setFocused(true);
            } else {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        }
        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
        this.apikey.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
        this.apikey.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}

