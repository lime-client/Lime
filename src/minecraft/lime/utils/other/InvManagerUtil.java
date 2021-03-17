package lime.utils.other;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;

public class InvManagerUtil {
    private static Minecraft mc = Minecraft.getMinecraft();
    public static void drop(int slot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }

    public static void swap(int slot1, int hotbarSlot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    public static void shiftClick(int slot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public static boolean shouldDrop(ItemStack is){
        return !is.getDisplayName().toLowerCase().contains("right click") && !is.getDisplayName().toLowerCase().contains("§");
    }

    public static float getDamage(ItemStack stack){
        float damage = 0;
        Item item = stack.getItem();
        if(item instanceof ItemTool){
            ItemTool tool = (ItemTool)item;
            damage += tool.getMaxDamage();
        }
        if(item instanceof ItemSword){
            ItemSword sword = (ItemSword)item;
            damage += sword.getDamageVsEntity();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(16, stack) * 1.25f +
                EnchantmentHelper.getEnchantmentLevel(20, stack) * 1.02f;
        return damage;

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
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)/100d;
        return value;
    }


    public static Slot getSlot(int i){return mc.thePlayer.inventoryContainer.getSlot(i); }
}
