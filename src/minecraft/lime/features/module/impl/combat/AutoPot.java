package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.Priority;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.SlideValue;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

@ModuleData(name = "Auto Pot", category = Category.COMBAT)
public class AutoPot extends Module {

    private class ItemPot {
        private final ItemStack itemStack;
        private int slot;

        public ItemPot(ItemStack itemStack, int slot) {
            this.itemStack = itemStack;
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }

    private final SlideValue health = new SlideValue("Health", this, 1, 20, 10, 0.5);
    private final SlideValue delay = new SlideValue("Delay", this, 100, 1000, 500, 100);

    private final Timer timer = new Timer();

    @EventTarget(priority = Priority.LOW)
    public void onMotion(EventMotion e) {
        if(!mc.thePlayer.onGround) return;
        ItemPot healthPotion = health.getCurrent() >= mc.thePlayer.getHealth() ? getHealingPotion() : !mc.thePlayer.isPotionActive(Potion.moveSpeed) ? getSpeedPotion() : null;
        if(e.isPre() && !mc.thePlayer.capabilities.allowFlying) {

            if(healthPotion == null || !timer.hasReached(delay.intValue()))
                return;

            e.setPitch(90);
        }
        if(!e.isPre() && !mc.thePlayer.capabilities.allowFlying) {
            if(healthPotion == null || !timer.hasReached(delay.intValue()))
                return;

            if(healthPotion.getSlot() < 36) {
                InventoryUtils.swap(healthPotion.getSlot(), 5);
                healthPotion.setSlot(41);
            }

            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(healthPotion.getSlot() - 36));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

            timer.reset();
        }
    }


    private ItemPot getHealingPotion() {
        for(int i = 9; i < 45; ++i) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemPotion) {
                    ItemPotion itemPotion = (ItemPotion) itemStack.getItem();

                    if(!ItemPotion.isSplash(itemStack.getItemDamage()))
                        continue;

                    for (PotionEffect effect : itemPotion.getEffects(itemStack)) {
                        if(effect.getPotionID() == Potion.heal.getId() || (effect.getPotionID() == Potion.regeneration.getId() && !mc.thePlayer.isPotionActive(Potion.regeneration))) {
                            return new ItemPot(itemStack, i);
                        }
                    }
                }
            }
        }
        return null;
    }

    private ItemPot getSpeedPotion() {
        for(int i = 9; i < 45; ++i) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemPotion) {
                    ItemPotion itemPotion = (ItemPotion) itemStack.getItem();

                    if(!ItemPotion.isSplash(itemStack.getItemDamage()))
                        continue;

                    for (PotionEffect effect : itemPotion.getEffects(itemStack)) {
                        if(effect.getPotionID() == Potion.moveSpeed.getId()) {
                            return new ItemPot(itemStack, i);
                        }
                    }
                }
            }
        }
        return null;
    }
}
