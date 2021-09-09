package lime.utils.combat;

import com.google.common.base.Predicates;
import lime.core.Lime;
import lime.features.module.impl.combat.KillAura;
import lime.utils.IUtil;
import lime.utils.other.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

import java.util.List;

public class CombatUtils implements IUtil {

    public static float[] getRotations(double posX, double posY, double posZ) {
        EntityLivingBase player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - player.posY - 0.5;
        double z = posZ - player.posZ;
        double dist = Math.sqrt(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)-(Math.atan2(y, dist) * 180.0D / Math.PI);
        return new float[] { yaw, pitch };
    }

    public static Rotation getEntityRotations(double d, double d2, double d3) {
        double d4 = d - mc.thePlayer.posX;
        double d5 = d2 - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight() - 0.2);
        double d6 = d3 - mc.thePlayer.posZ;
        double d7 = MathHelper.sqrt_double(d4 * d4 + d6 * d6);
        float f = (float)(Math.atan2(d6, d4) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(-(Math.atan2(d5, d7) * 180.0 / Math.PI));
        return new Rotation(f,f2);
    }

    public static float getRotationFromPosition(final double x, final double z) {
        final double xDiff = x - mc.thePlayer.posX;
        final double zDiff = z - mc.thePlayer.posZ;
        return (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0f;
    }

    public static float getDifference(float a, float b) {
        float r = (float) ((a - b) % 360.0);

        if (r < -180.0) {
            r += 360.0;
        }

        if (r >= 180.0) {
            r -= 360.0;
        }

        return r;
    }

    public static double getRotationDifference(float[] clientRotations, float[] serverRotations) {
        return Math.hypot(getDifference(clientRotations[0], serverRotations[0]), clientRotations[1] - serverRotations[1]);
    }

    public static double getRotationDifference(EntityLivingBase entity) {
        final float[] rotations = getEntityRotations(entity, false);
        return getRotationDifference(rotations, new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch});
    }

    public static float[] getEntityRotations(EntityLivingBase e, boolean random) {
        /*return random ? getRotations(e.posX,
                e.posY + (double)e.getEyeHeight() - 0.4D,
                e.posZ) :
                getRotations(e.posX, e.posY + (double)e.getEyeHeight() - 0.4D, e.posZ);*/

        /*double lastTickPosX = e.ticksExisted > 5 ? e.lastTickPosX : 0;
        double lastTickPosZ = e.ticksExisted > 5 ? e.lastTickPosZ : 0;


        double deltaX = e.posX + (e.posX - lastTickPosX) - mc.thePlayer.posX,
                deltaY = e.posY -3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                deltaZ = e.posZ + (e.posZ - lastTickPosZ) - mc.thePlayer.posZ,
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
                pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 +Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }else if(deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 +Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }*/



        double x = e.posX + (e.ticksExisted > 2 ? (e.posX - e.lastTickPosX) : 0) - mc.thePlayer.posX;
        double z = e.posZ + (e.ticksExisted > 2 ? (e.posZ - e.lastTickPosZ) : 0) - mc.thePlayer.posZ;
        double y = e.posY + e.getEyeHeight() * 0.75D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

        double distance = MathHelper.sqrt_double(x * x + z * z);

        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -((Math.atan2(y, distance) * 180.0D / Math.PI));

        //Rotation rotation = getEntityRotations(e.posX, e.posY, e.posZ);

        KillAura killAura = (KillAura) Lime.getInstance().getModuleManager().getModuleC(KillAura.class);

        return new float[] { yaw + (random ? MathUtils.random(-killAura.randomizeYaw.getCurrent(), killAura.randomizeYaw.getCurrent()) : 0), pitch + (random ? MathUtils.random(-killAura.randomizeYaw.getCurrent(), killAura.randomizeYaw.getCurrent()) : 0) };
    }

    public static Rotation smoothAngle(float[] dst, float[] src, float randMin, float randMax) {
        float[] smoothedAngle = new float[]{src[0] - dst[0], src[1] - dst[1]};
        smoothedAngle = constrainAngle(smoothedAngle);
        smoothedAngle[0] = src[0] - smoothedAngle[0] / 100.0F * MathUtils.random(randMin, randMax);
        smoothedAngle[1] = src[1] - smoothedAngle[1] / 100.0F * MathUtils.random(randMin, randMax);
        return new Rotation(smoothedAngle[0], smoothedAngle[1]);
    }

    public static float[] constrainAngle(float[] vector) {
        vector[0] %= 360.0F;

        for(vector[1] %= 360.0F; vector[0] <= -180.0F; vector[0] += 360.0F) {
        }

        while(vector[1] <= -180.0F) {
            vector[1] += 360.0F;
        }

        while(vector[0] > 180.0F) {
            vector[0] -= 360.0F;
        }

        while(vector[1] > 180.0F) {
            vector[1] -= 360.0F;
        }

        return vector;
    }

    public static final Vec3 getVectorForRotation(float yaw, float pitch)
    {
        final double f = Math.cos(Math.toRadians(-yaw) - Math.PI);
        final double f1 = Math.sin(Math.toRadians(-yaw) - Math.PI);
        final double f2 = -Math.cos(Math.toRadians(-pitch));
        final double f3 = Math.sin(Math.toRadians(-pitch));
        return new Vec3((f1 * f2), f3, (f * f2));
    }

    public static final Entity raycastEntity(double range, float[] rotations)
    {
        final Entity player = mc.getRenderViewEntity();

        if (player != null && mc.theWorld != null)
        {
            final Vec3 eyeHeight = player.getPositionEyes(mc.timer.renderPartialTicks);

            final Vec3 looks = getVectorForRotation(rotations[0], rotations[1]);
            final Vec3 vec = eyeHeight.addVector(looks.xCoord * range, looks.yCoord * range, looks.zCoord * range);
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().addCoord(looks.xCoord * range, looks.yCoord * range, looks.zCoord * range).expand(1, 1, 1), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));

            Entity raycastedEntity = null;

            for (Entity entity : list)
            {
                if (!(entity instanceof EntityLivingBase)) continue;

                final float borderSize = entity.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(borderSize, borderSize, borderSize);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(eyeHeight, vec);

                if (axisalignedbb.isVecInside(eyeHeight))
                {
                    if (range >= 0.0D)
                    {
                        raycastedEntity = entity;
                        range = 0.0D;
                    }
                }
                else if (movingobjectposition != null)
                {
                    double distance = eyeHeight.distanceTo(movingobjectposition.hitVec);

                    if (distance < range || range == 0.0D)
                    {

                        if (entity == player.ridingEntity)
                        {
                            if (range == 0.0D)
                            {
                                raycastedEntity = entity;
                            }
                        }
                        else
                        {
                            raycastedEntity = entity;
                            range = distance;
                        }
                    }
                }
            }
            return raycastedEntity;
        }
        return null;
    }
}
