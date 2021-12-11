package lime.features.command.impl;

import lime.features.command.Command;
import lime.utils.other.ChatUtils;

public class Account extends Command {

    public static String mail = "", pass = "";

    @Override
    public String getUsage() {
        return "spec mail:pass";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] { "spec"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        mail = args[1].split(":")[0];
        pass = args[1].split(":")[1];
        ChatUtils.sendMessage("Set spec acc");
    }
}
