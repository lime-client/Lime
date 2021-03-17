package lime.module.impl.player;

import lime.Lime;
import lime.settings.impl.BooleanValue;
import lime.settings.impl.ComboBooleanValue;
import lime.settings.impl.ListValue;
import lime.settings.impl.SlideValue;
import lime.events.EventTarget;
import lime.events.impl.EventDeath;
import lime.events.impl.EventMotion;
import lime.events.impl.EventWorldChange;
import lime.module.Module;
import lime.utils.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import static lime.utils.other.InvManagerUtil.*;

public class InvManager extends Module {

    ListValue mode = new ListValue("Mode", this, "Basic", "Basic", "FakeInv", "OpenInv");
    ComboBooleanValue disableon = new ComboBooleanValue("Disable on", this);
    BooleanValue onDeath = new BooleanValue("Death", this, true, disableon.getSet());
    BooleanValue onWorldChange = new BooleanValue("Changing World", this, true, disableon.getSet());
    SlideValue delay = new SlideValue("Delay", this, 200, 0, 1000, true);
    SlideValue sword = new SlideValue("Sword Slot", this, 1, 1, 9, true);
    SlideValue pickaxe = new SlideValue("Pickaxe Slot", this, 2, 1, 9, true);
    SlideValue axe = new SlideValue("Axe Slot", this, 3, 1, 9, true);
    public Timer timer = new Timer();

    public InvManager(){
        super("InvManager", 0, Category.PLAYER);
    }

    public void dropUselessItem(){
        for(int i = 9; i < 45; i++){
            if(getSlot(i).getHasStack()){
                ItemStack is = getSlot(i).getStack();
                Item item = is.getItem();
                if(!(item instanceof ItemPickaxe) && !(item instanceof ItemSword)
                         && !(item instanceof ItemAxe) &&
                        !(item instanceof ItemFood) && !(item instanceof ItemEnderPearl) && !(item instanceof ItemArmor)){
                    if(shouldDrop(is) && timer.hasReached(delay.getIntValue())){
                        if(item instanceof ItemBlock){
                            String name = item.getUnlocalizedName().toLowerCase();
                            if(name.contains("sand") || name.contains("cactus") || name.contains("gravel")){
                                if (mode.getValue().equalsIgnoreCase("fakeinv"))
                                    mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                                drop(i);
                                timer.reset();
                            }
                        } else if (item instanceof ItemPotion){
                            ItemPotion potion = (ItemPotion) item;
                            if(potion.getEffects(is) != null){
                                for(Object o : potion.getEffects(is)){
                                    PotionEffect effect = (PotionEffect) o;
                                    if(effect.getPotionID() != Potion.heal.id && effect.getPotionID() != Potion.moveSpeed.id && effect.getPotionID() != Potion.regeneration.id){
                                        if (mode.getValue().equalsIgnoreCase("fakeinv"))
                                            mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                                        drop(i);
                                        timer.reset();
                                        return;
                                    }
                                }
                            }
                        } else {
                            if (mode.getValue().equalsIgnoreCase("fakeinv"))
                                mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                            drop(i);
                            timer.reset();
                        }
                    }
                }
            }
        }
    }

    public void getBestSword(int slot){
        for(int i = 9; i < 45; i++){
            if(i == 36 + slot) continue;
            if(getSlot(i).getHasStack()){
                ItemStack stack = getSlot(i).getStack();
                if(stack.getItem() instanceof ItemSword){
                    if(isBestSword(stack)){
                        if(getSlot(36 + slot).getHasStack() && getSlot(36 + slot).getStack().getItem() instanceof ItemSword && getDamage(stack) == getDamage(getSlot(36 + slot).getStack())){
                            if(getDamage(stack) == getDamage(getSlot(36 + slot).getStack())){
                                ItemStack sword = getSlot(36 + slot).getStack();
                                if(sword.getItemDamage() < stack.getItemDamage()){
                                    dropWithTimerCheck(i, timer);
                                } else if(sword.getItemDamage() > stack.getItemDamage()){
                                    swapWithTimerCheck(i, slot, timer);
                                } else if (sword.getItemDamage() == stack.getItemDamage() && isBestSword(sword)) {
                                    dropWithTimerCheck(i, timer);
                                } else if(sword.getItemDamage() == stack.getItemDamage() && isBestSword(stack)){
                                    swapWithTimerCheck(i, slot, timer);
                                }
                            } else if (getDamage(stack) > getDamage(getSlot(36 + slot).getStack())){
                                swapWithTimerCheck(i, slot, timer);
                            } else if(getDamage(stack) < getDamage(getSlot(36 + slot).getStack())){
                                dropWithTimerCheck(i, timer);
                            }
                        } else {
                            swapWithTimerCheck(i, slot, timer);
                        }
                    } else {
                        dropWithTimerCheck(i, timer);
                    }
                }
            }
        }
    }

