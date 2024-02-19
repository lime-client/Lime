package lime.features.command.impl;

import lime.core.Lime;
import lime.features.command.Command;
import lime.utils.other.ChatUtils;

public class Friend extends Command {

    @Override
    public String getUsage() {
        return "friend <add/remove/clear> <name>";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] {"friend", "f", "friends"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        String type = args[1];

        switch(type.toLowerCase()) {
            case "add":
                Lime.getInstance().getFriendManager().addFriend(args[2]);
                ChatUtils.sendMessage("Added §a" + args[2]);
                break;
            case "clear":
                Lime.getInstance().getFriendManager().getFriends().clear();
                ChatUtils.sendMessage("Removed all friends!");
                break;
            case "remove":
                Lime.getInstance().getFriendManager().removeFriend(args[2]);
                ChatUtils.sendMessage("Removed §a" + args[2]);
                break;
        }
    }
}
