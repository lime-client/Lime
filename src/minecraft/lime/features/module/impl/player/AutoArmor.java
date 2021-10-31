package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.other.InventoryUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmor extends Module {

    public AutoArmor() {
        super("Auto Armor", Category.PLAYER);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Normal", "Normal", "OpenInv");
    private final NumberProperty delay = new NumberProperty("Delay", this, 0, 100, 50, 5);

    @EventTarget
    public void onTick(EventUpdate event) {
        if(mc.thePlayer.openContainer instanceof ContainerChest) return;
        if (InventoryManager.getTimer().hasReached((long)delay.getCurrent()) && !mc.thePlayer.capabilities.isCreativeMode && ((mc.currentScreen instanceof GuiInventory && mode.is("openinv")) || mode.is("normal"))) {
            for(int b = 5; b <= 8; ++b) {
                if (equipArmor(b)) {
                    InventoryManager.getTimer().reset();
                    break;
                }
            }
        }
    }

    private boolean equipArmor(int b) {
        int currentProtection = -1;
        int slot = -1;
        ItemArmor current = null;
        if (mc.thePlayer.inventoryContainer.getSlot(b).getStack() != null && mc.thePlayer.inventoryContainer.getSlot(b).getStack().getItem() instanceof ItemArmor) {
            current = (ItemArmor)mc.thePlayer.inventoryContainer.getSlot(b).getStack().getItem();
            currentProtection = current.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, mc.thePlayer.inventoryContainer.getSlot(b).getStack());
        }

        for(int i = 9; i <= 44; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor)stack.getItem();
                int armorProtection = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
                if (this.checkArmor(armor, b) && (current == null || currentProtection < armorProtection)) {
                    currentProtection = armorProtection;
                    current = armor;
                    slot = i;
                } else if(this.checkArmor(armor, b) && (currentProtection > armorProtection || currentProtection == armorProtection)) {
                    if(InventoryManager.getTimer().hasReached((long) delay.getCurrent())) {
                        InventoryUtils.drop(i);
                        InventoryManager.getTimer().reset();
                        break;
                    }
                }
            }
        }

        if (slot != -1) {
            boolean isNull = mc.thePlayer.inventoryContainer.getSlot(b).getStack() == null;
            if(InventoryManager.getTimer().hasReached((long) delay.getCurrent())) {
                if (!isNull) {
                    InventoryUtils.drop(b);
                } else {
                    InventoryUtils.shiftClick(slot);
                }
                InventoryManager.getTimer().reset();
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean checkArmor(ItemArmor item, int b) {
        return b == 5 && item.getUnlocalizedName().startsWith("item.helmet") || b == 6 && item.getUnlocalizedName().startsWith("item.chestplate") || b == 7 && item.getUnlocalizedName().startsWith("item.leggings") || b == 8 && item.getUnlocalizedName().startsWith("item.boots");
    }
}
