package lime.features.command.impl;

import lime.features.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class Teleport extends Command {
    @Override
    public String getUsage() {
        return "Teleport to a player";
    }

    @Override
    public String[] getPrefixes() {
        return new String[]{"tp", "teleport"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        for (EntityPlayer playerEntity : Minecraft.getMinecraft().theWorld.playerEntities) {
            if(playerEntity.getName().equalsIgnoreCase(args[1])) {
                Minecraft.getMinecraft().thePlayer.setPosition(playerEntity.posX, playerEntity.posY, playerEntity.posZ);
            }
        }
    }
}
