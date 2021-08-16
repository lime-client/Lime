package lime.features.commands.impl;

import lime.features.commands.Command;

public class Account extends Command {

    public static String mail = "", pass = "";

    @Override
    public String getUsage() {
        return "account mail:pass";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] { "account", "setspec", "spec", "spectator", "setspectator"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        mail = args[1].split(":")[0];
        pass = args[1].split(":")[1];
    }
}
