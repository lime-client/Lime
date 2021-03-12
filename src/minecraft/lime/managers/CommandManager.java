package lime.managers;

import lime.commands.Command;
import lime.commands.cmds.Help;
import lime.commands.impl.*;
import lime.utils.ChatUtils;

import java.util.ArrayList;

public class CommandManager {
    private static ArrayList<Command> commands;
    public CommandManager(){
        commands = new ArrayList<>();
        commands.add(new Help());
        commands.add(new VClip());
        commands.add(new HClip());
        commands.add(new Config());
        commands.add(new Panic());
        commands.add(new Toggle());
        commands.add(new T());
        commands.add(new Bind());
    }
    public ArrayList<Command> getCommands(){return commands;}

    public void callCommand(String inputString){
        String[] args = inputString.split(" ");
        String command = args[0];
        String arguments = inputString.substring(command.length()).trim();
        for(Command c : getCommands()){
            if(c.getAlias().equalsIgnoreCase(command)){
                try{
                    c.onCommand(arguments, arguments.split(" "));
                } catch (Exception ignored){
                    ChatUtils.sendMsg("§cInvalid Usage: §r§1" + c.getSyntax());
                }
                return;
            }
        }
        ChatUtils.sendMsg("§cCommand not found. More details with .help");
    }
}
