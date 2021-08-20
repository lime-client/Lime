package lime.features.commands.impl;

import lime.core.Lime;
import lime.features.commands.Command;
import lime.features.module.Module;
import lime.utils.other.ChatUtils;

public class Toggle extends Command {
    @Override
    public String getUsage() {
        return "toggle <name>";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] { "toggle", "t" };
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        getModule(args[1]).toggle();
        ChatUtils.sendMessage("Toggled §a" + getModule(args[1]).getName());
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
