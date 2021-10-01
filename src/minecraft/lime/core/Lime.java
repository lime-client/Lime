package lime.core;

import lime.features.module.Module;
import lime.features.setting.SettingsManager;
import lime.features.setting.impl.BoolValue;
import lime.managers.CommandManager;
import lime.managers.FriendManager;
import lime.managers.ModuleManager;
import lime.scripting.ScriptManager;
import lime.ui.clickgui.frame.ClickGUI;
import lime.ui.gui.MainScreen;
import lime.ui.notifications.NotificationManager;
import lime.ui.realaltmanager.AltManager;
import lime.utils.other.file.FileSaver;
import lime.utils.other.security.CipherEncryption;
import lime.utils.other.security.UserCheckThread;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import viamcp.ViaMCP;

import java.io.File;
import java.net.Proxy;

public class Lime {
    private final static Lime instance = new Lime();

    private SettingsManager settingsManager;
    private ModuleManager moduleManager;
    private ScriptManager scriptManager;
    private CommandManager commandManager;
    private FriendManager friendManager;
    private NotificationManager notificationManager;
    private ClickGUI clickGUI;
    private lime.ui.clickgui.frame2.ClickGUI clickGUI2;
    private AltManager altManager;

    private UserCheckThread userCheckThread;

    private FileSaver fileSaver;

    private Proxy proxy = Proxy.NO_PROXY;

    public boolean theAltening;

    public void initClient() {
        if(this.userCheckThread == null || !CipherEncryption.passCheck) {
            System.out.println("e");
            return;
        }

        Logger logger = LogManager.getLogger("Lime");

        logger.info("[LIME] Starting client");

        ViaMCP.getInstance().start();

        File limeFile = new File("Lime");
        File limeConfigFile = new File("Lime" + File.separator + "configs");
        File limeScriptsFile = new File("Lime" + File.separator + "scripts");

        if(!limeFile.exists())
            limeFile.mkdir();

        if(!limeConfigFile.exists())
            limeConfigFile.mkdir();

        if(!limeScriptsFile.exists())
            limeScriptsFile.mkdir();


        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        notificationManager = new NotificationManager();
        friendManager = new FriendManager();
        fileSaver = new FileSaver();
        altManager = new AltManager();

        for (Module module : moduleManager.getModules()) {
            new BoolValue("Show", module, true);
        }

        if((new File("Lime" + java.io.File.separator + "modules.json").exists()))
            fileSaver.applyJson("Lime" + java.io.File.separator + "modules.json", true);

        clickGUI = new ClickGUI();
        clickGUI2 = new lime.ui.clickgui.frame2.ClickGUI();

        this.scriptManager = new ScriptManager().loadScripts();

        new EventListener();

        Minecraft.getMinecraft().displayGuiScreen(new MainScreen());

        logger.info("[LIME] Client initialised.");
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AltManager getAltManager() {
        return altManager;
    }

    public ClickGUI getClickGUI() {
        return clickGUI;
    }

    public lime.ui.clickgui.frame2.ClickGUI getClickGUI2() {
        return clickGUI2;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    public static Lime getInstance() {
        return instance;
    }

    public FileSaver getFileSaver() {
        return fileSaver;
    }

    public UserCheckThread getUserCheckThread() {
        return userCheckThread;
    }

    public void setUserCheckThread(UserCheckThread userCheckThread) {
        this.userCheckThread = userCheckThread;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public void setClickGUI(ClickGUI clickGUI) {
        this.clickGUI = clickGUI;
    }

    public void setClickGUI2(lime.ui.clickgui.frame2.ClickGUI clickGUI2) {
        this.clickGUI2 = clickGUI2;
    }
}
