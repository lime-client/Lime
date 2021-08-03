package lime.core;

import lime.scripting.ScriptManager;
import lime.ui.gui.LoginScreen;
import lime.ui.gui.MainScreen;
import lime.ui.notifications.utils.NotificationManager;
import lime.utils.other.file.FileSaver;
import lime.managers.CommandManager;
import lime.managers.ModuleManager;
import lime.features.setting.SettingsManager;
import lime.ui.clickgui.frame.ClickGUI;
import lime.utils.other.security.UserCheckThread;
import lime.utils.render.GLSLSandboxShader;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import sun.misc.Unsafe;
import viamcp.ViaMCP;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.logging.Level;

public class Lime {
    private final static Lime instance = new Lime();

    private SettingsManager settingsManager;
    private ModuleManager moduleManager;
    private ScriptManager scriptManager;
    private CommandManager commandManager;
    private NotificationManager notificationManager;
    private ClickGUI clickGUI;
    private lime.ui.clickgui.frame2.ClickGUI clickGUI2;

    private UserCheckThread userCheckThread;

    private FileSaver fileSaver;

    private Proxy proxy = Proxy.NO_PROXY;

    public boolean theAltening;

    public void initClient() {
        if(this.userCheckThread == null) return;
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
        fileSaver = new FileSaver();

        if((new File("Lime" + java.io.File.separator + "modules.json").exists()))
            fileSaver.applyJson("Lime" + java.io.File.separator + "modules.json", true);

        clickGUI = new ClickGUI();
        clickGUI2 = new lime.ui.clickgui.frame2.ClickGUI();

        this.scriptManager = new ScriptManager().loadScripts();

        new EventListener();

        logger.info("[LIME] Client initialised.");
    }

    public CommandManager getCommandManager() {
        return commandManager;
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
