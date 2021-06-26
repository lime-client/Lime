package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.other.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;

import static lime.utils.other.InventoryUtils.*;

@ModuleData(name = "Inventory Manager", category = Category.PLAYER)
public class InventoryManager extends Module {
    private enum Mode {
        NORMAL, OPEN_INV
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.NORMAL);
    private final SlideValue delay = new SlideValue("Delay", this, 5, 100, 80, 5);
    private final BoolValue dropJunk = new BoolValue("Drop Junk", this, true);

    private final Timer timer = new Timer();

    @EventTarget
    public void onMotion(EventMotion e) {
        if((mode.is("open_inv") && mc.currentScreen instanceof GuiInventory) || mode.is("normal")) {
            if(dropJunk.isEnabled()) {
                dropJunks();
            }
        }
    }

    private void dropJunks() {
        for(int i = 9; i < 45; i++) {
            if(getSlot(i).getHasStack()) {
                ItemStack itemStack = getSlot(i).getStack();
                Item item = itemStack.getItem();
                if(!(item instanceof ItemSword || item instanceof ItemBlock || item instanceof ItemTool || item instanceof ItemFood
                        || item instanceof ItemArmor || item instanceof ItemBow || item.getUnlocalizedName().contains("arrow") ||
                        item instanceof ItemEnderPearl || (item instanceof ItemPotion))) {
                    String name = itemStack.getDisplayName();
                    if(name.contains("§") || name.toLowerCase().contains("right click")) {
                        continue;
                    }

                    if(timer.hasReached((long) delay.getCurrent())) {
                        drop(i);
                        timer.reset();
                    }
                }
            }
        }
    }
}
