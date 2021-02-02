package lime.module.impl.player;


import lime.Lime;
import lime.events.EventTarget;
import lime.events.impl.EventUpdate;
import lime.module.Module;
import lime.cgui.settings.Setting;
import lime.utils.Timer;
import lime.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.ArrayList;

public class AutoArmor extends Module {
    public AutoArmor(){
        super("AutoArmor", 0, Category.PLAYER);
        ArrayList<String> f = new ArrayList<>();
        f.add("OpenInv");
        f.add("FakeInv");
        f.add("Basic");
        Lime.setmgr.rSetting(new Setting("AutoArmor", this, "FakeInv", f));
    }
    Timer timer = new Timer();
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    @EventTarget
    public void onEvent(EventUpdate event) {
        int delay = 300;
        if(timer.hasReached(delay)){
            if(getSettingByName("AutoArmor").getValString().equalsIgnoreCase("OpenInv") && (mc.currentScreen instanceof GuiInventory)){
                getBestArmor();
            }
            if(mc.currentScreen instanceof GuiChat){
                return;
            }
            if(Lime.setmgr.getSettingByName("AutoArmor").getValString().equalsIgnoreCase("Basic")){
                getBestArmor();
            }
            if(Lime.setmgr.getSettingByName("AutoArmor").getValString().equalsIgnoreCase("FakeInv")){
                getBestArmor();
            }
            timer.reset();
        }


    }

    public void getBestArmor(){
        try{
            for(int type = 1; type < 5; type++){
                if(mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()){
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                    if(isBestArmor(is, type)){
                        continue;
                    }else{
                        if(getSettingByName("AutoArmor").getValString().equalsIgnoreCase("FakeInv")){
                            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        }
                        drop(4 + type);
                    }
                }
                for (int i = 9; i < 45; i++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if(isBestArmor(is, type) && getProtection(is) > 0){
                            shiftClick(i);
                            timer.reset();
                        }
                    }
                }
            }
        } catch (Exception ignored){}
    }
    public static boolean isBestArmor(ItemStack stack, int type){
        Minecraft mc = Minecraft.getMinecraft();
        float prot = getProtection(stack);
        String strType = "";
        if(type == 1){
            strType = "helmet";
        }else if(type == 2){
            strType = "chestplate";
        }else if(type == 3){
            strType = "leggings";
        }else if(type == 4){
            strType = "boots";
        }
        if(!stack.getUnlocalizedName().contains(strType)){
            return false;
        }
        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                    return false;
            }
        }
        return true;
    }
    public void shiftClick(int slot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public void drop(int slot){
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }
    public static float getProtection(ItemStack stack){
        float prot = 0;
        if ((stack.getItem() instanceof ItemArmor)) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(0, stack) * 0.0075D;
            prot += EnchantmentHelper.getEnchantmentLevel(3, stack)/100d;
            prot += EnchantmentHelper.getEnchantmentLevel(1, stack)/100d;
            prot += EnchantmentHelper.getEnchantmentLevel(7, stack)/100d;
            prot += EnchantmentHelper.getEnchantmentLevel(34, stack)/50d;
            //prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180308_g.effectId, stack)/100d;
        }
        return prot;
    }
}
