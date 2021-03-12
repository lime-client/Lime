package lime.gui;

import lime.Lime;
import lime.ui.GuiNewButton;
import lime.utils.render.Util2D;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class LoginMenu extends GuiScreen {
    GuiTextField user;
    @Override
    public void initGui() {
        user = new GuiTextField(69, mc.fontRendererObj, this.width / 2 - 50 + 1, this.height / 2 - 30, 101, 20);
        this.buttonList.add(new GuiNewButton(6969, this.width / 2 - 50 + 1, this.height / 2 - 6, 101, 20, "Login"));
        super.initGui();
    }
    boolean connected = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Util2D.drawImage(new ResourceLocation("lime/wp.jpg"), 0, 0, this.width, this.height);
        Util2D.drawRoundedRect(this.width / 2 - 60, this.height / 2 - 80, 120, 100, 5, new Color(50, 50, 50).getRGB(), false);
        if(!connected)
            Util2D.drawImage(new ResourceLocation("lime/logging.png"), this.width / 2 - 18, this.height / 2 - 80 + 2, 36, 36);
        else
            Util2D.drawImage(new ResourceLocation("lime/done.png"), this.width / 2 - 18, this.height / 2 - 80 + 2, 36, 36);
        user.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        user.textboxKeyTyped(typedChar, keyCode);
        if(keyCode == 1)
            return;
        if(keyCode == Keyboard.KEY_EQUALS && user.isFocused()){
            actionPerformed(buttonList.get(0));
        }
        super.keyTyped(typedChar, keyCode);
    }

    public static String zPJfe2Ck3279F4ptCL7u7A32W3G9xb32gSeiRT7mmFgNR(String BLviCHHy76v5Ch39PB3hpcX7W2qe45YaBPQyn285Dcg27) throws IOException
    {
        URL urlObject = new URL(BLviCHHy76v5Ch39PB3hpcX7W2qe45YaBPQyn285Dcg27);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        return AP2iKAwcS2gFL8cX8z944ZiJp2zS54T68Tp39nr2rJAwh(urlConnection.getInputStream());
    }

    private static String AP2iKAwcS2gFL8cX8z944ZiJp2zS54T68Tp39nr2rJAwh(InputStream L58C336iNBkwz86u4QV3HcDJ94i34gWv4gpzbqBC5ZCdG) throws IOException
    {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(L58C336iNBkwz86u4QV3HcDJ94i34gWv4gpzbqBC5ZCdG, "UTF-8")))
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
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 6969){
            Process tl = Runtime.getRuntime().exec("tasklist.exe");
            InputStream is = tl.getInputStream();
            String text = IOUtils.toString(is, StandardCharsets.UTF_8);
            if(text.toLowerCase().contains("debugger")) System.exit(0);
            String a = zPJfe2Ck3279F4ptCL7u7A32W3G9xb32gSeiRT7mmFgNR("http://149.202.251.229:8080/api/checkhwid?user=" + user.getText() + "&hwid=" + limehwid());
            if(a.contains("true")){
                mc.displayGuiScreen(new MainMenu());
                Lime.logged = true;
            }

        }
        super.actionPerformed(button);
    }

    public static String limehwid() {
        try {
            String string = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            StringBuilder stringBuilder = new StringBuilder();
            messageDigest.update(string.getBytes());
            for (byte by : messageDigest.digest()) {
                String string2 = Integer.toHexString(0xFF & by);
                if (string2.length() == 1) {
                    stringBuilder.append('0');
                }
                stringBuilder.append(string2);
            }
            return Base64.getEncoder().encodeToString(stringBuilder.toString().getBytes());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return "fail";
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        user.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
