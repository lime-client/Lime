package lime.utils.other;

import lime.utils.IUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

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

    public static int hasBlock(List blacklistedBlocks, boolean checkInv, boolean checkHotBar) {
        if(checkInv) {
            for(int i = 9; i < 36; ++i) {
                if(getSlot(i).getHasStack()) {
                    ItemStack itemStack = getSlot(i).getStack();
                    if(itemStack.getItem() instanceof ItemBlock) {
                        ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                        if(!blacklistedBlocks.contains(itemBlock.getBlock()) && itemStack.stackSize > 0)
                            return i;
                    }
                }
            }
        }

        if(checkHotBar) {
            for(int i = 36; i < 45; ++i) {
                if(getSlot(i).getHasStack()) {
                    ItemStack itemStack = getSlot(i).getStack();
                    if(itemStack.getItem() instanceof ItemBlock) {
                        ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                        if(!blacklistedBlocks.contains(itemBlock.getBlock()) && itemStack.stackSize > 0)
                            return i;
                    }
                }
            }
        }

        return -1;
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

    public static float getDamage(ItemStack stack){
        float damage = 0;
        Item item = stack.getItem();
        if(item instanceof ItemSword){
            ItemSword sword = (ItemSword)item;
            damage += sword.getDamageVsEntity();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(16, stack) * 1.25f +
                EnchantmentHelper.getEnchantmentLevel(20, stack) * 1.02f;
        return damage;

    }

    public static float getProtection(ItemStack stack){
        float prot = 0;
        if ((stack.getItem() instanceof ItemArmor)) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(0, stack) * 0.0075;
            prot += EnchantmentHelper.getEnchantmentLevel(3, stack)/100;
            prot += EnchantmentHelper.getEnchantmentLevel(1, stack)/100;
            prot += EnchantmentHelper.getEnchantmentLevel(7, stack)/100;
            prot += EnchantmentHelper.getEnchantmentLevel(34, stack)/50;
        }
        return prot;
    }

    public static boolean isBadPotion(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final PotionEffect o : potion.getEffects(stack)) {
                    final PotionEffect effect = o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static float getToolEffect(ItemStack stack){
        Item item = stack.getItem();
        if(!(item instanceof ItemTool))
            return 0;
        String name = item.getUnlocalizedName();
        ItemTool tool = (ItemTool)item;
        float value = 1;
        if(item instanceof ItemPickaxe){
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if(name.toLowerCase().contains("gold")){
                value -= 100;
            }
        }else if(item instanceof ItemSpade){
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if(name.toLowerCase().contains("gold")){
                value -= 100;
            }
        }else if(item instanceof ItemAxe){
            value = tool.getStrVsBlock(stack, Blocks.log);
            if(name.toLowerCase().contains("gold")){
                value -= 100;
            }
        }else
            return 1f;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)/100;
        return value;
    }

    public static boolean isInventoryFull() {
        for(int i = 9; i < 45; i++) {
            if(!getSlot(i).getHasStack()) {
                return false;
            }
        }
        return true;
    }

    public static boolean hotBarIsFull() {
        for(int i = 36; i < 45; ++i) {
            if(!getSlot(i).getHasStack()) return true;
        }

        return false;
    }

    public static void shiftClick(int slot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public static void drop(int slot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }
}
