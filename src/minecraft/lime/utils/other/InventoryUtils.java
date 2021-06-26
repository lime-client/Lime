package lime.utils.other;

import lime.utils.IUtil;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtils implements IUtil {
    public static void swap(int slot1, int hotbarSlot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    public static Slot getSlot(int i){return mc.thePlayer.inventoryContainer.getSlot(i); }

    public static boolean hasItem(Item item, boolean checkHotbar, boolean checkInventory) {
        if(checkHotbar) {
            for(int i = 36; i < 45; ++i) {
                if(getSlot(i).getHasStack()) {
                    ItemStack itemStack = getSlot(i).getStack();
                    if(item.getUnlocalizedName().equalsIgnoreCase(itemStack.getItem().getUnlocalizedName()))
                        return true;
                }
            }
        }

        if(checkInventory) {
            for (int i = 9; i < 36; ++i) {
                if (getSlot(i).getHasStack()) {
                    ItemStack itemStack = getSlot(i).getStack();
                    if (item.getUnlocalizedName().equalsIgnoreCase(itemStack.getItem().getUnlocalizedName()))
                        return true;
                }
            }
        }
        return false;
    }

    public static int getEmptySlot() {
        for(int i = 36; i < 45; ++i) {
            if(!getSlot(i).getHasStack()) return i;
        }
        return -1;
    }

    public static int findItem(int min, int max, Item item) {
        for(int i = min; i < max; ++i) {
            if(getSlot(i).getHasStack()) {
                Item item1 = getSlot(i).getStack().getItem();
                if(item.getUnlocalizedName().equalsIgnoreCase(item1.getUnlocalizedName()))
                    return i;
            }
        }
        return -1;
    }

    public static boolean isInventoryFull() {
        for(int i = 9; i < 45; i++) {
            if(!getSlot(i).getHasStack()) {
                return false;
            }
        }
        return true;
    }

}