    public void getBestPickaxe(int slot){
        for(int i = 9; i < 45; i++){
            if(i == 36 + slot) continue;
            if(getSlot(i).getHasStack()){
                ItemStack stack = getSlot(i).getStack();
                if(stack.getItem() instanceof ItemPickaxe){
                    if(isBestPickaxe(stack)){
                        if(getSlot(36 + slot).getHasStack() && getSlot(36 + slot).getStack().getItem() instanceof ItemPickaxe && getToolEffect(stack) == getToolEffect(getSlot(36 + slot).getStack())){
                            if(getToolEffect(stack) == getToolEffect(getSlot(36 + slot).getStack())){
                                ItemStack sword = getSlot(36 + slot).getStack();
                                if(sword.getItemDamage() < stack.getItemDamage()){
                                    dropWithTimerCheck(i, timer);
                                } else if(sword.getItemDamage() > stack.getItemDamage()){
                                    swapWithTimerCheck(i, slot, timer);
                                } else if (sword.getItemDamage() == stack.getItemDamage() && isBestPickaxe(sword)) {
                                    dropWithTimerCheck(i, timer);
                                } else if(sword.getItemDamage() == stack.getItemDamage() && isBestPickaxe(stack)){
                                    swapWithTimerCheck(i, slot, timer);
                                }
                            } else if (getToolEffect(stack) > getToolEffect(getSlot(36 + slot).getStack())){
                                swapWithTimerCheck(i, slot, timer);
                            } else if(getToolEffect(stack) < getToolEffect(getSlot(36 + slot).getStack())){
                                dropWithTimerCheck(i, timer);
                            }
                        } else {
                            swapWithTimerCheck(i, slot, timer);
                        }
                    } else {
                        dropWithTimerCheck(i, timer);
                    }
                }
            }
        }
    }

    public void getBestAxe(int slot){
        for(int i = 9; i < 45; i++){
            if(i == 36 + slot) continue;
            if(getSlot(i).getHasStack()){
                ItemStack stack = getSlot(i).getStack();
                if(stack.getItem() instanceof ItemAxe){

                    if(isBestAxe(stack)){
                        if(getSlot(36 + slot).getHasStack() && getSlot(36 + slot).getStack().getItem() instanceof ItemAxe){
                            if(getToolEffect(stack) == getToolEffect(getSlot(36 + slot).getStack())){
                                ItemStack sword = getSlot(36 + slot).getStack();
                                if(sword.getItemDamage() < stack.getItemDamage()){
                                    dropWithTimerCheck(i, timer);
                                } else if(sword.getItemDamage() > stack.getItemDamage()){
                                    swapWithTimerCheck(i, slot, timer);
                                } else if (sword.getItemDamage() == stack.getItemDamage() && isBestAxe(sword)) {
                                    dropWithTimerCheck(i, timer);
                                } else if(sword.getItemDamage() == stack.getItemDamage() && isBestAxe(stack)){
                                    swapWithTimerCheck(i, slot, timer);
                                }
                            } else if (getToolEffect(stack) > getToolEffect(getSlot(36 + slot).getStack())){
                                swapWithTimerCheck(i, slot, timer);
                            } else if(getToolEffect(stack) < getToolEffect(getSlot(36 + slot).getStack())){
                                dropWithTimerCheck(i, timer);
                            }
                        } else {
                            swapWithTimerCheck(i, slot, timer);
                        }
                    } else {
                        dropWithTimerCheck(i, timer);
                    }
                }
            }
        }
    }


