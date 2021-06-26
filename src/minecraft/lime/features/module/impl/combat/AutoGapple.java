package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.SlideValue;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;

@ModuleData(name = "Auto Gapple", category = Category.COMBAT)
public class AutoGapple extends Module {
    private final SlideValue delay = new SlideValue("Delay", this, 50, 1000, 100, 50);
    private final SlideValue health = new SlideValue("Health", this, 1, 20, 10, 0.5);

    private final Timer timer = new Timer();

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if (!InventoryUtils.hasItem(Items.golden_apple, true, true)) return;

        if (InventoryUtils.hasItem(Items.golden_apple, false, true) && !InventoryUtils.hasItem(Items.golden_apple, true, false)) {
            int emptySlot = InventoryUtils.getEmptySlot() == -1 ? 7 : InventoryUtils.getEmptySlot() - 36;

            int gappleSlot = InventoryUtils.findItem(9, 36, Items.golden_apple);

            if(gappleSlot == -1) return;

            InventoryUtils.swap(gappleSlot, emptySlot);
        }

        if(mc.thePlayer.getHealth() < 0 || mc.thePlayer.getHealth() > health.getCurrent() || !timer.hasReached((int) delay.getCurrent())) return;

        int gappleSlot = InventoryUtils.findItem(36, 45, Items.golden_apple);
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(gappleSlot - 36));
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, mc.thePlayer.inventory.mainInventory[gappleSlot - 36], 0, 0, 0));
        for(int i = 0; i < 35; i++) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
        }
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        if(mc.thePlayer.isBlocking()) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
        }

        timer.reset();
    }
}
