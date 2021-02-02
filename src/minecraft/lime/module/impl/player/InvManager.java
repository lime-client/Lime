package lime.module.impl.player;


import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventUpdate;
import lime.module.Module;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.ArrayList;

public class InvManager extends Module {
    public InvManager(){
        super("InvManager",0, Module.Category.PLAYER);
        ArrayList<String> mods = new ArrayList<>();
        mods.add("Basic");
        mods.add("OpenInv");
        mods.add("FakeInv");
        Lime.setmgr.rSetting(new Setting("InvManager Mode", this, "Basic", mods));
        Lime.setmgr.rSetting(new Setting("InvManager Delay", this, 500, 150, 1000, true));
        Lime.setmgr.rSetting(new Setting("Max Blocks Stack", this, 3, 1, 5, true));
    }
    private lime.utils.Timer timerDrop = new lime.utils.Timer(), timerSwap = new lime.utils.Timer();

    @Override
    public void onEnable(){
        super.onEnable();
    }
    @Override
    public void onDisable(){super.onDisable();}
    @EventTarget
    public void onUpdate(EventUpdate e){
        timerDelay = (int) Lime.setmgr.getSettingByName("InvManager Delay").getValDouble();
        if(mc.thePlayer.capabilities.isCreativeMode){
            return;
        }
        if(mc.currentScreen instanceof GuiInventory && Lime.setmgr.getSettingByName("InvManager Mode").getValString().equalsIgnoreCase("OpenInv")){
            try{
                getBestSword(36);
            } catch (Exception e1) {}
            try{
                dropRetardItem();
            } catch (Exception e1) {}
            try{
                dropShitArmor();
            } catch (Exception e1) {}
            try{
                getBestPickaxe(37);
            } catch (Exception e1) {}
            try{
                getBestAxe(38);
            } catch (Exception e1) {}
        }
        if(Lime.setmgr.getSettingByName("InvManager Mode").getValString().equalsIgnoreCase("Basic") || Lime.setmgr.getSettingByName("InvManager Mode").getValString().equalsIgnoreCase("FakeInv")){
            try{
                getBestSword(36);
            } catch (Exception e1) {}
            try{
                dropRetardItem();
            } catch (Exception e1) {}
            try{
                dropShitArmor();
            } catch (Exception e1) {}
            try{
                getBestPickaxe(37);
            } catch (Exception e1) {}
            try{
                getBestAxe(38);
            } catch (Exception e1) {}
        }
    }
    int timerDelay = 0;


