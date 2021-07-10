package lime.features.commands.impl;

import lime.features.commands.Command;
import lime.utils.movement.MovementUtils;
import lime.utils.other.ChatUtils;

public class VClip extends Command {
    @Override
    public String getUsage() {
        return "vclip <number>";
    }

    @Override
    public String[] getPrefixes() {
        return new String[]{"vclip", "vc"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        MovementUtils.vClip(Double.parseDouble(args[1]));
        ChatUtils.sendMessage("VCliped §a" + args[1] + " §7blocks");
    }
}
