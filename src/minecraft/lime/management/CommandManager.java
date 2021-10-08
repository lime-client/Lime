package lime.management;

import lime.features.commands.Command;
import lime.features.commands.impl.*;
import lime.utils.other.ChatUtils;

import java.util.ArrayList;

public class CommandManager {
    private final ArrayList<Command> commands;

    public CommandManager() {
        this.commands = new ArrayList<>();

        registerCommand(new Config());
        registerCommand(new VClip());
        registerCommand(new Bind());
        registerCommand(new HClip());
        registerCommand(new Name());
        registerCommand(new Script());
        registerCommand(new KillSult());
        registerCommand(new Account());
        registerCommand(new Friend());
        registerCommand(new Help());
        registerCommand(new Toggle());
    }

    private void registerCommand(Command command) {
        this.commands.add(command);
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void callCommand(String message) {
        message = message.substring(1);

        for(Command command : this.getCommands()) {
            for(String s : command.getPrefixes()) {
                if(s.equalsIgnoreCase(message.split(" ")[0])) {
                    try {
                        command.onCommand(message.split(" "));
                    } catch (Exception e) {
                        System.out.println("Failed to execute " + s + " command! Error: " + e.getMessage());
                    }
                    return;
                }
            }
        }

        ChatUtils.sendMessage("§a" + message.split(" ")[0] + " §7doesn't exist!");
    }
}
