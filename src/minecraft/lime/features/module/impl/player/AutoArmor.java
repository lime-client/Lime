package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;

@ModuleData(name = "Auto Armor", category = Category.PLAYER)
public class AutoArmor extends Module {

    private enum Mode {
        NORMAL, OPEN_INV
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.NORMAL);

    private final Timer timer = new Timer();

    @EventTarget
    public void onMotion(EventMotion e) {
        if((mode.is("open_inv") && mc.currentScreen instanceof GuiInventory) || mode.is("normal"))
            getBestArmor();
    }

    public void getBestArmor() {
        for(int type = 1; type < 5; ++type) {
            if(InventoryUtils.getSlot(4 + type).getHasStack()) {
                ItemStack itemStack = InventoryUtils.getSlot(4 + type).getStack();
                if(isBestArmor(itemStack, type))
                    return;
                else {
                    InventoryUtils.drop(4 + type);
                }
            }
            for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if(isBestArmor(itemStack, type) && InventoryUtils.getProtection(itemStack) > 0){
                        InventoryUtils.shiftClick(i);
                        timer.reset();
                    }
                }
            }
        }
    }

    public boolean isBestArmor(ItemStack itemStack, int type) {
        float protection = InventoryUtils.getProtection(itemStack);
        String armorType = "";
        switch(type) {
            case 1:
                armorType = "helmet";
                break;
            case 2:
                armorType = "chestplate";
                break;
            case 3:
                armorType = "leggings";
                break;
            case 4:
                armorType = "boots";
                break;
        }

        if(!itemStack.getUnlocalizedName().contains(armorType)) return false;

        for(int i = 5; i < 45; ++i) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack itemStack1 = InventoryUtils.getSlot(i).getStack();
                if(InventoryUtils.getProtection(itemStack1) > protection && itemStack.getUnlocalizedName().contains(armorType))
                    return false;
            }
        }

        return true;
    }
}
