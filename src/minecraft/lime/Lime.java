package lime;

import lime.altmanager.AltManager;
import lime.cgui.ClickGui;
import lime.cgui.cgui2.ClickGui2;
import lime.cgui.settings.SettingsManager;
import lime.events.EventManager;
import lime.events.EventTarget;
import lime.events.impl.EventKey;
import lime.events.impl.EventUpdate;
import lime.managers.FontManager;
import lime.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import viamcp.ViaFabric;

import java.awt.*;
import java.net.MalformedURLException;

public class Lime {
    public static int deltaTime = 0;
    public static String version = "b1", clientName = "Lime";
    public static EventManager eventManager;
    public static ModuleManager moduleManager;
    public static FontManager fontManager;
    public static SettingsManager setmgr;
    public static AltManager altManager;
    public static ClickGui clickgui;
    public static ClickGui2 clickgui2;
    public static Lime instance = new Lime();
    public void startClient(){
        Display.setTitle("Lime " + version);
        eventManager.register(this);
        initInstance();

    }
    public static void initInstance(){
        fontManager = new FontManager();
        setmgr = new SettingsManager();
        moduleManager = new ModuleManager();
        clickgui = new ClickGui();
        clickgui2 = new ClickGui2();
        loadViaMCP();
        altManager = new AltManager();
        //

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
