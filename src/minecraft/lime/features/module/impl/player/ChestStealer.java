package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.Random;

@ModuleData(name = "Chest Stealer", category = Category.PLAYER)
public class ChestStealer extends Module {
    private final SlideValue delayBeforeClose = new SlideValue("Delay before close", this, 0, 150, 100, 10);
    private final SlideValue delay = new SlideValue("Delay", this, 0, 150, 50, 5);
    private final BoolValue ignoreJunk = new BoolValue("Ignore Junk", this, true);
    private final BoolValue randomizer = new BoolValue("Randomizer", this, true);

    private boolean chestOpened;
    private final Timer closeTimer = new Timer();
    private final Timer timer = new Timer();

    @Override
    public void onEnable() {
        chestOpened = mc.currentScreen instanceof GuiChest;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(!chestOpened && mc.thePlayer.openContainer instanceof ContainerChest) {
            chestOpened = true;
            closeTimer.reset();
        } else if(chestOpened && !(mc.thePlayer.openContainer instanceof ContainerChest)) {
            chestOpened = false;
            closeTimer.reset();
            return;
        }

        if(mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;

            // Close if inventory full or chest empty
            if((isChestEmpty(chest) && closeTimer.hasReached((long) delayBeforeClose.getCurrent())) || (InventoryUtils.isInventoryFull())) {
                mc.thePlayer.closeScreen();
            }

            if(chest.getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase().contains("chest") || chest.getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase().contains("coffre")) {
                if(randomizer.isEnabled()) {
                    ArrayList<Integer> slots = new ArrayList<>();
                    for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                        if(chest.getLowerChestInventory().getStackInSlot(i) != null) {
                            slots.add(i);
                        }
                    }
                    Random random = new Random();
                    for (int i = 0; i < slots.size(); i++) {
                        int index = slots.get(random.nextInt(slots.size()));
                        if(chest.getLowerChestInventory().getStackInSlot(index) != null) {
                            if(timer.hasReached((long) delay.getCurrent()) && isValidItem(chest.getLowerChestInventory().getStackInSlot(index).getItem())) {
                                mc.playerController.windowClick(chest.windowId, index, 0, 1, mc.thePlayer);
                                slots.remove(i);
                                timer.reset();
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                        if(chest.getLowerChestInventory().getStackInSlot(i) != null) {
                            if(timer.hasReached((long) delay.getCurrent()) && isValidItem(chest.getLowerChestInventory().getStackInSlot(i).getItem())) {
                                mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                                timer.reset();
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isValidItem(Item item) {
        if(!ignoreJunk.isEnabled()) return true;

        return item instanceof ItemSword || item instanceof ItemBlock || item instanceof ItemTool || item instanceof ItemFood
                || item instanceof ItemArmor || item instanceof ItemBow || item.getUnlocalizedName().contains("arrow") ||
                item instanceof ItemEnderPearl || (item instanceof ItemPotion);
    }

    public boolean isChestEmpty(ContainerChest chest) {
        for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); ++i) {
            if (chest.getLowerChestInventory().getStackInSlot(i) != null && isValidItem(chest.getLowerChestInventory().getStackInSlot(i).getItem())) {
                return false;
            }
        }
        return true;
    }
}
