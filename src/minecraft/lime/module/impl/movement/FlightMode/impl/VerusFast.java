package lime.module.impl.movement.FlightMode.impl;

import lime.events.impl.EventBoundingBox;
import lime.events.impl.EventUpdate;
import lime.module.impl.movement.FlightMode.Flight;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class VerusFast extends Flight {
    public VerusFast(String name){
        super(name);
    }

    @Override
    public void onUpdate(EventUpdate e) {
        if(mc.thePlayer.ticksExisted % 20 == 0){
            mc.timer.timerSpeed = 0.5f;
        } else {
            mc.timer.timerSpeed = 3f;
        }
        super.onUpdate(e);
    }

    @Override
    public void onBB(EventBoundingBox e) {
        if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY)
            e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
        super.onBB(e);
    }
}
