package lime.core;

import lime.features.file.FileSaver;
import lime.features.managers.CommandManager;
import lime.features.managers.ModuleManager;
import lime.features.setting.SettingsManager;
import lime.ui.clickgui.frame.ClickGUI;
import lime.ui.gui.LoginScreen;
import lime.utils.other.security.User;
import lime.utils.other.security.UserCheckThread;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import viamcp.ViaFabric;

import java.io.File;

public class Lime {
    private final static Lime instance = new Lime();

    private SettingsManager settingsManager;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ClickGUI clickGUI;

    private UserCheckThread userCheckThread;
    private FileSaver fileSaver;
    private User user;

    public void initClient() {
        System.out.println("Initialising Client");

        Minecraft.getMinecraft().displayGuiScreen(new LoginScreen());

        Display.setTitle("Lime");

        File limeFile = new File("Lime");
        File limeConfigFile = new File("Lime" + File.separator + "configs");

        if(!limeFile.exists())
            limeFile.mkdir();

        if(!limeConfigFile.exists())
            limeConfigFile.mkdir();


        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        fileSaver = new FileSaver();
        clickGUI = new ClickGUI();


        new EventListener();

        try {
            new ViaFabric().onInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if((new File("Lime" + java.io.File.separator + "modules.json").exists()))
            fileSaver.applyJson("Lime" + java.io.File.separator + "modules.json");

        System.out.println("Client started");
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ClickGUI getClickGUI() {
        return clickGUI;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static Lime getInstance() {
        return instance;
    }

    public FileSaver getFileSaver() {
        return fileSaver;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public UserCheckThread getUserCheckThread() {
        return userCheckThread;
    }

    public void setUserCheckThread(UserCheckThread userCheckThread) {
        this.userCheckThread = userCheckThread;
    }
}
