package lime.module.impl.movement;

import lime.Lime;
import lime.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventSlow;
import lime.module.Module;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {
    public NoSlow(){
        super("NoSlow", 0, Category.MOVEMENT);
        Lime.setmgr.rSetting(new Setting("Mode", this, "NCP", new String[]{"Vanilla", "NCP"}));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onSlow(EventSlow e){
        e.setMoveStafeSlow(1.0f);
        e.setMoveForwardSlow(1.0f);
    }
    @EventTarget
    public void onMotion(EventMotion e){
        if(mc.thePlayer.isUsingItem() && !mc.thePlayer.isRiding() && mc.thePlayer.onGround && getSettingByName("Mode").getValString().equals("NCP") && mc.thePlayer.ticksExisted % 100 == 0){
            mc.playerController.syncCurrentPlayItem();
            if(e.getState() == EventMotion.State.PRE){
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            } else {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
            }

        }
    }
}
