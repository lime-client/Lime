package lime.commands.impl;

import lime.Lime;
import lime.commands.Command;
import lime.module.Module;

public class Panic extends Command {
    @Override
    public String getAlias() {
        return "panic";
    }

    @Override
    public String getDescription() {
        return "Disable all modules";
    }

    @Override
    public String getSyntax() {
        return ".panic";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        Lime.moduleManager.getModules().forEach(Module::disable);
    }
}
