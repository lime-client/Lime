package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;

public class VerusNoDamageFly extends FlightValue {
    public VerusNoDamageFly() {
        super("Verus_No_Damage");
    }

    private int y;

    @Override
    public void onEnable() {
        y = (int) mc.thePlayer.posY;
    }


    @Override
    public void onMotion(EventMotion e) {

        if(mc.thePlayer.motionY > 0.2) {
            mc.thePlayer.motionY = -0.0784000015258789;
        }

        if (mc.thePlayer.isMoving()) {
            double amplifier = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() : 0;
            double speedBoost = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? amplifier == 1 ? 0.035 : amplifier > 1 ? 0.035 * (amplifier / 2) : 0.035 / 2 : 0;
            double motionBoost = MovementUtils.isOnGround(0.15) && !mc.thePlayer.onGround ? 0.045 : 0;

            double boost = 0;
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                boost += 0.125;
            }

            if(MovementUtils.isOnGround(0.15) && boost == 0) {
                mc.thePlayer.motionY -= 0.0075;
            }

            if(mc.thePlayer.moveStrafing == 0)
                MovementUtils.setSpeed(0.3345 + speedBoost + motionBoost + boost);
            else
                MovementUtils.setSpeed(0.333 + speedBoost + motionBoost + boost);

        } else
            MovementUtils.setSpeed(0);
    }

    @Override
    public void onBoundingBox(EventBoundingBox e) {
        if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < y && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox())) {
            e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, y, e.getBlockPos().getZ() + 1));
        }
    }
}
