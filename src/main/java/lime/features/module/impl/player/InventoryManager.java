package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static lime.utils.other.InventoryUtils.*;

public class InventoryManager extends Module {

    public InventoryManager() {
        super("Inventory Manager", Category.PLAYER);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Normal", "Normal", "OpenInv");
    private final NumberProperty delay = new NumberProperty("Delay", this, 5, 100, 80, 5);
    private final BooleanProperty dropJunk = new BooleanProperty("Drop Junk", this, true);

    private static final Timer timer = new Timer();

    public static Timer getTimer() {
        return timer;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(mc.thePlayer.openContainer instanceof ContainerChest) return;
        if((mode.is("openinv") && mc.currentScreen instanceof GuiInventory) || (mode.is("normal"))  && timer.hasReached((long) delay.intValue())) {
            if(dropJunk.isEnabled()) {
                dropJunks();
            }

            if(!timer.hasReached((long) delay.intValue())) return;

            getBestSword(36);

            if(!timer.hasReached(delay.intValue())) return;

            getBestPickaxe(37);

            if(!timer.hasReached(delay.intValue())) return;

            getBestAxe(38);
        }
    }

    private void getBestSword(int slot) {
        ArrayList<ItemStack> swords = new ArrayList<>();

        for (int i = 9; i < 45; i++) {
            if(getSlot(i).getHasStack() && i != slot) {
                ItemStack itemStack = getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemSword && !(itemStack.getDisplayName().contains("§") || itemStack.getDisplayName().contains("right click"))) {
                    swords.add(itemStack);
                }
            }
        }

        if(swords.isEmpty()) return;

        swords.sort(Comparator.comparingDouble(InventoryUtils::getDamage));

        Collections.reverse(swords);

        ItemStack bestSword = swords.get(0);


        if(findSlotByItem(bestSword) == slot) {
            return;
        }

        if(timer.hasReached((long) delay.getCurrent())) {
            if(getSlot(slot).getHasStack() && getSlot(slot).getStack().getItem() instanceof ItemSword) {
                if(getDamage(bestSword) <= getDamage(getSlot(slot).getStack())) {
                    drop(findSlotByItem(bestSword));
                } else {
                    swap(findSlotByItem(bestSword), slot - 36);
                }
            } else {
                swap(findSlotByItem(bestSword), slot - 36);
            }
            timer.reset();
        } else
            return;

        swords.remove(bestSword);

        for (ItemStack sword : swords) {
            if(timer.hasReached((long) delay.getCurrent())) {
                drop(findSlotByItem(sword));
                timer.reset();
            } else
                break;
        }

    }

    private void getBestPickaxe(int slot) {
        ArrayList<ItemStack> pickaxes = new ArrayList<>();

        for (int i = 9; i < 45; i++) {
            if(getSlot(i).getHasStack() & i != slot) {
                ItemStack itemStack = getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemPickaxe && !(itemStack.getDisplayName().contains("§") || itemStack.getDisplayName().contains("right click"))) {
                    pickaxes.add(itemStack);
                }
            }
        }

        if(pickaxes.isEmpty()) return;

        pickaxes.sort(Comparator.comparingDouble(InventoryUtils::getToolEffect));

        Collections.reverse(pickaxes);

        ItemStack bestPickaxe = pickaxes.get(0);

        if(timer.hasReached(delay.intValue())) {
            if(getSlot(slot).getHasStack() && getSlot(slot).getStack().getItem() instanceof ItemPickaxe) {
                if(getToolEffect(bestPickaxe) <= getToolEffect(getSlot(slot).getStack())) {
                    drop(findSlotByItem(bestPickaxe));
                } else {
                    swap(findSlotByItem(bestPickaxe), slot - 36);
                }
            } else {
                swap(findSlotByItem(bestPickaxe), slot - 36);
            }
            timer.reset();
        } else
            return;

        pickaxes.remove(bestPickaxe);

        for (ItemStack pickaxe : pickaxes) {
            if(!timer.hasReached(delay.intValue()))
                break;
            drop(findSlotByItem(pickaxe));
        }
    }


    private void getBestAxe(int slot) {
        ArrayList<ItemStack> axes = new ArrayList<>();

        for (int i = 9; i < 45; i++) {
            if(getSlot(i).getHasStack() & i != slot) {
                ItemStack itemStack = getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemAxe && !(itemStack.getDisplayName().contains("§") || itemStack.getDisplayName().contains("right click"))) {
                    axes.add(itemStack);
                }
            }
        }

        if(axes.isEmpty()) return;

        axes.sort(Comparator.comparingDouble(InventoryUtils::getToolEffect));

        Collections.reverse(axes);

        ItemStack bestAxe = axes.get(0);

        if(timer.hasReached(delay.intValue())) {
            if(getSlot(slot).getHasStack() && getSlot(slot).getStack().getItem() instanceof ItemAxe) {
                if(getToolEffect(bestAxe) <= getToolEffect(getSlot(slot).getStack())) {
                    drop(findSlotByItem(bestAxe));
                } else {
                    swap(findSlotByItem(bestAxe), slot - 36);
                }
            } else {
                swap(findSlotByItem(bestAxe), slot - 36);
            }
            timer.reset();
        } else
            return;

        axes.remove(bestAxe);

        for (ItemStack axe : axes) {
            if(!timer.hasReached(delay.intValue()))
                break;
            drop(findSlotByItem(axe));
        }
    }

    private int findSlotByItem(ItemStack itemStack) {
        for(int i = 9; i < 45; ++i) {
            if(getSlot(i).getHasStack()) {
                if(getSlot(i).getStack() == itemStack)
                    return i;
            }
        }
        return -1;
    }

    private void dropJunks() {
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
