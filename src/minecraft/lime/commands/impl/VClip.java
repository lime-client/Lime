package lime.commands.impl;

import lime.commands.Command;
import lime.utils.movement.MovementUtil;

public class VClip extends Command {
    @Override
    public String getAlias() {
        return "vclip";
    }

    @Override
    public String getDescription() {
        return "VClip a number given.";
    }

    @Override
    public String getSyntax() {
        return ".vclip <number>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        double toV = Double.parseDouble(args[0]);
        MovementUtil.vClip(toV);
    }
}
