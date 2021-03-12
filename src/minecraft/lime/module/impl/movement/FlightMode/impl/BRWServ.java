package lime.module.impl.movement.FlightMode.impl;

import lime.Lime;
import lime.events.impl.EventBoundingBox;
import lime.events.impl.EventUpdate;
import lime.module.impl.movement.FlightMode.Flight;
import lime.utils.movement.MovementUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class BRWServ extends Flight {
    public BRWServ(String name){
        super(name);
    }

    @Override
    public void onUpdate(EventUpdate e) {
        if (MovementUtil.isMoving())
            MovementUtil.setSpeed(getSettingByName("Flight").getValDouble());
        super.onUpdate(e);
    }

    @Override
    public void onBB(EventBoundingBox e) {
        if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY)
            e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
        super.onBB(e);
    }
}
