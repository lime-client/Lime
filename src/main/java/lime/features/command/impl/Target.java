package lime.features.command.impl;

import lime.core.Lime;
import lime.features.command.Command;
import lime.utils.other.ChatUtils;

public class Target extends Command {
    @Override
    public String getUsage() {
        return "friend <add/remove/clear> <name>";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] {"target"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        String type = args[1];

        switch(type.toLowerCase()) {
            case "add":
                Lime.getInstance().getTargetManager().addTarget(args[2]);
                ChatUtils.sendMessage("Added §a" + args[2]);
                break;
            case "clear":
                Lime.getInstance().getTargetManager().getEntitiesName().clear();
                ChatUtils.sendMessage("Removed all targets!");
                break;
            case "remove":
                Lime.getInstance().getTargetManager().getEntitiesName().remove(args[2]);
                ChatUtils.sendMessage("Removed §a" + args[2]);
                break;
        }
    }
}
