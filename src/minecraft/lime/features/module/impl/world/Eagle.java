package lime.features.module.impl.world;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

@ModuleData(name = "Eagle", category = Category.WORLD)
public class Eagle extends Module {

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(mc.thePlayer == null || mc.theWorld == null) return;

        ItemStack itemStack = mc.thePlayer.getCurrentEquippedItem();
        BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
        if(itemStack != null && itemStack.getItem() instanceof ItemBlock) {
            mc.gameSettings.keyBindSneak.pressed = mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air;
        }
    }
}