    public void getBestSword(int slot) {
        for(int i = 9; i < 45; i++) {
            if (i == 36) { i++;}
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack iss = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (iss.getItem() instanceof ItemSword) {
                    if (isBestSword(iss)) {
                        if(Lime.setmgr.getSettingByName("InvManager Mode").getValString().equalsIgnoreCase("FakeInv")){
                            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        }
                        if (mc.thePlayer.inventoryContainer.getSlot(36).getHasStack()) {
                            ItemStack xoof = mc.thePlayer.inventoryContainer.getSlot(36).getStack();
                            if ((getDamage(iss) > getDamage(xoof)) && timerSwap.hasReached((timerDelay))) {
                                swap(i, slot - 36);
                                timerSwap.reset();
                            } else if ((getDamage(iss) < getDamage(xoof)) && timerDrop.hasReached(timerDelay)) {
                                drop(i);
                                timerDrop.reset();
                            } else if (getDamage(iss) == getDamage(xoof)) {
                                if ((xoof.getItemDamage() > iss.getItemDamage()) && timerSwap.hasReached(timerDelay)) {
                                    swap(i, slot - 36);
                                    timerSwap.reset();
                                } else if ((xoof.getItemDamage() < iss.getItemDamage()) && timerDrop.hasReached(timerDelay)) {
                                    drop(i);
                                    timerDrop.reset();
                                } else if ((xoof.getItemDamage() == iss.getItemDamage()) && timerDrop.hasReached(timerDelay)) {
                                    drop(i);
                                    timerDrop.reset();
                                }
                            }
                        } else {
                            if(timerSwap.hasReached(timerDelay)){
                                swap(i, slot - 36);
                                timerSwap.reset();
                            }
                        }
                    } else if ((!isBestSword(iss) && iss.getItem() instanceof ItemSword) && timerDrop.hasReached(timerDelay)) {
                        drop(i);
                        timerDrop.reset();
                    }
                }
            }

        }
    }
    public void getBestPickaxe(int slot) {
        for(int i = 9; i < 45; i++){
            if(i == 37){
                i++;
            }
            if(mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack iss = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (iss.getItem() instanceof ItemPickaxe) {
                    if (isBestPickaxe(iss)) {
                        if(Lime.setmgr.getSettingByName("InvManager Mode").getValString().equalsIgnoreCase("FakeInv")){
                            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        }
                        if(mc.thePlayer.inventoryContainer.getSlot(37).getHasStack()){
                            ItemStack xoof = mc.thePlayer.inventoryContainer.getSlot(37).getStack();
                            if((getPickaxeDamage(iss) > getPickaxeDamage(xoof)) && timerSwap.hasReached(timerDelay)){
                                swap(i, slot - 36);
                                timerSwap.reset();
                            } else if((getPickaxeDamage(iss) < getPickaxeDamage(xoof)) && timerDrop.hasReached(timerDelay)){
                                drop(i);
                                timerDrop.reset();
                            } else if(getPickaxeDamage(iss) == getPickaxeDamage(xoof)){
                                if((xoof.getItemDamage() > iss.getItemDamage()) && timerSwap.hasReached(timerDelay)){
                                    swap(i, slot - 36);
                                    timerSwap.reset();
                                } else if((xoof.getItemDamage() < iss.getItemDamage()) && timerDrop.hasReached(timerDelay)){
                                    drop(i);
                                    timerDrop.reset();
                                } else if((xoof.getItemDamage() == iss.getItemDamage() && timerDrop.hasReached(timerDelay))){
                                    drop(i);
                                    timerDrop.reset();
                                }
                            }
                        } else {
                            if(timerSwap.hasReached(timerDelay)){
                                swap(i, slot - 36);
                                timerSwap.reset();
                            }
                        }
                    } else {
                        if(timerDrop.hasReached(timerDelay)){
                            drop(i);
                            timerDrop.reset();
                        }
                    }
                }
            }
        }
    }


    public void getBestAxe(int slot) {
        for(int i = 9; i < 45; i++){
            if(i == 38){
                i++;
            }
            if(mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack iss = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (iss.getItem() instanceof ItemAxe) {
                    if (isBestAxe(iss)) {
                        if(Lime.setmgr.getSettingByName("InvManager Mode").getValString().equalsIgnoreCase("FakeInv")){
                            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        }
                        if(mc.thePlayer.inventoryContainer.getSlot(38).getHasStack()){
                            ItemStack xoof = mc.thePlayer.inventoryContainer.getSlot(38).getStack();
                            if((getAxeDamage(iss) > getAxeDamage(xoof)) && timerSwap.hasReached(timerDelay)){
                                swap(i, slot - 36);
                                timerSwap.reset();
                            } else if(getAxeDamage(iss) < getAxeDamage(xoof) && timerDrop.hasReached(timerDelay)){
                                drop(i);
                                timerDrop.reset();
                            } else if(getAxeDamage(iss) == getAxeDamage(xoof)){
                                if(xoof.getItemDamage() > iss.getItemDamage() && timerSwap.hasReached(timerDelay)){
                                    swap(i, slot - 36);
                                    timerSwap.reset();
                                } else if(xoof.getItemDamage() < iss.getItemDamage() && timerDrop.hasReached(timerDelay)){
                                    drop(i);
                                    timerDrop.reset();
                                } else if(xoof.getItemDamage() == iss.getItemDamage() && timerDrop.hasReached(timerDelay)){
                                    drop(i);
                                    timerDrop.reset();
                                }
                            }
                        } else {
                            if(timerSwap.hasReached(timerDelay)){
                                swap(i, slot - 36);
                                timerSwap.reset();
                            }
                        }
                    } else {
                        if(timerDrop.hasReached(timerDelay)){
                            drop(i);
                            timerDrop.reset();
                        }
                    }
                }
            }

        }
    }

    public boolean isBestAxe(ItemStack stack){
        float damage = getAxeDamage(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getAxeDamage(is) > damage && (is.getItem() instanceof ItemAxe))
                    return false;
            }
        }
        if((stack.getItem() instanceof ItemAxe)){
            return true;
        }else{
            return false;
        }

    }

    public float getAxeDamage(ItemStack s){
        if(s.getItem() instanceof ItemAxe){
            float value = 0;
            Item item = s.getItem();
            ItemTool tool = (ItemTool) item;

            value += (float) tool.getMaxDamage();
            if(tool.getToolMaterialName().contains("WOOD")){
                value += 0.1;
            }
            value += EnchantmentHelper.getEnchantmentLevel(32, s) * 1.25f +
                    EnchantmentHelper.getEnchantmentLevel(34, s) * 0.25f;
            return value;
        } else {
            return 0;
        }

    }

    public boolean isBestSword(ItemStack stack){
        float damage = getDamage(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getDamage(is) > damage && (is.getItem() instanceof ItemSword))
                    return false;
            }
        }
        if((stack.getItem() instanceof ItemSword)){
            return true;
        }else{
            return false;
        }

    }

    public boolean isBestPickaxe(ItemStack stack){
        float damage = getPickaxeDamage(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getPickaxeDamage(is) > damage && (is.getItem() instanceof ItemPickaxe))
                    return false;
            }
        }
        if((stack.getItem() instanceof ItemPickaxe)){
            return true;
        }else{
            return false;
        }

    }

    public float getPickaxeDamage(ItemStack s){
        if(s.getItem() instanceof ItemPickaxe){
            float value = 0;
            Item item = s.getItem();
            ItemTool tool = (ItemTool) item;

            value += (float) tool.getMaxDamage();
            if(tool.getToolMaterialName().contains("WOOD")){
                value += 0.1;
            }
            value += EnchantmentHelper.getEnchantmentLevel(32, s) * 1.25f +
                    EnchantmentHelper.getEnchantmentLevel(34, s) * 0.25f;
            return value;
        } else {
            return 0;
        }

    }






    public float getDamage(ItemStack stack){
        float damage = 0;
        Item item = stack.getItem();
        if(item instanceof ItemTool){
            ItemTool tool = (ItemTool)item;
            damage += tool.getMaxDamage();
        }
        if(item instanceof ItemSword){
            ItemSword sword = (ItemSword)item;
            damage += sword.getDamageVsEntity();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(16, stack) * 1.25f +
                EnchantmentHelper.getEnchantmentLevel(20, stack) * 0.01f;
        return damage;

    }

    public void swap(int slot1, int hotbarSlot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }






    public void dropRetardItem(){
        boolean alreadyFoundABow = false;
        int totalBlocksFounds = 0;
        for(int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack iss = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item e = iss.getItem();
                if (!(e instanceof ItemPickaxe) && !(e instanceof ItemAxe) && !(e instanceof ItemSword) && !(e instanceof ItemFood) && !(e instanceof ItemAppleGold) && !(e instanceof ItemPotion)  && !e.getUnlocalizedName().toLowerCase().contains("arrow") && !(e instanceof ItemArmor)) {
                    if(e instanceof ItemBow && !alreadyFoundABow){
                        alreadyFoundABow = true;
                        continue;
                    }
                    if(e instanceof ItemBlock){
                        if(totalBlocksFounds < (getSettingByName("Max Blocks Stack").getValDouble() * 64)){
                            totalBlocksFounds += iss.stackSize;
                            continue;
                        }
                    }
                    if (shouldDrop(iss)) {
                        if(timerDrop.hasReached(timerDelay)){
                            if(Lime.setmgr.getSettingByName("InvManager Mode").getValString().equalsIgnoreCase("FakeInv")){
                                mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                            }
                            drop(i);
                            timerDrop.reset();
                        }
                    }
                }


            }


        }
    }
    public boolean shouldDrop(ItemStack xd){
        if(xd.getDisplayName().contains("right click") || xd.getDisplayName().contains("§")){
            return false;
        }
        return true;
    }








    public void drop(int slot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }
    public void dropShitArmor(){
        if(!Lime.moduleManager.getModuleByName("AutoArmor").isToggled()){
            return;
        }
        int type = 0;
        for(int i = 9; i < 45; i++) {
            if(mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()){
                ItemStack ntm = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(ntm.getUnlocalizedName().contains("helmet")){
                    type = 1;
                } else if(ntm.getUnlocalizedName().contains("chestplate")){
                    type = 2;
                } else if(ntm.getUnlocalizedName().contains("leggings")){
                    type = 3;
                } else if (ntm.getUnlocalizedName().contains("boots")){
                    type = 4;
                }
                if(ntm.getItem() instanceof ItemArmor && !AutoArmor.isBestArmor(ntm, type)){
                    if(timerDrop.hasReached(timerDelay)){
                        if(Lime.setmgr.getSettingByName("InvManager Mode").getValString().equalsIgnoreCase("FakeInv")){
                            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        }
                        drop(i);
                        timerDrop.reset();
                    }
                } else if(ntm.getItem() instanceof ItemArmor && !AutoArmor.isBestArmor(ntm, type) && (AutoArmor.getProtection(ntm) == AutoArmor.getProtection(mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack()) && ntm.getUnlocalizedName() == mc.thePlayer.inventoryContainer.getSlot(4 + type ).getStack().getUnlocalizedName())){
                    if(timerDrop.hasReached(timerDelay)){
                        drop(i);
                        timerDrop.reset();
                    }
                }
            }
        }
    }
}