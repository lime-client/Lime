package lime.features.command.impl;

import lime.features.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

import java.util.ArrayList;
import java.util.List;

public class BlackListBed extends Command {

    public static List<BlockPos> beds = new ArrayList<>();

    @Override
    public String getUsage() {
        return "blacklistbed";
    }

    @Override
    public String[] getPrefixes() {
        return new String[]{"blacklistbed", "blbed"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        if(Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
            beds.add(mop.getBlockPos());
        }
    }
}
