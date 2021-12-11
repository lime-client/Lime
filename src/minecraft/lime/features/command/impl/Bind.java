package lime.features.command.impl;

import lime.core.Lime;
import lime.features.command.Command;
import lime.features.module.Module;
import lime.utils.other.ChatUtils;
import org.lwjgl.input.Keyboard;

public class Bind extends Command {
    @Override
    public String getUsage() {
        return "bind <module> <key>";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] {"bind"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        Module module = getModule(args[1]);

        if(module == null) {
            ChatUtils.sendMessage("Module §a" + args[1] + " §7was not found.");
            return;
        }

        int key = Keyboard.getKeyIndex(args[2].toUpperCase());

        module.setKey(key);

        ChatUtils.sendMessage("§a" + module.getName() + "§7 is now bound to §a" + args[2] + "§7.");
    }

    private Module getModule(String name) {
        for (Module module : Lime.getInstance().getModuleManager().getModules()) {
            if(module.getName().replace(" ", "").equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
}
