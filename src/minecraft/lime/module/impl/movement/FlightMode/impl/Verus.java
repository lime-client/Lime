package lime.module.impl.movement.FlightMode.impl;

import lime.events.impl.EventBoundingBox;
import lime.module.impl.movement.FlightMode.Flight;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class Verus extends Flight {
    public Verus(String name){
        super(name);
    }

    @Override
    public void onBB(EventBoundingBox e) {
        if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY)
            e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
        super.onBB(e);
    }
}
