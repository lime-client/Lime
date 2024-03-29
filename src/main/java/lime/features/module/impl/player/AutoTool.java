package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.other.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

public class AutoTool extends Module {

    public AutoTool() {
        super("Auto Tool", Category.PLAYER);
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null) {
            if(mc.objectMouseOver.getBlockPos() != null && !(mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockAir)) {
                int slot = -1;
                float max = 0;
                for(int i = 36; i < 45; ++i) {
                    if(InventoryUtils.getSlot(i).getHasStack()) {
                        ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                        Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
                        if(itemStack.getStrVsBlock(block) > max && itemStack.getItem() instanceof ItemTool) {
                            slot = i;
                            max = itemStack.getStrVsBlock(block);
                        }
                    }
                }

                if(slot != -1) {
                    mc.thePlayer.inventory.currentItem = slot - 36;
                    mc.playerController.updateController();
                }
            }
        }
    }
}
