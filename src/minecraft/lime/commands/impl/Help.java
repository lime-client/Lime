package lime.commands.cmds;

import lime.Lime;
import lime.commands.Command;
import lime.managers.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Help extends Command {
    @Override
    public String getAlias(){
        return "help";
    }
    @Override
    public String getDescription(){
        return "Get every help commands";
    }
    @Override
    public String getSyntax() { return ""; }
    @Override
    public void onCommand(String command, String[] args) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("\n§6Help List:");
        for (Command c : Lime.commandManager.getCommands()){
            sb.append("\n  §c.").append(c.getAlias()).append((c.getSyntax().equals("") ? "" : " §9" + c.getSyntax())).append("§8: §7").append(c.getDescription());
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(sb.toString()));

    }
}