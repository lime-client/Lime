package lime.ui.gui;

import lime.core.Lime;
import lime.managers.FontManager;
import lime.ui.fields.ButtonField;
import lime.ui.fields.TextField;
import lime.utils.other.ChatUtils;
import lime.utils.other.Timer;
import lime.utils.other.WebUtils;
import lime.utils.other.security.CipherEncryption;
import lime.utils.other.security.User;
import lime.utils.other.security.UserCheckThread;
import lime.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class LoginScreen extends GuiScreen {

    private long initTime;
    private TextField textField;

    private final CipherEncryption cipherEncryption = new CipherEncryption("XR88T4T3uVRkbH92BzJsYPdHYQD7xsQf88338uAnK36hGGPL274bdRADMYfvJTj7XXV32w62K95y9pW2nNL3ntRY3Hf4dkWD6Ng39tpWzg2f2Y2WV5XjVgjHH68j34HtKR42vtGEZMnu2K49akFeB5gittC49Kv7aYKqM6CNQW668KYah3CdQig4azmLEw3qV72m87S33443gnghWJd5xF44AuC24BwuaFh25fvJ8SABx28fZwDg843574Yc4NSbSbT9A5FZmf37e87k56qnE9PBmffQ9D5fWznEyk3pi2H78CnXfhE76YSn3tr5bv783f2efGWcR3x85A2TSAF9vX2Qxikn96g9QzXhi76ffWJp3k884p3457xrkevDHc6WiYyQewAUh5nU4vsPySm7DRBk29SRb59K43WN482Gq86xR9G5inb37vUs878CcK9fqsr53iBJ83ZsM2CBS3n3fm6s255RV8BWhn2WyWTs22s65TiRzep854N8M5S9vM5DJ8zaiQpgauewFwaF64Qh6m2g24z5S2WCZEeGwf7aeb5mJyQ8939HTf2RCSX9576S9r4KZ8ujV7R2CkJxm9D9KmLu86C78JTx8jRF2WN8TF2zmXH622W6T69Dp2DPY8PfP74XMEKmHi4P5uf37Svrg7v9j7dg2GtQw2J8Da76x3Sb6zg7jV9JdrArJZYQCSuifH762H76PJi5C98sGpm6S7dv9AXk9M4633wGiG2nu96rA27rufd5QN4Z5s36RyStq572rPj8XJq63DK6fjBH57dgp58869FdWWRAg8qB2zVgXnM52f6sm7E94Kw8a4tSQq2pn39JN6838ZgDUZ3j5D7cDrsRW6Nqwcp588dm7e6AWNL3Tf6Za5Rw367msQyc7R63qM96Piywgnv9ZH79La5Jg952FmER3hyiTv42qZ7ruaTetwnn27LE3M5drcA9Nqq6J9j33A8Gk9quJgFE495mVndMAK66LV5Qte3mTnZQCPPjSF2PzA4naWrLD9u9PbK5LdrszMfUWQdk5wME7464gHFtG6q5Rn7r26ufXmZs47phT7r7f99wrVTVmn7KYYbe4d29pF5ERcUY3nL268xbBMa4SUL4jkRVuW8t27a646VpqqyKPyFmGr59tVU9T4G97yH3x5KD9VZ9T2Q42Ld9i5wd3FZwUgsuEK9d6L6KamZFygDDz6E395fJ2H3p9AZF6V636vx8E9V9BcUFP53VvaA9VemXK9jv5M6598v767763F6A6mj35g7rk926EzUT4T7vtkHxpji4QzJaFq7gj8U88meeU6Y4HLpHxmR93MxVrtiknPFrKzA2b4454p56ebbPy4Gekg25c753KQNS5ru87YZA2pu65UeR56DFQh22XcqDkkA7nYNDw4C3VzkmSxV4cwkW2H98NAyF62Y7aq3ZB4vEN8hb4kEPPUdDGy8E28e48uD3222Ckqp658wH5z9gL4v6qWxS48wh6LaKG3dc8u9B29mV897D5MMyK96qETHtU3UsUp7Be6Wr739ySBKa2U92mb79U7464MN5AYahJ7wYM8ikrY7Sy4A69r9YN4N4adQ7qe3H5p9DPF7a2327CmFk7am4sAJxt9b5j3Qn4d36uQDR7N66NJi52PkqhT2X49vPfeDnnQQm47BsCerBq8eWz9HYQ8hm85eQrf698dQuUv2gyrndTA3JSfpd2gs3LcAq69V5Y9B469N9a57V6GkGaht34VPLW5G57TM7fjtfzwQ224PC2p3NqHkew95acLnxsf54DUaaWBqwnBNqCHYmAgaN5MEHq63d2Dy8JuJUW2Cr3997F35CTt8867N8cG8Qda7tp6ZwhN386SMX6n8vAY7Ns6y62Qic5BFEwV85Xni3DLKss7V4Gy2R946emn2g22L3RbJ69mzbwHuFiHd3NpQ2cCNELC9Xq426V2874NAE6F2P2bKz3xQSf3jNa8ycN3p392LfiUi44KH9Q3qYz2n4SNQfESP9YVgN6q5vrL265strcL4XA68rEapwzCtd2mS3n23dh2CL2K72Bx3bYERJ947DvqG96NTp7C5C47LQp27d39J3zgAst7pt23282ra385yw548qhDn47zs93tFT7naeL6FLquN8986225m255z42CbV8vR45VcyVRPGM3dMXr435Q76thDSg6m7z5pacDPZKA7G3d4WecZUhS53zR9LM338pJ8g7Qas7NAKDwBcQ3tV3yGd9jK8a7Rr5Y4jxey29xzau2utS7HQphK2xg4392KA4ksBNK2zr3LQ4N9h7Xmv8Gw42M7BN2rWj2N7y68fxG75MtJZDTdn3R8YS2TWwns5Wm7S3jLx2vGK3HJ7aaTq34UnfPV7xpsKW978Xcn6CWmZ2AU6DE9ngXSY4ra3GvMWE6582pdX5sqAs2ucPTU5UeiSUZ3xqrq2BfwVJ3T5v66SSnYGLFm462u863QEcKeaWJJWkr6y646gZ6jreu46e4V7VucVi6Npd25nteA4jjDEHvE4mx729UpaffRn3Hj5GuCC82uk2QfS65VAhZ9n");
    private String status;

    private final Timer timer = new Timer();

    @Override
    public void initGui() {
        textField = new TextField(FontManager.ProductSans20.getFont(), "UID", this.width / 2F - 75, this.height / 2F - 10, 150, 20);
        initTime = System.currentTimeMillis();
        status = "§7Waiting for connection";
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Log in", this.width / 2F - 75, this.height / 2F + 12, 150, 20, new Color(90, 24, 184, 255), () -> {
            try {
                new Thread(() -> {
                    try {
                        status = "§7Logging in...";
                        if(hasDebugger() || hasHTTPDebugger() || hasUnauthorizedCacerts() || isOnVM() || Lime.getInstance().getUserCheckThread() != null) {
                            status = "§cDebugger detected.";
                            return;
                        }
                        char[] c = new char[43];
                        c[0] = 'h';
                        c[1] = 't';
                        c[2] = 't';
                        c[3] = 'p';
                        c[4] = ':';
                        c[5] = '/';
                        c[6] = '/';
                        c[7] = '1';
                        c[8] = '0';
                        c[9] = '8';
                        c[10] = '.';
                        c[11] = '6';
                        c[12] = '1';
                        c[13] = '.';
                        c[14] = '2';
                        c[15] = '1';
                        c[16] = '0';
                        c[17] = '.';
                        c[18] = '1';
                        c[19] = '1';
                        c[20] = '5';
                        c[21] = ':';
                        c[22] = '8';
                        c[23] = '0';
                        c[24] = '0';
                        c[25] = '0';
                        c[26] = '/';
                        c[27] = 'a';
                        c[28] = 'p';
                        c[29] = 'i';
                        c[30] = '/';
                        c[31] = 'i';
                        c[32] = 's';
                        c[33] = 'V';
                        c[34] = 'a';
                        c[35] = 'l';
                        c[36] = 'i';
                        c[37] = 'd';
                        c[38] = '?';
                        c[39] = 'u';
                        c[40] = 'i';
                        c[41] = 'd';
                        c[42] = '=';


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

                        String key = new Random().nextInt(Integer.MAX_VALUE) +"";

                        final String response = WebUtils.getSource(s + cipherEncryption.encrypt(userName) + s1 + cipherEncryption.encrypt(Minecraft.getHardwareID()) + "&key=" + cipherEncryption.encrypt(key));

                        String uid = cipherEncryption.decrypt(response).split(":")[0];
                        String hwid = cipherEncryption.decrypt(response).split(":")[1];
                        String time = cipherEncryption.decrypt(response).split(":")[2];
                        String valid = cipherEncryption.decrypt(response).split(":")[3];

                        if(uid.equals(textField.getText()) && hwid.equals(Minecraft.getHardwareID()) && time.equals(key) && valid.contains("true")) {
                            status = valid.contains("-1") ? "§aSuccessfully reset HWID. Logged in" : "§aLogged in.";
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
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 13) {
            this.customButtonList.get(0).mouseClicked();
        } else if(keyCode == 1) {
            return;
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
            if(s.equalsIgnoreCase("HTTPDebuggerSvc.exe") || s.contains("cheatengine") || s.equalsIgnoreCase("Fiddler.exe") || s.toLowerCase().contains("wireshark")) {
                return true;
            }
        }

        return false;
    }

    private boolean hasDebugger() {
        List<String> launchArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String launchArg : launchArgs) {
            if (launchArg.startsWith("-Xbootclasspath") || launchArg.startsWith("-Xdebug") || (launchArg.startsWith("-agentlib") && !launchArg.startsWith("-agentlib:jdwp=transport=dt_socket,address=")) || (launchArg.startsWith("-javaagent:") && !launchArg.equalsIgnoreCase("-javaagent:C:\\Users\\Chine\\AppData\\Local\\JetBrains\\IdeaIC2021.2\\captureAgent\\debugger-agent.jar"))
                    || launchArg.startsWith("-Xrunjdwp:") || launchArg.startsWith("-verbose") || launchArg.startsWith("-Dhttp.proxy") || launchArg.contains("proxy") || launchArg.contains("http")) {
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
