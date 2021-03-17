package lime.commands.impl;

import lime.Lime;
import lime.commands.Command;
import lime.utils.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class Friend extends Command {
    @Override
    public String getAlias() {
        return "friend";
    }

    @Override
    public String getDescription() {
        return "Add / delete friend.";
    }

    @Override
    public String getSyntax() {
        return "friend <add/delete/list> friend";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        String syntax = args[0];
        switch(syntax.toLowerCase()){
            case "add":
                Entity ent = getEntityInWorldByName(args[1]);
                if(ent != null){
                    if(!Lime.friendManager.isIn(ent.getName())){
                        Lime.friendManager.addFriend(ent.getName());
                        ChatUtils.sendMsg("Added " + args[1] + " as friend.");
                    }
                }
                break;
            case "delete":
                Entity ent2 = getEntityInWorldByName(args[1]);
                if(ent2 != null){
                    if(Lime.friendManager.isIn(ent2.getName())){
                        Lime.friendManager.deleteFriend(ent2.getName());
                        ChatUtils.sendMsg("Removed " + args[1] + " friend.");
                    }
                }
            case "list":
                String facile = "Friends: §c";
                for(String entity : Lime.friendManager.getFriends()){
                    facile += entity + "\n§c";
                }
                ChatUtils.sendMsg(facile);
            case "clear":
                Lime.friendManager.getFriends().clear();
        }
    }

    public Entity getEntityInWorldByName(String name){
        for(Entity ent : Minecraft.getMinecraft().theWorld.loadedEntityList){
            if(ent.getName().equalsIgnoreCase(name)) return ent;
        }
        return null;
    }
}
