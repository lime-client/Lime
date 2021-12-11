package lime.features.command.impl;

import lime.features.command.Command;
import lime.utils.movement.MovementUtils;
import lime.utils.other.ChatUtils;

public class HClip extends Command {
    @Override
    public String getUsage() {
        return "hclip <number>";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] {"hclip", "hc"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        MovementUtils.hClip(Integer.parseInt(args[1]));
        ChatUtils.sendMessage("HCliped §a" + args[1] + " §7blocks");
    }
}
