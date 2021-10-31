package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static lime.utils.other.InventoryUtils.*;

public class InventoryManager2 extends Module {
    public InventoryManager2() {
        super("Inventory Manager 2", Category.PLAYER);
    }

    private final NumberProperty delay = new NumberProperty("Delay", this, 10, 500, 50, 5);
    private final NumberProperty swordSlot = new NumberProperty("Sword Slot", this, 1, 9, 1, 1);
    private final NumberProperty pickaxeSlot = new NumberProperty("Pickaxe Slot", this, 1, 9, 2, 1);
    private final NumberProperty axeSlot = new NumberProperty("Axe Slot", this, 1, 9, 3, 1);
    private final BooleanProperty filterInventory = new BooleanProperty("Filter Inventory", this, true);
    private final BooleanProperty openInventory = new BooleanProperty("Open Inventory", this, false);
    private final BooleanProperty autoArmor = new BooleanProperty("Auto Armor", this, true);
    private final BooleanProperty dropJunk = new BooleanProperty("Drop Junk", this, true);

    private final Timer timer = new Timer();

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if((openInventory.isEnabled() && !(mc.currentScreen instanceof GuiInventory)) || !timer.hasReached(delay.intValue()))
            return;

        if(filterInventory.isEnabled()) {
            filterInventory();
        }

        if(dropJunk.isEnabled()) {
            dropJunk();
        }
    }

    public void filterInventory() {
        {
            int slot = swordSlot.intValue();
            if(timer.hasReached(delay.intValue())) {
                for(int i = 9; i < 45; ++i) {
                    if(slot - 1 + 36 == i) continue;
                    if(InventoryUtils.getSlot(i).getHasStack()) {
                        ItemStack is = InventoryUtils.getSlot(i).getStack();
                        if(is.getItem() instanceof ItemSword) {
                            if(isBestSword(is)) {
                                InventoryUtils.swap(i, slot - 1);
                            } else {
                                InventoryUtils.drop(i);
                            }
                            timer.reset();
                            break;
                        }
                    }
                }
            }
        }

        {
            int slot = pickaxeSlot.intValue();
            if(timer.hasReached(delay.intValue())) {
                for(int i = 9; i < 45; ++i) {
                    if(slot - 1 + 36 == i) continue;
                    if(InventoryUtils.getSlot(i).getHasStack()) {
                        ItemStack is = InventoryUtils.getSlot(i).getStack();
                        if(is.getItem() instanceof ItemPickaxe) {
                            if(isBestPickaxe(is)) {
                                InventoryUtils.swap(i, slot - 1);
                            } else {
                                InventoryUtils.drop(i);
                            }
                            timer.reset();
                            break;
                        }
                    }
                }
            }
        }

        {
            int slot = axeSlot.intValue();
            if(timer.hasReached(delay.intValue())) {
                for(int i = 9; i < 45; ++i) {
                    if(slot - 1 + 36 == i) continue;
                    if(InventoryUtils.getSlot(i).getHasStack()) {
                        ItemStack is = InventoryUtils.getSlot(i).getStack();
                        if(is.getItem() instanceof ItemAxe) {
                            if(isBestAxe(is)) {
                                InventoryUtils.swap(i, slot - 1);
                            } else {
                                InventoryUtils.drop(i);
                            }
                            timer.reset();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void filterArmor() {

    }

    public boolean isBestSword(ItemStack is) {
        ArrayList<ItemStack> swords = new ArrayList<>();

        for (int i = 9; i < 45; i++) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack is1 = InventoryUtils.getSlot(i).getStack();
                if(is1.getItem() instanceof ItemSword) {
                    swords.add(is1);
                }
            }
        }

        if(swords.isEmpty()) return true;

        swords.sort(Comparator.comparingDouble(InventoryUtils::getDamage));
        Collections.reverse(swords);

        return is == swords.get(0);
    }

    public boolean isBestPickaxe(ItemStack is) {
        ArrayList<ItemStack> pickaxes = new ArrayList<>();

        for (int i = 9; i < 45; i++) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack is1 = InventoryUtils.getSlot(i).getStack();
                if(is1.getItem() instanceof ItemPickaxe) {
                    pickaxes.add(is1);
                }
            }
        }

        if(pickaxes.isEmpty()) return true;

        pickaxes.sort(Comparator.comparingDouble(InventoryUtils::getToolEffect));
        Collections.reverse(pickaxes);

        return is == pickaxes.get(0);
    }

    public boolean isBestAxe(ItemStack is) {
        ArrayList<ItemStack> axes = new ArrayList<>();

        for (int i = 9; i < 45; i++) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack is1 = InventoryUtils.getSlot(i).getStack();
                if(is1.getItem() instanceof ItemAxe) {
                    axes.add(is1);
                }
            }
        }

        if(axes.isEmpty()) return true;

        axes.sort(Comparator.comparingDouble(InventoryUtils::getToolEffect));
        Collections.reverse(axes);

        return is == axes.get(0);
    }

    public void dropJunk() {
        for(int i = 9; i < 45; i++) {
            if(!timer.hasReached(delay.intValue()))
                break;
            if(getSlot(i).getHasStack()) {
                ItemStack itemStack = getSlot(i).getStack();
                Item item = itemStack.getItem();
                if(!(item instanceof ItemSword || item instanceof ItemBlock || item instanceof ItemTool || item instanceof ItemFood
                        || item instanceof ItemArmor || item instanceof ItemBow || item.getUnlocalizedName().contains("arrow") ||
                        item instanceof ItemEnderPearl || (item instanceof ItemPotion && !isBadPotion(itemStack)))) {
                    String name = itemStack.getDisplayName();
                    if(name.contains("§") || name.toLowerCase().contains("right click")) {
                        continue;
                    }

                    if(timer.hasReached((long) delay.getCurrent())) {
                        drop(i);
                        timer.reset();
                        break;
                    }
                }
            }
        }
    }
}
