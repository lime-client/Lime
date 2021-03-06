package lime.commands.impl;

import lime.commands.Command;
import lime.utils.movement.MovementUtil;

public class HClip extends Command {
    @Override
    public String getAlias() {
        return "hclip";
    }

    @Override
    public String getDescription() {
        return "HClip a number given.";
    }

    @Override
    public String getSyntax() {
        return ".hclip <number>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        double toH = Double.parseDouble(args[0]);
        MovementUtil.hClip(toH);
    }
}
