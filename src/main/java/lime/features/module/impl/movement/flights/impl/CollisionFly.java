package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventBoundingBox;
import lime.features.module.impl.movement.flights.FlightValue;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class CollisionFly extends FlightValue {
    public CollisionFly()
    {
        super("Verus");
    }

    @Override
    public void onBoundingBox(EventBoundingBox e) {
        if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox())) {
            e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
        }
    }
}
