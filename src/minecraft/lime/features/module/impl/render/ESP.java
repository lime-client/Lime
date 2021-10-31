package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.AntiBot;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.render.ColorUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.*;

public class ESP extends Module {

    public ESP() {
        super("ESP", Category.VISUALS);
        this.collectedEntities = new ArrayList<>();
        this.viewport = GLAllocation.createDirectIntBuffer(16);
        this.modelview = GLAllocation.createDirectFloatBuffer(16);
        this.projection = GLAllocation.createDirectFloatBuffer(16);
        this.vector = GLAllocation.createDirectFloatBuffer(4);
        this.color = Color.WHITE.getRGB();
        this.backgroundColor = (new Color(0, 0, 0, 120)).getRGB();
        this.black = Color.BLACK.getRGB();
    }

    public final EnumProperty boxMode = new EnumProperty("Box Mode", this, "Box", "Box", "Corners");
    public final BooleanProperty healthBar = new BooleanProperty("Health bar", this, true);
    public final BooleanProperty you = new BooleanProperty("You", this,true);
    public final BooleanProperty players = new BooleanProperty("Players", this,true);
    public final BooleanProperty invisibles = new BooleanProperty("Invisibles", this,false);
    public final BooleanProperty mobs = new BooleanProperty("Mobs", this,true);
    public final BooleanProperty animals = new BooleanProperty("Animals", this,false);
    public final BooleanProperty items = new BooleanProperty("Items", this,false);
    public final java.util.List<Entity> collectedEntities;
    private final IntBuffer viewport;
    private final FloatBuffer modelview;
    private final FloatBuffer projection;
    private final FloatBuffer vector;
    private final int color;
    private final int backgroundColor;
    private final int black;
    private final BooleanProperty skeleton = new BooleanProperty("Skeleton", this, false);

    private static final Map<EntityPlayer, float[][]> entities = new HashMap<>();
    private final NumberProperty width = new NumberProperty("Width", this, 0.5, 10, 1, 0.1).onlyIf(skeleton.getSettingName(), "bool", "true");

