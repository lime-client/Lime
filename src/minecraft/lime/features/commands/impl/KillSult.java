package lime.features.commands.impl;

import lime.features.commands.Command;

public class KillSult extends Command {
    @Override
    public String getUsage() {
        return "Reload killsults";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] {"killsults", "killsult", "sult", "insults", "insult"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        if(args[1].equalsIgnoreCase("reload")) {
            lime.features.module.impl.player.KillSult.loadKillsults(lime.features.module.impl.player.KillSult.killsults);
        }
    }
}