    public void swapWithTimerCheck(int slot1, int hotbarSlot,Timer timer){
        if(timer.hasReached(delay.getIntValue())){
            swap(slot1, hotbarSlot);
            timer.reset();
        }
    }
    public void dropWithTimerCheck(int slot,Timer timer){
        if(timer.hasReached(delay.getIntValue())){
            drop(slot);
            timer.reset();
        }
    }

    public boolean isBestSword(ItemStack is){
        float damage = getDamage(is);
        for(int i = 9; i < 45; i++){
            if(getSlot(i).getHasStack()){
                ItemStack itemStack = getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemSword && itemStack != is){
                    if(getDamage(itemStack) > damage)
                        return false;
                }
            }
        }
        return is.getItem() instanceof ItemSword;
    }

    public boolean isBestPickaxe(ItemStack is){
        float damage = getToolEffect(is);
        for(int i = 9; i < 45; i++){
            if(getSlot(i).getHasStack()){
                ItemStack itemStack = getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemPickaxe){
                    if(damage < getToolEffect(itemStack))
                        return false;
                }
            }
        }
        return is.getItem() instanceof ItemPickaxe;
    }

    public boolean isBestAxe(ItemStack is){
        float damage = getToolEffect(is);
        for(int i = 9; i < 45; i++){
            if(getSlot(i).getHasStack()){
                ItemStack itemStack = getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemAxe){
                    if(damage < getToolEffect(itemStack))
                        return false;
                }
            }
        }
        return is.getItem() instanceof ItemAxe;
    }

    public void dropUselessArmor(){
        if(!Lime.moduleManager.getModuleByName("AutoArmor").isToggled()) return;
        for(int i = 9; i < 45; i++){
            int type = -1;
            if(getSlot(i).getHasStack()){
                ItemStack is = getSlot(i).getStack();
                if(is.getUnlocalizedName().contains("helmet"))
                    type = 1;
                if(is.getUnlocalizedName().contains("chestplate"))
                    type = 2;
                if(is.getUnlocalizedName().contains("leggings"))
                    type = 3;
                if(is.getUnlocalizedName().contains("boots"))
                    type = 4;
                if(type == -1) continue;
                if(is.getItem() instanceof ItemArmor && !AutoArmor.isBestArmor(is, type)){
                    if(Lime.setmgr.getSettingByName("Mode").getValString().equalsIgnoreCase("FakeInv")){
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    }
                    dropWithTimerCheck(i, timer);
                }
                if(is.getItem() instanceof ItemArmor && getSlot(type + 4).getHasStack() && AutoArmor.getProtection(is) == AutoArmor.getProtection(getSlot(type + 4).getStack())){
                    if(Lime.setmgr.getSettingByName("Mode").getValString().equalsIgnoreCase("FakeInv")){
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    }
                    dropWithTimerCheck(i, timer);
                }

            }
        }
    }





    @EventTarget
    public void onMotion(EventMotion e){
        if(mode.getValue().equalsIgnoreCase("OpenInv") && !(mc.currentScreen instanceof GuiInventory)) return;
        if(sword.getIntValue() == pickaxe.getIntValue()) return;
        if(sword.getIntValue() == axe.getIntValue()) return;
        if(axe.getIntValue() == pickaxe.getIntValue()) return;
        dropUselessItem();
        getBestSword(sword.getIntValue() - 1);
        getBestPickaxe(pickaxe.getIntValue() - 1);
        getBestAxe(axe.getIntValue() - 1);
        dropUselessArmor();
    }

    @EventTarget
    public void onWorldChange(EventWorldChange e){
        if(onWorldChange.getValue())
            this.disable();
    }

    @EventTarget
    public void onDeath(EventDeath e){
        if(onDeath.getValue())
            this.disable();
    }
}
