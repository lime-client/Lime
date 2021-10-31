package lime.utils.movement;

import lime.core.Lime;
import lime.core.events.impl.EventMove;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.movement.TargetStrafe;
import lime.utils.IUtil;
import lime.utils.other.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class MovementUtils implements IUtil {

    public static boolean isVoidUnder() {
        for(int i = (int) mc.thePlayer.posY; i > 0; --i) {
            if(!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }

    public static double getBPS() {
        Vec3 lastPos = new Vec3(mc.thePlayer.lastTickPosX, 0, mc.thePlayer.lastTickPosZ);
        Vec3 pos = new Vec3(mc.thePlayer.posX, 0, mc.thePlayer.posZ);
        return MathUtils.roundToPlace(Math.abs(lastPos.distanceTo(pos) * 20d), 2) * mc.timer.timerSpeed;
    }

    public static void vClip(double number) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + number, mc.thePlayer.posZ);
    }

    public static float getDirection(final EntityLivingBase e) {
        float yaw = e.rotationYaw;
        final float forward = e.moveForward;
        final float strafe = e.moveStrafing;
        yaw += (forward < 0.0F ? 180 : 0);
        final int i = forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45;
        if (strafe < 0.0F) {
            yaw += i;
        }
        if (strafe > 0.0F) {
            yaw -= i;
        }
        return yaw * 0.017453292F;
    }

    public static float getDirection() {
        return getDirection(mc.thePlayer);
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static void hClip(double offset) {
        mc.thePlayer.setPosition(mc.thePlayer.posX + -MathHelper.sin(getDirection()) * offset, mc.thePlayer.posY, mc.thePlayer.posZ + MathHelper.cos(getDirection()) * offset);
    }

    public static void hClipPacket(double offset, boolean ground) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + -MathHelper.sin(getDirection()) * offset, mc.thePlayer.posY, mc.thePlayer.posZ + MathHelper.cos(getDirection()) * offset, ground));
    }

    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (float) (amplifier + 1) * 0.1F;
        }

        return baseJumpHeight;
    }

    public static boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int y = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY); y < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxY) + 1; y++) {
                for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir)) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if (block instanceof BlockHopper)
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        if (boundingBox != null && mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public static void setSpeed(double speed) {
        mc.thePlayer.motionX = -MathHelper.sin(getDirection()) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(getDirection()) * speed;
    }

    public static void setSpeed(final EventMove moveEvent, final double moveSpeed) {
        TargetStrafe targetStrafe = Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
        if(targetStrafe.isToggled() && KillAura.getEntity() != null) {
            targetStrafe.setMoveSpeed(moveEvent, moveSpeed);
        } else {
            setSpeed(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.moveStrafing, mc.thePlayer.movementInput.moveForward);
        }
    }

    public static void strafe() {
        if(!mc.thePlayer.isMoving()) return;
        float yaw = getDirection();
        double speed = MovementUtils.getBaseMoveSpeed();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static void setSpeed(EventMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (pseudoForward != 0.0D) {
            if (pseudoStrafe > 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? -45 : 45);
            } else if (pseudoStrafe < 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;
            if (pseudoForward > 0.0D) {
                forward = 1.0D;
            } else if (pseudoForward < 0.0D) {
                forward = -1.0D;
            }
        }

        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }

        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        moveEvent.setX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.setZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public static double getSpeed(){
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public static boolean hasSpeed() {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed);
    }

    public static double getBaseMoveSpeed(double base) {
        double baseSpeed = base;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }
}
