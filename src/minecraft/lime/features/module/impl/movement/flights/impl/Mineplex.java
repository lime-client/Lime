package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import lime.utils.movement.pathfinder.CustomVec;
import lime.utils.other.InventoryUtils;
import lime.utils.other.MathUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class Mineplex extends FlightValue {
    public Mineplex() {
        super("Mineplex");
    }

    private int stage;
    private int y;
    private double moveSpeed;
    private boolean isBoosted;

    @Override
    public void onEnable() {
        y = (int) mc.thePlayer.posY;
        super.onEnable();
    }

    @Override
    public void onMotion(EventMotion e) {
        int airSlot = 3;
        if(e.isPre()) {
            if(mc.thePlayer.onGround) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(airSlot));
                BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1.0, mc.thePlayer.posZ);
                CustomVec vec = new CustomVec(blockPos.getX(), blockPos.getY(), blockPos.getZ()).addVector(0.4f, 0.4f, 0.4f);
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, null, blockPos, EnumFacing.UP, new Vec3(vec.getX() * 0.4, vec.getY() * 0.4, vec.getZ() * 0.4));
                moveSpeed = MovementUtils.getBaseMoveSpeed();
                MovementUtils.setSpeed(-0.1);
                mc.thePlayer.motionY = 0.14;
            } else {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(MathUtils.random(Float.MIN_VALUE, Float.MAX_VALUE), MathUtils.random(Float.MIN_VALUE, Float.MAX_VALUE), MathUtils.random(Float.MIN_VALUE, Float.MAX_VALUE)), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-5, -5, -5), EnumFacing.DOWN));
                MovementUtils.setSpeed(moveSpeed);
            }
        }
    }


    @Override
    public void onBoundingBox(EventBoundingBox e) {
        if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < y && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox())) {
            e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, y, e.getBlockPos().getZ() + 1));
        }
        super.onBoundingBox(e);
    }
}
