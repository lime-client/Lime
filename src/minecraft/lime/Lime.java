package lime;

import lime.altmanager.AltManager;
import lime.cgui.ClickGui;
import lime.cgui.cgui2.ClickGui2;
import lime.cgui.settings.SettingsManager;
import lime.events.EventManager;
import lime.events.EventTarget;
import lime.events.impl.EventKey;
import lime.events.impl.EventUpdate;
import lime.file.impl.ModuleSaver;
import lime.file.impl.SettingsSaver;
import lime.gui.LoginMenu;
import lime.managers.CommandManager;
import lime.managers.FileManager;
import lime.managers.FontManager;
import lime.managers.ModuleManager;
import lime.utils.Timer;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;
import viamcp.ViaFabric;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Lime {
    public static int deltaTime = 0;
    public static String version = "b1", clientName = "Lime";
    public static EventManager eventManager;
    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static FileManager fileManager;
    public static FontManager fontManager;
    public static SettingsManager setmgr;
    public static AltManager altManager;
    public static ClickGui clickgui;
    public static ClickGui2 clickgui2;
    public static boolean logged = false;
    public static Lime instance = new Lime();
    public void startClient(){
        Display.setTitle("Lime " + version);
        eventManager.register(this);
        initInstance();

    }
    public static void initInstance(){
        if(logged) return;
        if(Lime.logged && Minecraft.getMinecraft().currentScreen instanceof LoginMenu) System.exit(0);
        if(!Lime.logged && !(Minecraft.getMinecraft().currentScreen instanceof LoginMenu)){
            System.exit(0);
        }
        try{
            File file = new File("Lime");
            if(!file.exists()) file.mkdir();
        } catch (Exception ignored){

        }
        try{
            File file = new File("Lime" + File.separator + "configs");
            if(!file.exists()) file.mkdir();
        } catch (Exception ignored){

        }
        fontManager = new FontManager();
        setmgr = new SettingsManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        fileManager = new FileManager();
        clickgui = new ClickGui(new ArrayList<>(moduleManager.getModules()));
        ((ModuleSaver) fileManager.getFileByClass(ModuleSaver.class)).load();
        ((SettingsSaver) fileManager.getFileByClass(SettingsSaver.class)).load();
        clickgui2 = new ClickGui2();
        loadViaMCP();
        altManager = new AltManager();
        //

    }
    public static void stopClient(){
        ((ModuleSaver) fileManager.getFileByClass(ModuleSaver.class)).save();
        ((SettingsSaver) fileManager.getFileByClass(SettingsSaver.class)).save();
    }
    public static void loadViaMCP() {
        try {
            new ViaFabric().onInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @EventTarget
    public void onKey(EventKey event) {
        moduleManager.getModules().stream().filter(module -> module.getKey() == event.key).forEach(module -> module.toggle());
    }
    @EventTarget
    public void onUpdate(EventUpdate e){
        Minecraft.getMinecraft().playerController.updateController();
    }
}
