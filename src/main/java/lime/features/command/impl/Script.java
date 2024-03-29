package lime.features.command.impl;

import lime.features.command.Command;

public class Script extends Command {
    @Override
    public String getUsage() {
        return "script reload";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] {"script"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        if(args[1].equalsIgnoreCase("reload")) {
            //Lime.getInstance().getScriptManager().loadScripts();
            //ChatUtils.sendMessage("Reloaded all scripts.");
        }
    }
}
