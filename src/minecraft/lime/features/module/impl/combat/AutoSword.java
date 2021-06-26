package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.other.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;

@ModuleData(name = "Auto Sword", category = Category.COMBAT)
public class AutoSword extends Module {
    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
            if(packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                int slot = -1;
                float damage = 0;
                for(int i = 36; i < 45; i++) {
                    if(InventoryUtils.getSlot(i).getHasStack()) {
                        ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                        if(InventoryUtils.getDamage(itemStack) > damage) {
                            slot = i;
                            damage = InventoryUtils.getDamage(itemStack);
                        }
                    }
                }
                if(slot != -1) {
                    mc.thePlayer.inventory.currentItem = slot - 36;
                    mc.playerController.updateController();
                }
            }
        }
    }
}
