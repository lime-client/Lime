package lime.utils.combat;

import com.google.common.base.Predicates;
import lime.utils.IUtil;
import lime.utils.other.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

import java.util.List;

import static lime.utils.other.MathUtils.random;

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

    public static float[] getEntityRotations(EntityLivingBase e, boolean random) {
        /*return random ? getRotations(e.posX,
                e.posY + (double)e.getEyeHeight() - 0.4D,
                e.posZ) :
                getRotations(e.posX, e.posY + (double)e.getEyeHeight() - 0.4D, e.posZ);*/

        double lastTickPosX = e.ticksExisted > 5 ? e.lastTickPosX : 0;
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
        }



        /*double x = e.posX + (e.ticksExisted > 2 ? (e.posX - e.lastTickPosX) : 0) - mc.thePlayer.posX;
        double z = e.posZ + (e.ticksExisted > 2 ? (e.posZ - e.lastTickPosZ) : 0) - mc.thePlayer.posZ;
        double y = e.posY + e.getEyeHeight() * 0.75D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

        double distance = MathHelper.sqrt_double(x * x + z * z);

        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -((Math.atan2(y, distance) * 180.0D / Math.PI));
        */return new float[] { yaw + (float) Math.random(), pitch };
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
