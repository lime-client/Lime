package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.Event3D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.render.GLUProjection;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@ModuleData(name = "ESP", category = Category.RENDER)
public class ESP extends Module {

    private final BoolValue esp3d = new BoolValue("3D ESP", this, false);

    private final EnumValue mode = new EnumValue("Mode", this, "Box", "Box", "Cylinder").onlyIf(esp3d.getSettingName(), "bool", "true");
    private final BoolValue box = new BoolValue("Box", this, true);
    private final BoolValue items = new BoolValue("Items", this, false);
    private final BoolValue health = new BoolValue("Health", this, true);
    private final BoolValue skeleton = new BoolValue("Skeleton", this, false);

    private static final Map<EntityPlayer, float[][]> entities = new HashMap<>();
    private final SlideValue width = new SlideValue("Width", this, 0.5, 10, 1, 0.1).onlyIf(skeleton.getSettingName(), "bool", "true");

    @EventTarget
    public void on2D(Event2D e)
    {
        final ScaledResolution scaledRes = new ScaledResolution(mc);
        mc.theWorld.getLoadedEntityList().forEach(entity -> {
            if (entity instanceof EntityItem && items.isEnabled()) {
                EntityItem ent = (EntityItem) entity;
                double posX = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * e.getPartialTicks();
                double posY = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * e.getPartialTicks();
                double posZ = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * e.getPartialTicks();
                AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
                Vector3d[] corners = {new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f)};
                GLUProjection.Projection result;
                Vector4f transformed = new Vector4f(scaledRes.getScaledWidth() * 2.0f, scaledRes.getScaledHeight() * 2.0f, -1.0f, -1.0f);
                for (Vector3d vec : corners) {
                    result = GLUProjection.getInstance().project(vec.getX() - mc.getRenderManager().viewerPosX, vec.getY() - mc.getRenderManager().viewerPosY, vec.getZ() - mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, true);
                    transformed.setX((float) Math.min(transformed.getX(), result.getX()));
                    transformed.setY((float) Math.min(transformed.getY(), result.getY()));
                    transformed.setW((float) Math.max(transformed.getW(), result.getX()));
                    transformed.setZ((float) Math.max(transformed.getZ(), result.getY()));
                }
                GlStateManager.pushMatrix();
                if (RenderUtils.isInViewFrustrum(ent.getEntityBoundingBox())) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.scale(.5f, .5f, .5f);
                    float x = transformed.x * 2;
                    float x2 = transformed.w * 2;
                    float y = transformed.y * 2;
                    float y2 = transformed.z * 2;
                    if (box.isEnabled()) {
                        RenderUtils.drawHollowBox(x, y, x2, y2, 3f, Color.BLACK.getRGB());
                        RenderUtils.drawHollowBox(x + 1f, y + 1f, x2 - 1f, y2 + 1f, 1f, new Color(255, 255, 255).getRGB());
                    }
                    if (ent.getEntityItem().getMaxDamage() > 0) {
                        double offset = y2 - y;
                        double percentoffset = offset / ent.getEntityItem().getMaxDamage();
                        double finalnumber = percentoffset * (ent.getEntityItem().getMaxDamage() - ent.getEntityItem().getItemDamage());
                        Gui.drawRect(x - 4f, y, x - 1f, y2 + 3f, -0x1000000);
                        Gui.drawRect(x - 3f, y2 - finalnumber + 1f, x - 2f, y2 + 2f, new Color(0x3E83E3).getRGB());
                    }
                    final String nametext = StringUtils.stripControlCodes(ent.getEntityItem().getItem().getItemStackDisplayName(ent.getEntityItem())) + (ent.getEntityItem().getMaxDamage() > 0 ? "§9 : " + (ent.getEntityItem().getMaxDamage() - ent.getEntityItem().getItemDamage()) : "");
                    mc.fontRendererObj.drawStringWithShadow(nametext, (x + ((x2 - x) / 2)) - (mc.fontRendererObj.getStringWidth(nametext) / 2F), y - mc.fontRendererObj.FONT_HEIGHT - 2, -1);
                    GlStateManager.popMatrix();
                }
                GlStateManager.popMatrix();
            }
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase ent = (EntityLivingBase) entity;
                if (ent instanceof EntityPlayer && ent != mc.thePlayer) {
                    double posX = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * e.getPartialTicks();
                    double posY = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * e.getPartialTicks();
                    double posZ = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * e.getPartialTicks();
                    AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
                    Vector3d[] corners = {new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f)};
                    GLUProjection.Projection result;
                    Vector4f transformed = new Vector4f(scaledRes.getScaledWidth() * 2.0f, scaledRes.getScaledHeight() * 2.0f, -1.0f, -1.0f);
                    for (Vector3d vec : corners) {
                        result = GLUProjection.getInstance().project(vec.getX() - mc.getRenderManager().viewerPosX, vec.getY() - mc.getRenderManager().viewerPosY, vec.getZ() - mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, true);
                        transformed.setX((float) Math.min(transformed.getX(), result.getX()));
                        transformed.setY((float) Math.min(transformed.getY(), result.getY()));
                        transformed.setW((float) Math.max(transformed.getW(), result.getX()));
                        transformed.setZ((float) Math.max(transformed.getZ(), result.getY()));
                    }
                    GlStateManager.pushMatrix();
                    if (RenderUtils.isInViewFrustrum(ent.getEntityBoundingBox())) {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.scale(.5f, .5f, .5f);
                        float x = transformed.x * 2;
                        float x2 = transformed.w * 2;
                        float y = transformed.y * 2;
                        float y2 = transformed.z * 2;
                        if (box.isEnabled()) {
                            RenderUtils.drawHollowBox(x, y, x2, y2, 3f, Color.BLACK.getRGB());
                            RenderUtils.drawHollowBox(x + 1f, y + 1f, x2 - 1f, y2 + 1f, 1f, new Color(0xFFF9F8).getRGB());
                        }
                        if (health.isEnabled()) {
                            float healthHeight = (y2 - y) * (((EntityLivingBase) entity).getHealth() / ((EntityLivingBase) entity).getMaxHealth());
                            if(((EntityLivingBase) entity).getHealth() > 20) healthHeight = (y2 - y) * (20 / ((EntityLivingBase) entity).getMaxHealth());
                            Gui.drawRect(x - 4f, y, x - 1f, y2 + 3f, -0x1000000);
                            Gui.drawRect(x - 3f, y2 - healthHeight + 1f, x - 2f, y2 + 2f, getHealthColor(((EntityLivingBase) entity)));
                        }
                        GlStateManager.popMatrix();
                    }
                    GlStateManager.popMatrix();
                }
            }
        });
    }

    private int getHealthColor(EntityLivingBase player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
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

        this.setSuffix(mode.getSelected());
        if(!esp3d.isEnabled())
        {
            this.setSuffix("");
            return;
        }
        for(Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(entity == mc.thePlayer) continue;
            if(entity instanceof EntityPlayer) {
                GL11.glPushMatrix();

                GlStateManager.enableBlend();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GlStateManager.disableTexture2D();
                GlStateManager.disableDepth();

                RenderUtils.glColor(HUD.getColor(0));
                GL11.glLineWidth(2.5f);

                double factor = entity.width - 0.15;
                double yOffset = entity.height + 0.2;

                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

                if(mode.is("box")) {
                    drawBox(x, y, z, factor, yOffset);
                }
                if(mode.is("cylinder")) {
                    drawCylinder(x, y, z, factor, yOffset);
                }

                GL11.glLineWidth(1);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();

                GL11.glPopMatrix();
            }
        }
    }

    private void drawSkeleton(Event3D event, EntityPlayer e) {
        final Color color = new Color(e.getName().equalsIgnoreCase(mc.thePlayer.getName()) ? 0xFF99ff99 : new Color(255, 255, 255).getRGB());
        if (!e.isInvisible()) {
            float[][] entPos = entities.get(e);
            if (entPos != null && e.getEntityId() != -1488 && e.isEntityAlive() && RenderUtils.isInViewFrustrum(e.getEntityBoundingBox()) && !e.isDead && e != mc.thePlayer && !e.isPlayerSleeping()) {
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
