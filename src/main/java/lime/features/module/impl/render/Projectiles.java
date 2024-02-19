package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.util.ArrayList;
import java.util.List;

public class Projectiles extends Module {

    public Projectiles() {
        super("Projectiles", Category.VISUALS);
    }

    @EventTarget
    public void on3D(Event3D e) {
        this.draw(-1);
    }

    public void draw(int color) {
        boolean isBow = false;
        float pitchDifference = 0.0f;
        float motionFactor = 1.5f;
        float motionSlowdown = 0.99f;
        if (mc.thePlayer.getHeldItem() != null) {
            final Item item = mc.thePlayer.getHeldItem().getItem();
            float gravity;
            float size;
            if (item instanceof ItemBow) {
                isBow = true;
                gravity = 0.05f;
                size = 0.3f;
                float power = mc.thePlayer.getItemInUseCount() / 20.0f;
                power = (power * power + power * 2.0f) / 3.0f;
                if (power < 0.1) {
                    return;
                }
                if (power > 1.0f) {
                    power = 1.0f;
                }
                motionFactor = power * 3.0f;
            }
            else if (item instanceof ItemFishingRod) {
                gravity = 0.04f;
                size = 0.25f;
                motionSlowdown = 0.92f;
            }
            else if (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && ItemPotion.isSplash(mc.thePlayer.getHeldItem().getItemDamage())) {
                gravity = 0.05f;
                size = 0.25f;
                pitchDifference = -20.0f;
                motionFactor = 0.5f;
            }
            else {
                if (!(item instanceof ItemSnowball) && !(item instanceof ItemEnderPearl) && !(item instanceof ItemEgg)) {
                    return;
                }
                gravity = 0.03f;
                size = 0.25f;
            }
            double posX = mc.getRenderManager().renderPosX - MathHelper.cos(mc.thePlayer.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
            double posY = mc.getRenderManager().renderPosY + mc.thePlayer.getEyeHeight() - 0.10000000149011612;
            double posZ = mc.getRenderManager().renderPosZ - MathHelper.sin(mc.thePlayer.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
            double motionX = -MathHelper.sin(mc.thePlayer.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            double motionY = -MathHelper.sin((mc.thePlayer.rotationPitch + pitchDifference) / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            double motionZ = MathHelper.cos(mc.thePlayer.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            final float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= motionFactor;
            motionY *= motionFactor;
            motionZ *= motionFactor;
            MovingObjectPosition landingPosition = null;
            boolean hasLanded = false;
            boolean hitEntity = false;
            RenderUtils.enable(true);
            RenderUtils.glColor(color);

            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            final int limit = 0;
            while (!hasLanded && posY > 0.0) {
                Vec3 posBefore = new Vec3(posX, posY, posZ);
                Vec3 posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                landingPosition = mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
                posBefore = new Vec3(posX, posY, posZ);
                posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                if (landingPosition != null) {
                    hasLanded = true;
                    posAfter = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
                }
                final AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
                final List<Entity> entityList = this.getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
                for (int i = 0; i < entityList.size(); ++i) {
                    final Entity possibleEntity = entityList.get(i);
                    if (possibleEntity.canBeCollidedWith() && possibleEntity != mc.thePlayer) {
                        final AxisAlignedBB possibleEntityBoundingBox = possibleEntity.getEntityBoundingBox().expand(size, size, size);
                        final MovingObjectPosition possibleEntityLanding = possibleEntityBoundingBox.calculateIntercept(posBefore, posAfter);
                        if (possibleEntityLanding != null) {
                            hitEntity = true;
                            hasLanded = true;
                            landingPosition = possibleEntityLanding;
                        }
                    }
                }
                posX += motionX;
                posY += motionY;
                posZ += motionZ;
                final BlockPos var18 = new BlockPos(posX, posY, posZ);
                final Block var19 = mc.theWorld.getBlockState(var18).getBlock();
                if (var19.getMaterial() == Material.water) {
                    motionX *= 0.6;
                    motionY *= 0.6;
                    motionZ *= 0.6;
                }
                else {
                    motionX *= motionSlowdown;
                    motionY *= motionSlowdown;
                    motionZ *= motionSlowdown;
                }
                motionY -= gravity;
                GL11.glVertex3d(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);
            }
            GL11.glEnd();
            GL11.glPushMatrix();
            GL11.glTranslated(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);
            if (landingPosition != null) {
                switch (landingPosition.sideHit.getAxis().ordinal()) {
                    case 0: {
                        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                        break;
                    }
                    case 2: {
                        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                        break;
                    }
                }
                if (hitEntity) {
                    // Util3D.color(new Color(color).darker().getRGB());
                    RenderUtils.glColor(0xFFFF0000);
                }
            }
            this.renderPoint();
            GL11.glPopMatrix();
            RenderUtils.disable(true);
        }
    }

    private void renderPoint() {
        GL11.glBegin(1);
        GL11.glVertex3d(-0.5, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, -0.5);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.5, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.5);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glEnd();
        final Cylinder c = new Cylinder();
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.5f, 0.5f, 0.1f, 24, 1);
    }

    private List<Entity> getEntitiesWithinAABB(final AxisAlignedBB axisalignedBB) {
        final List<Entity> list = new ArrayList<Entity>();
        final int chunkMinX = MathHelper.floor_double((axisalignedBB.minX - 2.0) / 16.0);
        final int chunkMaxX = MathHelper.floor_double((axisalignedBB.maxX + 2.0) / 16.0);
        final int chunkMinZ = MathHelper.floor_double((axisalignedBB.minZ - 2.0) / 16.0);
        final int chunkMaxZ = MathHelper.floor_double((axisalignedBB.maxZ + 2.0) / 16.0);
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                mc.theWorld.getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(mc.thePlayer, axisalignedBB, list, null);
            }
        }
        return list;
    }
}
