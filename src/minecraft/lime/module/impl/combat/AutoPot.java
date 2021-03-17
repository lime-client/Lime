package lime.module.impl.combat;

import lime.Lime;
import lime.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Random;

public class AutoPot extends Module {

    int ticks = 0;
    public static boolean doPot = false;
    public static boolean doSp = false;

    public AutoPot() {
        super("AutoPot", 0, Category.COMBAT);
        Lime.setmgr.rSetting(new Setting("Health", this, 10.0, 1.0, 20.0, false));
    }



    public static double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted;
    }

    @EventTarget
    public void onUpdate(EventMotion event) {
        setSuffix(getCount() + "");
        if (this.ticks > 0)
        {
            this.ticks -= 1;
            return;
        }
        if (event.getState() == EventMotion.State.PRE)
        {
            ItemThing potSlot = getHealingItemFromInventory();
            ItemThing spSlot = getSpItemFromInventory();
            if ((this.ticks == 0) && (mc.thePlayer.getHealth() <= getSettingByName("Health").getValDouble() && (potSlot.getSlot() != -1)))
            {
                event.setPitch(123);
                if(mc.thePlayer.onGround) mc.thePlayer.jump();
                doPot = true;
            }
            if(this.ticks == 0 && spSlot.getSlot() != -1) {
                event.setPitch(123);
                if(mc.thePlayer.onGround) mc.thePlayer.jump();
                doSp = true;
            }
        }
        else if (doPot)
        {
            ItemThing potSlot = getHealingItemFromInventory();
            if (potSlot.getSlot() == -1) {
                return;
            }
            if (this.doPot)
            {
                if (potSlot.getSlot() < 9)
                {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(potSlot.getSlot()));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                else
                {
                    swap(potSlot.getSlot(), 5);
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(5));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    if (potSlot.isSoup()) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                    }
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                this.ticks =  10;
                this.doPot = false;
            }
        } else if(doSp){
            ItemThing potSlot = getSpItemFromInventory();
            if (potSlot.getSlot() < 9)
            {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(potSlot.getSlot()));
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            else
            {
                swap(potSlot.getSlot(), 5);
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(5));
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                if (potSlot.isSoup()) {
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                }
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            this.ticks =  10;
            this.doSp = false;
        }
    }

    public static int getCount() {
        int pot = -1;
        int counter = 0;
        for (int i = 0; i < 36; ++i) {
            if (Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i] != null) {
                ItemStack is = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i];
                Item item = is.getItem();
                if (item instanceof ItemPotion) {
                    ItemPotion potion = (ItemPotion) item;
                    if (potion.getEffects(is) != null) {
                        for (Object o : potion.getEffects(is)) {
                            PotionEffect effect = (PotionEffect) o;
                            if ((effect.getPotionID() == Potion.heal.id && ItemPotion.isSplash(is.getItemDamage())) || effect.getPotionID() == Potion.regeneration.id && ItemPotion.isSplash(is.getItemDamage())) {
                                ++counter;
                            }
                        }
                    }
                }


            }
        }

        return counter;
    }

    private ItemThing getHealingItemFromInventory() {
        int itemSlot = -1;
        int counter = 0;
        boolean soup = false;
        for (int i = 0; i < 36; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                Item item = is.getItem();
                if (item instanceof ItemPotion) {
                    ItemPotion potion = (ItemPotion) item;
                    if (potion.getEffects(is) != null) {
                        for (Object o : potion.getEffects(is)) {
                            PotionEffect effect = (PotionEffect) o;
                            if ((effect.getPotionID() == Potion.heal.id && ItemPotion.isSplash(is.getItemDamage())) || (effect.getPotionID() == Potion.moveSpeed.id && ItemPotion.isSplash(is.getItemDamage()) && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) || (effect.getPotionID() == Potion.regeneration.id && ItemPotion.isSplash(is.getItemDamage()) && !mc.thePlayer.isPotionActive(Potion.regeneration))) {
                                ++counter;
                                itemSlot = i;
                            }
                        }
                    }
                }
            }
        }

        return new ItemThing(itemSlot, soup);
    }
    private ItemThing getSpItemFromInventory() {
        int itemSlot = -1;
        int counter = 0;
        boolean soup = false;
        for (int i = 0; i < 36; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                Item item = is.getItem();
                if (item instanceof ItemPotion) {
                    ItemPotion potion = (ItemPotion) item;
                    if (potion.getEffects(is) != null) {
                        for (Object o : potion.getEffects(is)) {
                            PotionEffect effect = (PotionEffect) o;
                            if (effect.getPotionID() == Potion.moveSpeed.id && ItemPotion.isSplash(is.getItemDamage()) && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                                ++counter;
                                itemSlot = i;
                            }
                        }
                    }
                }
            }
        }

        return new ItemThing(itemSlot, soup);
    }

    private void swap(int slot, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarSlot, 2, mc.thePlayer);
    }

    public class ItemThing {

        private boolean soup;
        private int slot;

        public ItemThing(int slot, boolean soup) {
            this.slot = slot;
            this.soup = soup;
        }

        public int getSlot() {
            return slot;
        }

        public boolean isSoup() {
            return soup;
        }

    }

}