package lime.module.impl.movement.SpeedMode.impl;

import lime.events.impl.EventBoundingBox;
import lime.events.impl.EventMotion;
import lime.module.impl.movement.SpeedMode.Speed;
import lime.utils.Timer;
import lime.utils.movement.MovementUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class Verus extends Speed {
    public Verus(String name){
        super(name);
    }
    Timer timer = new Timer();
    Timer timer2 = new Timer();

    @Override
    public void onEnable() {
        timer.reset();
        super.onEnable();
    }

    @Override
    public void onMotion(EventMotion e) {
        super.onMotion(e);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onBB(EventBoundingBox e) {

        if(mc.thePlayer.onGround) {
            timer.reset();
            if(!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)){
                MovementUtil.vClip(1);
            }
        }
        if(timer2.hasReached(2500)){
            mc.thePlayer.motionY -= 0.12;
            timer2.reset();
        }
        if(!timer.hasReached(500) && !timer2.hasReached(2500) && timer2.hasReached(300)){

            if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY)
                e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
            if(mc.thePlayer.ticksExisted % 20 == 0){
                mc.timer.timerSpeed = 0.5f;
            } else {
                mc.timer.timerSpeed = 2.5f;
            }
        }

        super.onBB(e);
    }
}
