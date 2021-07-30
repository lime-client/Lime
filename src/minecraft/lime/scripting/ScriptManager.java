package lime.scripting;

import jdk.nashorn.api.scripting.AbstractJSObject;
import lime.core.Lime;
import lime.core.events.EventBus;
import lime.features.module.Category;
import lime.scripting.api.ClientWrapper;
import lime.scripting.api.ModuleWrapper;
import lime.scripting.api.PlayerWrapper;
import lime.ui.clickgui.frame.ClickGUI;
import lime.utils.other.ChatUtils;
import net.minecraft.client.Minecraft;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class ScriptManager {
    private ScriptEngine scriptEngine;
    private final ArrayList<ScriptModule> scriptModules;

    private final ClientWrapper clientWrapper;
    private final PlayerWrapper playerWrapper;

    public ScriptManager()
    {
        scriptModules = new ArrayList<>();
        scriptEngine = new ScriptEngineManager().getEngineByName("ecmascript");

        clientWrapper = new ClientWrapper();
        playerWrapper = new PlayerWrapper();
    }

    public ScriptManager loadScripts()
    {
        scriptEngine = new ScriptEngineManager().getEngineByName("ecmascript");
        for (ScriptModule scriptModule : scriptModules) {
            EventBus.INSTANCE.unregister(scriptModule);
            Lime.getInstance().getModuleManager().getModules().remove(scriptModule);
            Lime.getInstance().getSettingsManager().getHashMapSettings().remove(scriptModule);
        }
        scriptModules.clear();

        scriptEngine.put("registerModule", new AbstractJSObject() {
            @Override
            public Object call(Object thiz, Object... args) {
                ScriptModule scriptModule = new ScriptModule((String) args[0], (int) args[1], Category.SCRIPT);
                scriptModules.add(scriptModule);
                Lime.getInstance().getModuleManager().getModules().add(scriptModule);
                return new ModuleWrapper(scriptModule);
            }
        });
        scriptEngine.put("client", clientWrapper);
        scriptEngine.put("player", playerWrapper);

        File scriptsFolder = new File(Minecraft.getMinecraft().mcDataDir, "Lime/scripts");

        for (File file : Objects.requireNonNull(scriptsFolder.listFiles())) {
            if(file.getName().contains(".js")) {
                try {
                    scriptEngine.eval(new InputStreamReader(new FileInputStream(new File(Minecraft.getMinecraft().mcDataDir, "Lime/scripts/" + file.getName()))));
                } catch (Exception e) {
                    if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null)
                    {
                        ChatUtils.sendMessage(e.getMessage());
                    }
                }
            }
        }
        Lime.getInstance().setClickGUI(new ClickGUI());
        Lime.getInstance().setClickGUI2(new lime.ui.clickgui.frame2.ClickGUI());
        return this;
    }
}
