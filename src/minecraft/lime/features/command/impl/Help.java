package lime.features.command.impl;

import lime.core.Lime;
import lime.features.command.Command;
import lime.utils.other.ChatUtils;

public class Help extends Command {
    @Override
    public String getUsage() {
        return "help";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] { "help" };
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        for (Command command : Lime.getInstance().getCommandManager().getCommands()) {
            if(command != this) {
                ChatUtils.sendMessage(command.getPrefixes()[0] + ": " + command.getUsage());
            }
        }
    }
}
