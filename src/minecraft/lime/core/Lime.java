package lime.core;

import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.management.*;
import lime.ui.altmanager.AltManager;
import lime.ui.clickgui.frame.ClickGUI;
import lime.ui.gui.MainScreen;
import lime.ui.notifications.NotificationManager;
import lime.utils.other.file.FileSaver;
import lime.utils.other.security.CipherEncryption;
import lime.utils.other.security.User;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

import java.io.File;

public class Lime {
    private final static Lime instance = new Lime();

    private SettingManager settingsManager;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private TargetManager targetManager;
    private FriendManager friendManager;
    private NotificationManager notificationManager;
    private ClickGUI clickGUI;
    private lime.ui.clickgui.frame2.ClickGUI clickGUI2;
    private AltManager altManager;
    private User user;

    private FileSaver fileSaver;

    public boolean theAltening;

    public void initClient() {
        if(!CipherEncryption.passCheck) {
            return;
        }

        Display.setTitle("Lime");

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


        settingsManager = new SettingManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        notificationManager = new NotificationManager();
        targetManager = new TargetManager();
        friendManager = new FriendManager();
        fileSaver = new FileSaver();
        altManager = new AltManager();

        for (Module module : moduleManager.getModules()) {
            new BooleanProperty("Show", module, true);
        }

        if((new File("Lime" + java.io.File.separator + "modules.json").exists()))
            fileSaver.applyJson("Lime" + java.io.File.separator + "modules.json", true);

        clickGUI = new ClickGUI();
        clickGUI2 = new lime.ui.clickgui.frame2.ClickGUI();

        new EventListener();

        Minecraft.getMinecraft().displayGuiScreen(new MainScreen());

        logger.info("[LIME] Started.");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public SettingManager getSettingsManager() {
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

    public TargetManager getTargetManager() {
        return targetManager;
    }

    public static Lime getInstance() {
        return instance;
    }

    public FileSaver getFileSaver() {
        return fileSaver;
    }
}
