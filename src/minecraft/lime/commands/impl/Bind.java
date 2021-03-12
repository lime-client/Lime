package lime.commands.impl;

import lime.Lime;
import lime.commands.Command;
import lime.module.Module;
import org.lwjgl.input.Keyboard;

public class Bind extends Command {

    @Override
    public String getAlias() {
        return "bind";
    }

    @Override
    public String getDescription() {
        return "Bind a module";
    }

    @Override
    public String getSyntax() {
        return "bind <module> <key>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        Lime.moduleManager.getModuleByName(args[0]).setKey(Keyboard.getKeyIndex(args[1].toUpperCase()));
    }
}
