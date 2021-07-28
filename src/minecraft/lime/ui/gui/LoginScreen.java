package lime.ui.gui;

import lime.core.Lime;
import lime.managers.FontManager;
import lime.ui.fields.ButtonField;
import lime.ui.fields.TextField;
import lime.utils.other.WebUtils;
import lime.utils.other.security.User;
import lime.utils.other.security.UserCheckThread;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.codec.digest.DigestUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;

public class LoginScreen extends GuiScreen {
    public LoginScreen() { }

    private final Animate lime = new Animate();
    private final Animate statusAnimation = new Animate();
    private final Animate loginRectAnimation = new Animate();

    private String status;

    private ButtonField logInButton;
    private TextField logInTextField;

    @Override
    public void initGui() {
        lime.setEase(Easing.CUBIC_OUT);
        loginRectAnimation.setEase(Easing.CUBIC_OUT);
        statusAnimation.setEase(Easing.CUBIC_OUT);
        lime.setSpeed(20);
        statusAnimation.setSpeed(15);
        loginRectAnimation.setSpeed(125);
        lime.setMin(0);
        statusAnimation.setMin(0);
        loginRectAnimation.setMin(0);
        lime.setMax(15);
        statusAnimation.setMax(23);
        loginRectAnimation.setMax(height / 2);
        lime.reset();
        statusAnimation.reset();
        loginRectAnimation.reset();
        status = "§7Waiting for connection...";

        logInTextField = new TextField(FontManager.ProductSans20.getFont(), "Username", width / 2 - 75, 3, 150, 20);

        this.customButtonList.add(logInButton = new ButtonField(FontManager.ProductSans20.getFont(), "Log In", width / 2 - 75, height / 2, 150, 20, new Color(23, 201, 115), () -> {
            new Thread() {
                @Override
                public void run() {
                    status = "§7Checking for debuggers...";
                    if(isOnVM() || hasDebugger() || hasUnauthorizedCacerts() || hasHTTPDebugger()) {
                        statusAnimation.reset();
                        System.out.println(isOnVM() + " " + hasDebugger() + " " + hasUnauthorizedCacerts() + " " + hasHTTPDebugger());
                        status = "§cDebugger detected.";
                        super.run();
                        return;
                    }
                    status = "§7Logging in...";
                    statusAnimation.reset();

                    char[] url = new char[44];
                    url[0] = 'h';
                    url[1] = 't';
                    url[2] = 't';
                    url[3] = 'p';
                    url[4] = ':';
                    url[5] = '/';
                    url[6] = '/';
                    url[7] = '5';
                    url[8] = '.';
                    url[9] = '1';
                    url[10] = '9';
                    url[11] = '6';
                    url[12] = '.';
                    url[13] = '2';
                    url[14] = '4';
                    url[15] = '3';
                    url[16] = '.';
                    url[17] = '4';
                    url[18] = '3';
                    url[19] = ':';
                    url[20] = '8';
                    url[21] = '0';
                    url[22] = '8';
                    url[23] = '0';
                    url[24] = '/';
                    url[25] = 'a';
                    url[26] = 'p';
                    url[27] = 'i';
                    url[28] = '/';
                    url[29] = 'c';
                    url[30] = 'h';
                    url[31] = 'e';
                    url[32] = 'c';
                    url[33] = 'k';
                    url[34] = 'h';
                    url[35] = 'w';
                    url[36] = 'i';
                    url[37] = 'd';
                    url[38] = '?';
                    url[39] = 'u';
                    url[40] = 's';
                    url[41] = 'e';
                    url[42] = 'r';
                    url[43] = '=';

                    char[] url2 = new char[6];
                    url2[0] = '&';
                    url2[1] = 'h';
                    url2[2] = 'w';
                    url2[3] = 'i';
                    url2[4] = 'd';
                    url2[5] = '=';

                    String s = "";
                    String s1 = "";

                    for (char c : url) {
                        s += c;
                    }

                    for (char c : url2) {
                        s1 += c;
                    }

                    try {
                        if(WebUtils.getSource(s + logInTextField.getText() + s1 + getHardwareID()).equalsIgnoreCase("true")) {
                            System.out.println("Username and Hardware ID match in user database, redirecting to main menu.");
                            status = "§aLogged in.";
                            Lime.getInstance().setUser(new User(logInTextField.getText(), getHardwareID()));
                            Lime.getInstance().setUserCheckThread(new UserCheckThread());
                            Lime.getInstance().getUserCheckThread().start();
                        } else {
                            status = "§cInvalid username or hardware ID.";
                        }
                        super.run();
                    } catch (IOException e) {
                        status = "§cWasn't able to make a request to auth server.";
                    }
                }
            }.start();
        }));

        this.customButtonList.add(logInButton = new ButtonField(FontManager.ProductSans20.getFont(), "Copy HWID", width / 2 - 75, height / 2 + 22, 150, 20, new Color(23, 201, 115), () -> {
            GuiScreen.setClipboardString(Minecraft.getHardwareID());
        }));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(Lime.getInstance().getUser() != null && Lime.getInstance().getUserCheckThread() != null && statusAnimation.getValue() == statusAnimation.getMax()) {
            mc.displayGuiScreen(new MainScreen());
            return;
        }
        lime.update();
        statusAnimation.update();
        loginRectAnimation.update();
        mc.getTextureManager().bindTexture(new ResourceLocation("lime/images/backgrounds/wp.jpg"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
        //Gui.drawRect(width / 2 - 75, loginRectAnimation.getValue() - 50, width / 2 + 75, loginRectAnimation.getValue() - 2, new Color(41, 41, 41).getRGB());
        FontManager.ProductSans20.getFont().drawStringWithShadow("Lime", this.width / 2F - (FontManager.ProductSans20.getFont().getStringWidth("Lime") / 2F), lime.getValue(), -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow(status, this.width / 2F - (FontManager.ProductSans20.getFont().getStringWidth(status) / 2F), statusAnimation.getValue(), -1);
        logInTextField.setY(loginRectAnimation.getValue() - 22);
        //logInButton.setY(loginRectAnimation.getValue() + 2);
        logInTextField.drawTextField(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton == 0) {
            this.logInTextField.mouseClicked();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.logInTextField.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
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
            if (launchArg.startsWith("-Xbootclasspath") || launchArg.startsWith("-Xdebug") || (launchArg.startsWith("-agentlib") && !launchArg.startsWith("-agentlib:jdwp=transport=dt_socket,address=")) || (launchArg.startsWith("-javaagent:") && !launchArg.equalsIgnoreCase("-javaagent:C:\\Users\\E\\AppData\\Local\\JetBrains\\IdeaIC2021.1\\captureAgent\\debugger-agent.jar"))
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


        File[] files = new File[] {new File("C:\\windows\\System32\\Drivers\\Vmmouse.sys"), new File("C:\\windows\\System32\\Drivers\\vm3dgl.dll"),
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