    @EventTarget
    public void onRender(Event2D event) {
        GL11.glPushMatrix();
        this.collectEntities();
        float partialTicks = event.getPartialTicks();
        ScaledResolution scaledResolution = event.getScaledResolution();
        int scaleFactor = scaledResolution.getScaleFactor();
        double scaling = (double)scaleFactor / Math.pow(scaleFactor, 2.0D);
        GL11.glScaled(scaling, scaling, scaling);
        RenderManager renderMng = mc.getRenderManager();
        boolean health = this.healthBar.isEnabled();
        int i = 0;

        for(int collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; ++i) {
            Entity entity = collectedEntities.get(i);
            if (this.isValid(entity) && RenderUtils.isInViewFrustrum(entity.getEntityBoundingBox())) {
                double x = RenderUtils.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                double y = RenderUtils.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                double z = RenderUtils.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                double width = (double)entity.width / 1.5D;
                double height = (double)entity.height + (entity.isSneaking() ? -0.3D : 0.2D);
                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                java.util.List<javax.vecmath.Vector3d> vectors = Arrays.asList(new javax.vecmath.Vector3d(aabb.minX, aabb.minY, aabb.minZ), new javax.vecmath.Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new javax.vecmath.Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new javax.vecmath.Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new javax.vecmath.Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new javax.vecmath.Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new javax.vecmath.Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new javax.vecmath.Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                mc.entityRenderer.setupCameraTransform(partialTicks, 0);
                Vector4d position = null;
                for (javax.vecmath.Vector3d vector3d : vectors) {
                    vector3d = this.project2D(scaleFactor, vector3d.x - renderMng.viewerPosX, vector3d.y - renderMng.viewerPosY, vector3d.z - renderMng.viewerPosZ);
                    if (vector3d != null && vector3d.z >= 0.0D && vector3d.z < 1.0D) {
                        if (position == null) {
                            position = new Vector4d(vector3d.x, vector3d.y, vector3d.z, 0.0D);
                        }

                        position.x = Math.min(vector3d.x, position.x);
                        position.y = Math.min(vector3d.y, position.y);
                        position.z = Math.max(vector3d.x, position.z);
                        position.w = Math.max(vector3d.y, position.w);
                    }
                }

                if (position != null) {
                    mc.entityRenderer.setupOverlayRendering();
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;
                    double endPosY = position.w;
                    if (boxMode.is("box")) {
                        Gui.drawRect(posX - 1.0D, posY, posX + 0.5D, endPosY + 0.5D, black);
                        Gui.drawRect(posX - 1.0D, posY - 0.5D, endPosX + 0.5D, posY + 0.5D + 0.5D, black);
                        Gui.drawRect(endPosX - 0.5D - 0.5D, posY, endPosX + 0.5D, endPosY + 0.5D, black);
                        Gui.drawRect(posX - 1.0D, endPosY - 0.5D - 0.5D, endPosX + 0.5D, endPosY + 0.5D, black);
                        Gui.drawRect(posX - 0.5D, posY, posX + 0.5D - 0.5D, endPosY, color);
                        Gui.drawRect(posX, endPosY - 0.5D, endPosX, endPosY, color);
                        Gui.drawRect(posX - 0.5D, posY, endPosX, posY + 0.5D, color);
                        Gui.drawRect(endPosX - 0.5D, posY, endPosX, endPosY, color);
                    } else {
                        Gui.drawRect(posX + 0.5D, posY, posX - 1.0D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                        Gui.drawRect(posX - 1.0D, endPosY, posX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                        Gui.drawRect(posX - 1.0D, posY - 0.5D, posX + (endPosX - posX) / 3.0D + 0.5D, posY + 1.0D, black);
                        Gui.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, posY - 0.5D, endPosX, posY + 1.0D, black);
                        Gui.drawRect(endPosX - 1.0D, posY, endPosX + 0.5D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                        Gui.drawRect(endPosX - 1.0D, endPosY, endPosX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                        Gui.drawRect(posX - 1.0D, endPosY - 1.0D, posX + (endPosX - posX) / 3.0D + 0.5D, endPosY + 0.5D, black);
                        Gui.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, endPosY - 1.0D, endPosX + 0.5D, endPosY + 0.5D, black);
                        Gui.drawRect(posX, posY, posX - 0.5D, posY + (endPosY - posY) / 4.0D, color);
                        Gui.drawRect(posX, endPosY, posX - 0.5D, endPosY - (endPosY - posY) / 4.0D, color);
                        Gui.drawRect(posX - 0.5D, posY, posX + (endPosX - posX) / 3.0D, posY + 0.5D, color);
                        Gui.drawRect(endPosX - (endPosX - posX) / 3.0D, posY, endPosX, posY + 0.5D, color);
                        Gui.drawRect(endPosX - 0.5D, posY, endPosX, posY + (endPosY - posY) / 4.0D, color);
                        Gui.drawRect(endPosX - 0.5D, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0D, color);
                        Gui.drawRect(posX, endPosY - 0.5D, posX + (endPosX - posX) / 3.0D, endPosY, color);
                        Gui.drawRect(endPosX - (endPosX - posX) / 3.0D, endPosY - 0.5D, endPosX - 0.5D, endPosY, color);
                    }

                    boolean living = entity instanceof EntityLivingBase;
                    EntityLivingBase entityLivingBase;
                    float armorValue;
                    float itemDurability;
                    double durabilityWidth;
                    double textWidth;
                    float tagY;
                    if (living) {
                        entityLivingBase = (EntityLivingBase)entity;
                        if (health) {
                            armorValue = entityLivingBase.getHealth();
                            itemDurability = entityLivingBase.getMaxHealth();
                            if (armorValue > itemDurability) {
                                armorValue = itemDurability;
                            }

                            durabilityWidth = armorValue / itemDurability;
                            textWidth = (endPosY - posY) * durabilityWidth;
                            Gui.drawRect(posX - 3.5D, posY - 0.5D, posX - 1.5D, endPosY + 0.5D, backgroundColor);
                            if (armorValue > 0.0F) {
                                int healthColor = ColorUtils.getHealthColor(armorValue, itemDurability).getRGB();
                                Gui.drawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - textWidth, healthColor);
                                tagY = entityLivingBase.getAbsorptionAmount();
                                if (tagY > 0.0F) {
                                    Gui.drawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - (endPosY - posY) / 6.0D * (double)tagY / 2.0D, (new Color(Potion.absorption.getLiquidColor())).getRGB());
                                }
                            }
                        }
                    }
                }
            }
        }

        GL11.glPopMatrix();
        GlStateManager.enableBlend();
        mc.entityRenderer.setupOverlayRendering();
    }

    private void collectEntities() {
        this.collectedEntities.clear();
        List<Entity> playerEntities = mc.theWorld.loadedEntityList;
        int i = 0;

        for (Entity entity : playerEntities) {
            if (this.isValid(entity)) {
                this.collectedEntities.add(entity);
            }
        }
    }

    private javax.vecmath.Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, this.modelview, this.projection, this.viewport, this.vector) ? new javax.vecmath.Vector3d((this.vector.get(0) / (float)scaleFactor), (((float) Display.getHeight() - this.vector.get(1)) / (float)scaleFactor), this.vector.get(2)) : null;
    }

    private boolean isValid(Entity entity) {
        if(entity instanceof EntityPlayer && Lime.getInstance().getModuleManager().getModuleC(AntiBot.class).isToggled()  && ((AntiBot) Lime.getInstance().getModuleManager().getModuleC(AntiBot.class)).checkBot((EntityPlayer) entity)) {
            return false;
        }
        if (entity != mc.thePlayer || this.you.isEnabled() && mc.gameSettings.thirdPersonView != 0) {
            if (entity.isDead) {
                return false;
            } else if (!this.invisibles.isEnabled() && entity.isInvisible()) {
                return false;
            } else if (this.items.isEnabled() && entity instanceof EntityItem && mc.thePlayer.getDistanceToEntity(entity) < 10.0F) {
                return true;
            } else if (this.animals.isEnabled() && entity instanceof EntityAnimal) {
                return true;
            } else if (this.players.isEnabled() && entity instanceof EntityPlayer) {
                return true;
            } else {
                return this.mobs.isEnabled() && (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityDragon || entity instanceof EntityGolem);
            }
        } else {
            return false;
        }
    }

    @EventTarget
    public void on3D(Event3D e) {

        if(skeleton.isEnabled())
        {
            startEnd(true);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glDisable(2848);
            mc.theWorld.playerEntities.forEach(player -> drawSkeleton(e, player));
            GlStateManager.color(1, 1, 1, 1);
            startEnd(false);
        }
    }

    private void drawSkeleton(Event3D event, EntityPlayer e) {
        final Color color = new Color(e.getName().equalsIgnoreCase(mc.thePlayer.getName()) ? 0xFF99ff99 : new Color(255, 255, 255).getRGB());
        if (!e.isInvisible()) {
            float[][] entPos = entities.get(e);
            if (entPos != null && e.getEntityId() != -1488 && RenderUtils.isInViewFrustrum(e.getEntityBoundingBox()) && !e.isDead && e != mc.thePlayer && !e.isPlayerSleeping() && isValid(e)) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glLineWidth((float) width.getCurrent());
                GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
                Vec3 vec = getVec3(event, e);
                double x = vec.xCoord - mc.getRenderManager().renderPosX;
                double y = vec.yCoord - mc.getRenderManager().renderPosY;
                double z = vec.zCoord - mc.getRenderManager().renderPosZ;
                GL11.glTranslated(x, y, z);
                float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
                GL11.glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
                GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? -0.235D : 0.0D);
                float yOff = e.isSneaking() ? 0.6F : 0.75F;
                GL11.glPushMatrix();
                GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
                GL11.glTranslated(-0.125D, yOff, 0.0D);
                if (entPos[3][0] != 0.0F) {
                    GL11.glRotatef(entPos[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (entPos[3][1] != 0.0F) {
                    GL11.glRotatef(entPos[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (entPos[3][2] != 0.0F) {
                    GL11.glRotatef(entPos[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, (-yOff), 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
                GL11.glTranslated(0.125D, yOff, 0.0D);
                if (entPos[4][0] != 0.0F) {
                    GL11.glRotatef(entPos[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (entPos[4][1] != 0.0F) {
                    GL11.glRotatef(entPos[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (entPos[4][2] != 0.0F) {
                    GL11.glRotatef(entPos[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, (-yOff), 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? 0.25D : 0.0D);
                GL11.glPushMatrix();
                GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
                GL11.glTranslated(0.0D, e.isSneaking() ? -0.05D : 0.0D, e.isSneaking() ? -0.01725D : 0.0D);
                GL11.glPushMatrix();
                GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
                GL11.glTranslated(-0.375D, yOff + 0.55D, 0.0D);
                if (entPos[1][0] != 0.0F) {
                    GL11.glRotatef(entPos[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (entPos[1][1] != 0.0F) {
                    GL11.glRotatef(entPos[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (entPos[1][2] != 0.0F) {
                    GL11.glRotatef(-entPos[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, -0.5D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.375D, yOff + 0.55D, 0.0D);
                if (entPos[2][0] != 0.0F) {
                    GL11.glRotatef(entPos[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (entPos[2][1] != 0.0F) {
                    GL11.glRotatef(entPos[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (entPos[2][2] != 0.0F) {
                    GL11.glRotatef(-entPos[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, -0.5D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glRotatef(xOff - e.rotationYawHead, 0.0F, 1.0F, 0.0F);
                GL11.glPushMatrix();
                GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
                GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
                if (entPos[0][0] != 0.0F) {
                    GL11.glRotatef(entPos[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, 0.3D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glRotatef(e.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslated(0.0D, e.isSneaking() ? -0.16175D : 0.0D, e.isSneaking() ? -0.48025D : 0.0D);
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, yOff, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
                GL11.glVertex3d(0.125D, 0.0D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
                GL11.glTranslated(0.0D, yOff, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, 0.55D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
                GL11.glVertex3d(0.375D, 0.0D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
        }
    }

    public static void addEntity(EntityPlayer e, ModelPlayer model) {
        entities.put(e, new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
    }

    private Vec3 getVec3(Event3D event, EntityPlayer var0) {
        float pt = event.getPartialTicks();
        double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * pt;
        double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * pt;
        double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * pt;
        return new Vec3(x, y, z);
    }

    private void startEnd(boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(!revert);
    }

    public void drawBox(double x, double y, double z, double factor, double yOffset) {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x - factor, y, z + factor);
        GL11.glVertex3d(x + factor, y, z + factor);
        GL11.glVertex3d(x + factor, y, z - factor);
        GL11.glVertex3d(x - factor, y, z - factor);
        GL11.glVertex3d(x - factor, y, z + factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x - factor, y + yOffset, z + factor);
        GL11.glVertex3d(x + factor, y + yOffset, z + factor);
        GL11.glVertex3d(x + factor, y + yOffset, z - factor);
        GL11.glVertex3d(x - factor, y + yOffset, z - factor);
        GL11.glVertex3d(x - factor, y + yOffset, z + factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x - factor, y, z + factor);
        GL11.glVertex3d(x - factor, y + yOffset, z + factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x + factor, y, z + factor);
        GL11.glVertex3d(x + factor, y + yOffset, z + factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x - factor, y, z - factor);
        GL11.glVertex3d(x - factor, y + yOffset, z - factor);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x + factor, y, z - factor);
        GL11.glVertex3d(x + factor, y + yOffset, z - factor);
        GL11.glEnd();
    }

    public void drawCylinder(double x, double y, double z, double factor, double yOffset) {
        factor += 0.3;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i = 0; i < 361; i++) {
            GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * factor, y, z - Math.sin(Math.toRadians(i)) * factor);
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i = 0; i < 361; i++) {
            GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * factor, y + yOffset, z - Math.sin(Math.toRadians(i)) * factor);
        }
        GL11.glEnd();

        for (int i = 0; i < 361; i++) {
            if(i == 90 || i == 90 + 90 || i == 90 + 90 + 90 || i == 90 + 90 + 90 + 90) {
                GL11.glBegin(GL11.GL_LINE_STRIP);
                final double v = Math.cos(Math.toRadians(i)) * factor;
                final double v1 = Math.sin(Math.toRadians(i)) * factor;
                GL11.glVertex3d(x + v, y, z - v1);
                GL11.glVertex3d(x + v, y + yOffset, z - v1);
                GL11.glEnd();
            }
        }
    }
}
