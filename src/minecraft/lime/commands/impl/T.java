package lime.commands.impl;

import lime.Lime;
import lime.commands.Command;

public class T extends Command {
    @Override
    public String getAlias() {
        return "t";
    }

    @Override
    public String getDescription() {
        return "Toggle a module";
    }

    @Override
    public String getSyntax() {
        return "toggle <module>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        Lime.moduleManager.getModuleByName(args[0]).toggle();
    }
}
