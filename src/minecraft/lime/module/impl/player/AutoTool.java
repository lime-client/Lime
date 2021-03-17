package lime.module.impl.player;

import lime.Lime;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import lime.utils.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class AutoTool extends Module {
    public AutoTool(){
        super("AutoTool", 0, Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onMotion(EventMotion e){
        if (!mc.gameSettings.keyBindAttack.isKeyDown())
            return;
        if (mc.objectMouseOver == null)
            return;
        BlockPos pos = mc.objectMouseOver.getBlockPos();
        if (pos == null)
            return;
        updateTool(pos);
    }

    public void updateTool(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        float strength = 1.0F;
        int bestItemIndex = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null) {
                continue;
            }
            if ((itemStack.getStrVsBlock(block) > strength)) {
                strength = itemStack.getStrVsBlock(block);
                bestItemIndex = i;
            }
        }
        if (bestItemIndex != -1) {
            mc.thePlayer.inventory.currentItem = bestItemIndex;
        }
    }
}
