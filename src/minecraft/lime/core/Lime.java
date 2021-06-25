package lime.core;

import lime.features.managers.CommandManager;
import lime.features.managers.ModuleManager;
import lime.features.setting.SettingsManager;
import lime.ui.clickgui.frame.ClickGUI;
import org.lwjgl.opengl.Display;
import viamcp.ViaFabric;

public class Lime {
    private final static Lime instance = new Lime();

    private SettingsManager settingsManager;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ClickGUI clickGUI;

    public void initClient() {
        System.out.println("Initialising Client");

        Display.setTitle("Lime");

        if(!new java.io.File("Lime").exists()) {
            new java.io.File("Lime").mkdir();
        }

        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        clickGUI = new ClickGUI();


        new EventListener();

        try {
            new ViaFabric().onInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if((new java.io.File("Lime" + java.io.File.separator + "modules.json").exists()))
            new lime.features.file.File().applyJson();

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
}
