package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChestStealer extends Module {
    public ChestStealer() {
        super("Chest Stealer", Category.PLAYER);
    }

    private final NumberProperty delay = new NumberProperty("Delay", this, 0, 500, 100, 10);
    private final BooleanProperty ignoreJunk = new BooleanProperty("Ignore Junk", this, true);
    private final BooleanProperty randomize = new BooleanProperty("Randomize", this, true);
    private final BooleanProperty ncp = new BooleanProperty("NCP", this, false);

    private final Timer timer = new Timer();
    private boolean isStealing;

    @Override
    public void onEnable() {
        isStealing = false;
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(mc.thePlayer.openContainer instanceof ContainerChest) {
            isStealing = true;
            if(timer.hasReached(delay.intValue())) {
                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
                if(randomize.isEnabled()) {
                    List<Integer> slots = new ArrayList<>();
                    for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                        if(chest.getLowerChestInventory().getStackInSlot(i) != null && isValidItem(chest.getLowerChestInventory().getStackInSlot(i))) {
                            slots.add(i);
                        }
                    }
                    Collections.shuffle(slots);
                    for (Integer i : slots) {
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                        if(ncp.isEnabled()) {
                            mc.playerController.windowClick(chest.windowId, i, 1, 1, mc.thePlayer);
                        }
                        timer.reset();
                        break;
                    }
                } else {
                    for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                        if(chest.getLowerChestInventory().getStackInSlot(i) != null && isValidItem(chest.getLowerChestInventory().getStackInSlot(i))) {
                            mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                            if(ncp.isEnabled()) {
                                mc.playerController.windowClick(chest.windowId, i, 1, 1, mc.thePlayer);
                            }
                            timer.reset();
                            break;
                        }
                    }
                }
            }
        } else {
            isStealing = false;
        }
    }

    public boolean isValidItem(ItemStack itemStack) {
        if(!ignoreJunk.isEnabled()) return true;

        Item item = itemStack.getItem();

        return item instanceof ItemSword || item instanceof ItemBlock || item instanceof ItemTool || item instanceof ItemFood
                || item instanceof ItemArmor || item instanceof ItemBow || item.getUnlocalizedName().contains("arrow") ||
                item instanceof ItemEnderPearl || (item instanceof ItemPotion && !InventoryUtils.isBadPotion(itemStack));
    }
}
