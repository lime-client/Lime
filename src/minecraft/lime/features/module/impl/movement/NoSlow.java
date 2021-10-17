package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventSlow;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {

    public NoSlow() {
        super("No Slow", Category.MOVE);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Vanilla", "Vanilla", "NCP");
    private final BoolValue swords = new BoolValue("Swords", this, true);
    private final BoolValue consumables = new BoolValue("Consumables", this, true);
    private final BoolValue potions = new BoolValue("Potions", this, true);
    private final BoolValue bow = new BoolValue("Bow", this, true);

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(mc.thePlayer.isBlocking() && mode.is("ncp") && isValid(mc.thePlayer.getHeldItem())) {
            if(e.isPre())
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            else
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
        }
    }

    @EventTarget
    public void onSlow(EventSlow e) {
        if(isValid(mc.thePlayer.getHeldItem())) {
            e.setMoveForward(1);
            e.setMoveStrafing(1);
        }
    }

    public boolean isValid(ItemStack is) {
        return is != null && ((is.getItem() instanceof ItemSword && swords.isEnabled()) || (is.getItem() instanceof ItemFood && consumables.isEnabled()) || (is.getItem() instanceof ItemBow && bow.isEnabled()) || (is.getItem() instanceof ItemPotion && potions.isEnabled()));
    }
}
