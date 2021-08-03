package lime.ui.gui;

import lime.core.Lime;
import lime.features.module.Category;
import lime.features.module.ModuleData;
import lime.managers.FontManager;
import lime.ui.fields.ButtonField;
import lime.ui.fields.TextField;
import lime.utils.other.ChatUtils;
import lime.utils.other.Timer;
import lime.utils.other.WebUtils;
import lime.utils.other.security.User;
import lime.utils.other.security.UserCheckThread;
import lime.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@ModuleData(name = "ClickGUI", category = Category.RENDER)
public class LoginScreen extends GuiScreen {

    private long initTime;
    private TextField textField;

    private String status;

    private final Timer timer = new Timer();

    @Override
    public void initGui() {
        textField = new TextField(FontManager.ProductSans20.getFont(), "UID", this.width / 2F - 75, this.height / 2F - 10, 150, 20);
        initTime = System.currentTimeMillis();
        status = "§7Waiting for connection";
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Login", this.width / 2F - 75, this.height / 2F + 12, 150, 20, new Color(90, 24, 184, 255), () -> {
            try {
                new Thread(() -> {
                    try {
                        status = "§7Logging in...";
                        if(hasDebugger() || hasHTTPDebugger() || hasUnauthorizedCacerts() || isOnVM()) {
                            status = "§cDebugger detected.";
                            return;
                        }
                        char[] c = new char[41];
                        c[0] = 'h';
                        c[1] = 't';
                        c[2] = 't';
                        c[3] = 'p';
                        c[4] = ':';
                        c[5] = '/';
                        c[6] = '/';
                        c[7] = '5';
                        c[8] = '.';
                        c[9] = '1';
                        c[10] = '9';
                        c[11] = '6';
                        c[12] = '.';
                        c[13] = '2';
                        c[14] = '4';
                        c[15] = '3';
                        c[16] = '.';
                        c[17] = '4';
                        c[18] = '3';
                        c[19] = ':';
                        c[20] = '8';
                        c[21] = '0';
                        c[22] = '0';
                        c[23] = '0';
                        c[24] = '/';
                        c[25] = 'a';
                        c[26] = 'p';
                        c[27] = 'i';
                        c[28] = '/';
                        c[29] = 'i';
                        c[30] = 's';
                        c[31] = 'V';
                        c[32] = 'a';
                        c[33] = 'l';
                        c[34] = 'i';
                        c[35] = 'd';
                        c[36] = '?';
                        c[37] = 'u';
                        c[38] = 'i';
                        c[39] = 'd';
                        c[40] = '=';

                        char[] c1 = new char[6];
                        c1[0] = '&';
                        c1[1] = 'h';
                        c1[2] = 'w';
                        c1[3] = 'i';
                        c1[4] = 'd';
                        c1[5] = '=';

                        String s = "", s1 = "";
                        for (char char_ : c) {
                            s += char_;
                        }
                        for(char char_ : c1) {
                            s1 += char_;
                        }

                        String userName = textField.getText();
                        while(userName.startsWith("0")) {
                            userName = userName.substring(1);
                        }
                        final String response = WebUtils.getSource(s + userName + s1 + Minecraft.getHardwareID());

                        if(response.contains("true")) {
                            status = response.contains("-1") ? "§aSuccessfully reset HWID. Logged in" : "§aLogged in.";
                            Lime.getInstance().setUserCheckThread(new UserCheckThread(new User(userName, Minecraft.getHardwareID())));
                            Lime.getInstance().getUserCheckThread().start();
                            timer.reset();
                        } else {
                            status = "§cInvalid UID or HWID.";
                        }
                    } catch (Exception ignored) {
                        try {
                            Field field = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
                            field.setAccessible(true);
                            Object unsafe = field.get(null);
                            unsafe.getClass().getDeclaredMethod("getByte", long.class).invoke(unsafe, 0);
                        } catch (Exception ignored1){}
                    }
                }).start();
            } catch (Exception e) {
                try {
                    Field field = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    Object unsafe = field.get(null);
                    unsafe.getClass().getDeclaredMethod("getByte", long.class).invoke(unsafe, 0);
                } catch (Exception ignored) {
                }
            }
        }));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
        Gui.drawRect(this.width / 2F - 80, this.height / 2F - 15, this.width / 2F + 80,  this.height / 2F + 50, new Color(25, 25, 25, 225).getRGB());
        RenderUtils.drawHollowBox(this.width / 2F - 80, this.height / 2F - 15, this.width / 2F + 80,  this.height / 2F + 50, 0.5f, new Color(90, 24, 184).getRGB());
        textField.drawTextField(mouseX, mouseY);
        FontManager.ProductSans20.getFont().drawStringWithShadow(status, this.width / 2F - (FontManager.ProductSans20.getFont().getStringWidth(ChatUtils.removeColors(status)) / 2F), this.height / 2F + 34, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(Lime.getInstance().getUserCheckThread() != null && timer.hasReached(2500)) {
            Display.setTitle("Lime");
            mc.displayGuiScreen(new MainScreen());
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 13) {
            this.customButtonList.get(0).mouseClicked();
        }
        textField.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textField.mouseClicked();
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean hasUnauthorizedCacerts() {
        File file = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "cacerts");
        return getMD5(file).equalsIgnoreCase("4a4ae67681255735cec94a81534e9950") || getMD5(file).equalsIgnoreCase("b40b81544993ba86a858d579a06b3ef2");
    }

    private boolean hasHTTPDebugger() {
        if(Util.getOSType() != Util.EnumOS.WINDOWS) return false;
        List<String> taskList = getTaskList();

        for (String s : taskList) {
            if(s.equalsIgnoreCase("HTTPDebuggerSvc.exe") || s.equalsIgnoreCase("Fiddler.exe") || s.toLowerCase().contains("wireshark")) {
                return true;
            }
        }

        return false;
    }

    private boolean hasDebugger() {
        List<String> launchArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String launchArg : launchArgs) {
            if (launchArg.startsWith("-Xbootclasspath") || launchArg.startsWith("-Xdebug") || (launchArg.startsWith("-agentlib") && !launchArg.startsWith("-agentlib:jdwp=transport=dt_socket,address=")) || (launchArg.startsWith("-javaagent:") && !launchArg.equalsIgnoreCase("-javaagent:C:\\Users\\E\\AppData\\Local\\JetBrains\\IdeaIC2021.2\\captureAgent\\debugger-agent.jar"))
                    || launchArg.startsWith("-Xrunjdwp:") || launchArg.startsWith("-verbose")) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnVM() {
        if(Util.getOSType() == Util.EnumOS.WINDOWS) {
            ArrayList<String> taskList = getTaskList();

            for (String s : taskList) {
                if(s.equalsIgnoreCase("vmtoolsd.exe") || s.equalsIgnoreCase("vmwaretrat.exe") || s.equalsIgnoreCase("vmwareuser.exe") ||
                        s.equalsIgnoreCase("vmacthlp.exe") || s.equalsIgnoreCase("vboxservice.exe") || s.equalsIgnoreCase("vboxtray.exe")) {
                    return true;
                }
            }
        }


        File[] files = new File[] {new File("C:\\windows\\System32\\Drivers\\vm3dgl.dll"),
                new File("C:\\windows\\System32\\Drivers\\vmdum.dll"), new File("C:\\windows\\System32\\Drivers\\vm3dver.dll"),
                new File("C:\\windows\\System32\\Drivers\\vmtray.dll"), new File("C:\\windows\\System32\\Drivers\\VMToolsHook.dll"),
                new File("C:\\windows\\System32\\Drivers\\vmmousever.dll"), new File("C:\\windows\\System32\\Drivers\\vmhgfs.dll"),
                new File("C:\\windows\\System32\\Drivers\\vmGuestLib.dll"), new File("C:\\windows\\System32\\Drivers\\VmGuestLibJava.dll"),
                new File("C:\\windows\\System32\\Drivers\\VBoxMouse.sys"),
                new File("C:\\windows\\System32\\Drivers\\VBoxGuest.sys"), new File("C:\\windows\\System32\\Drivers\\VBoxSF.sys"),
                new File("C:\\windows\\System32\\Drivers\\VBoxVideo.sys"), new File("C:\\windows\\System32\\vboxdisp.dll"),
                new File("C:\\windows\\System32\\vboxhook.dll"), new File("C:\\windows\\System32\\vboxmrxnp.dll"),
                new File("C:\\windows\\System32\\vboxogl.dll"), new File("C:\\windows\\System32\\vboxoglarrayspu.dll"),
                new File("C:\\windows\\System32\\vboxoglcrutil.dll"), new File("C:\\windows\\System32\\vboxoglerrorspu.dll"),
                new File("C:\\windows\\System32\\vboxoglfeedbackspu.dll"), new File("C:\\windows\\System32\\vboxoglpackspu.dll"),
                new File("C:\\windows\\System32\\vboxoglpassthroughspu.dll"), new File("C:\\windows\\System32\\vboxservice.exe"),
                new File("C:\\windows\\System32\\vboxtray.exe"), new File("C:\\windows\\System32\\VBoxControl.exe")};

        for (File file : files) {
            if(file.exists()) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> getTaskList() {
        ArrayList<String> list = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
            new Thread(() -> {
                Scanner sc = new Scanner(process.getInputStream());
                if (sc.hasNextLine()) sc.nextLine();
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(",");
                    String unq = parts[0].substring(1).replaceFirst(".$", "");
                    list.add(unq.toLowerCase());
                }
            }).start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String getMD5(File file) {
        try {
            InputStream is = Files.newInputStream(Paths.get(file.toURI()));
            return DigestUtils.md5Hex(is);
        } catch (Exception e) {
            return "";
        }
    }
}
