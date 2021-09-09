package lime.features.module.impl.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventLivingLabel;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.utils.render.ColorUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
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
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ESP2D extends Module {
    public final EnumValue boxMode = new EnumValue("Box Mode", this, "Box", "Box", "Corners");
    public final BoolValue healthBar = new BoolValue("Health bar", this, true);
    public final BoolValue armorBar = new BoolValue("Armor bar", this,true);
    public final BoolValue you = new BoolValue("You", this,true);
    public final BoolValue players = new BoolValue("Players", this,true);
    public final BoolValue invisibles = new BoolValue("Invisibles", this,false);
    public final BoolValue mobs = new BoolValue("Mobs", this,true);
    public final BoolValue animals = new BoolValue("Animals", this,false);
    public final BoolValue items = new BoolValue("Items", this,false);
    public final List<Entity> collectedEntities;
    private final IntBuffer viewport;
    private final FloatBuffer modelview;
    private final FloatBuffer projection;
    private final FloatBuffer vector;
    private final int color;
    private final int backgroundColor;
    private final int black;
    
    public ESP2D() {
        super("ESP2D", 0, Category.RENDER);
        this.collectedEntities = new ArrayList<>();
        this.viewport = GLAllocation.createDirectIntBuffer(16);
        this.modelview = GLAllocation.createDirectFloatBuffer(16);
        this.projection = GLAllocation.createDirectFloatBuffer(16);
        this.vector = GLAllocation.createDirectFloatBuffer(4);
        this.color = Color.WHITE.getRGB();
        this.backgroundColor = (new Color(0, 0, 0, 120)).getRGB();
        this.black = Color.BLACK.getRGB();
    }

    @EventTarget
    public void onEvent(EventLivingLabel event) {
        if (this.isValid(event.getEntity())) {
            event.setCanceled(true);
        }

    }

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
        boolean armor = this.armorBar.isEnabled();
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
                List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                mc.entityRenderer.setupCameraTransform(partialTicks, 0);
                Vector4d position = null;
                for (Vector3d vector3d : vectors) {
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

                    if (armor) {
                        if (living) {
                            entityLivingBase = (EntityLivingBase)entity;
                            armorValue = (float)entityLivingBase.getTotalArmorValue();
                            double armorWidth = (endPosX - posX) * (double)armorValue / 20.0D;
                            Gui.drawRect(posX - 0.5D, endPosY + 1.5D, posX - 0.5D + endPosX - posX + 1.0D, endPosY + 1.5D + 2.0D, backgroundColor);
                            if (armorValue > 0.0F) {
                                Gui.drawRect(posX, endPosY + 2.0D, posX + armorWidth, endPosY + 3.0D, 16777215);
                            }
                        } else if (entity instanceof EntityItem) {
                            ItemStack itemStack = ((EntityItem)entity).getEntityItem();
                            if (itemStack.isItemStackDamageable()) {
                                int maxDamage = itemStack.getMaxDamage();
                                itemDurability = (float)(maxDamage - itemStack.getItemDamage());
                                durabilityWidth = (endPosX - posX) * (double)itemDurability / (double)maxDamage;
                                Gui.drawRect(posX - 0.5D, endPosY + 1.5D, posX - 0.5D + endPosX - posX + 1.0D, endPosY + 1.5D + 2.0D, backgroundColor);
                                Gui.drawRect(posX, endPosY + 2.0D, posX + durabilityWidth, endPosY + 3.0D, 16777215);
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

    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, this.modelview, this.projection, this.viewport, this.vector) ? new Vector3d((this.vector.get(0) / (float)scaleFactor), (((float)Display.getHeight() - this.vector.get(1)) / (float)scaleFactor), this.vector.get(2)) : null;
    }

    private boolean isValid(Entity entity) {
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
}
