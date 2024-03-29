package lime.features.module.impl.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventLivingLabel;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.other.MathUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vector3d;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4d;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nametags extends Module {

    public Nametags() {
        super("Nametags", Category.VISUALS);
    }

    @EventTarget
    public void on2D(Event2D e) {
        drawNameTags(e);
    }

    @EventTarget
    public void onRenderNameTags(EventLivingLabel e) {
        if(e.getEntity() instanceof EntityPlayer && isValid((EntityPlayer) e.getEntity())) {
            e.setCanceled(true);
        }
    }

    public void drawNameTags(Event2D e) {
        mc.theWorld.loadedEntityList.forEach(entity -> {
            if (entity instanceof EntityPlayer) {
                EntityPlayer ent = (EntityPlayer) entity;
                if (isValid(ent) && RenderUtils.isInViewFrustrum(ent.getEntityBoundingBox())) {
                    double posX = RenderUtils.interpolate(ent.posX, ent.lastTickPosX, e.getPartialTicks());
                    double posY = RenderUtils.interpolate(ent.posY, ent.lastTickPosY, e.getPartialTicks());
                    double posZ = RenderUtils.interpolate(ent.posZ, ent.lastTickPosZ, e.getPartialTicks());
                    double width = ent.width / 1.5;
                    double height = ent.height + (ent.isSneaking() ? -0.3 : 0.2);
                    AxisAlignedBB aabb = new AxisAlignedBB(posX - width, posY, posZ - width, posX + width, posY + height, posZ + width);
                    List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                    mc.entityRenderer.setupCameraTransform(e.getPartialTicks(), 0);
                    Vector4d position = null;
                    for (Vector3d vector : vectors) {
                        vector = RenderUtils.project(vector.field_181059_a - mc.getRenderManager().viewerPosX, vector.field_181060_b - mc.getRenderManager().viewerPosY, vector.field_181061_c - mc.getRenderManager().viewerPosZ);
                        if (vector != null && vector.field_181061_c >= 0.0 && vector.field_181061_c < 1.0) {
                            if (position == null) {
                                position = new Vector4d(vector.field_181059_a, vector.field_181060_b, vector.field_181061_c, 0.0);
                            }
                            position.x = Math.min(vector.field_181059_a, position.x);
                            position.y = Math.min(vector.field_181060_b, position.y);
                            position.z = Math.max(vector.field_181059_a, position.z);
                            position.w = Math.max(vector.field_181060_b, position.w);
                        }
                    }
                    mc.entityRenderer.setupOverlayRendering();
                    if (position != null) {
                        GL11.glPushMatrix();
                        float size = .5f;
                        drawArmor(ent, (int) (position.x + ((position.z - position.x) / 2)), (int) position.y - 4 - mc.fontRendererObj.FONT_HEIGHT * 2, size);
                        GlStateManager.scale(size, size, size);
                        float x = (float) position.x / size;
                        float x2 = (float) position.z / size;
                        float y = (float) position.y / size;
                        final String nametext = entity.getDisplayName().getFormattedText() + " §7(§fH: " + MathUtils.roundToPlace(((EntityPlayer) entity).getHealth(), 2) + " §c❤§7)";
                        Gui.drawRect((x + (x2 - x) / 2) - (mc.fontRendererObj.getStringWidth(nametext) >> 1) - 2, y - mc.fontRendererObj.FONT_HEIGHT - 4, (x + (x2 - x) / 2) + (mc.fontRendererObj.getStringWidth(nametext) >> 1) + 2, y - 2, new Color(0, 0, 0, 120).getRGB());

                        mc.fontRendererObj.drawStringWithShadow(nametext, (x + ((x2 - x) / 2)) - (mc.fontRendererObj.getStringWidth(nametext) / 2F), y - mc.fontRendererObj.FONT_HEIGHT - 2, getNameColor(ent));
                        GL11.glPopMatrix();
                    }
                }
            }
        });
    }

    private int getNameColor(EntityLivingBase ent) {
        if (Lime.getInstance().getFriendManager().isFriend(ent) || mc.thePlayer.isOnSameTeam(ent)) return new Color(122, 190, 255).getRGB();
        else if (ent.getName().equals(mc.thePlayer.getName())) return new Color(0xFF99ff99).getRGB();
        return new Color(-1).getRGB();
    }

    private ChatFormatting getNameHealthColor(EntityLivingBase player) {
        final double health = Math.ceil(player.getHealth());
        final double maxHealth = player.getMaxHealth();
        final double percentage = 100 * (health / maxHealth);
        if (percentage > 85) return ChatFormatting.DARK_GREEN;
        else if (percentage > 75) return ChatFormatting.GREEN;
        else if (percentage > 50) return ChatFormatting.YELLOW;
        else if (percentage > 25) return ChatFormatting.RED;
        else if (percentage > 0) return ChatFormatting.DARK_RED;
        return ChatFormatting.BLACK;
    }

    private void drawArmor(EntityPlayer player, int x, int y, float size) {
        if (player.inventory.armorInventory.length > 0) {
            List<ItemStack> items = new ArrayList<>();
            if (player.getHeldItem() != null) {
                items.add(player.getHeldItem());
            }
            for (int index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory[index];
                if (stack != null) {
                    items.add(stack);
                }
            }
            int armorX = x - ((items.size() * 18) / 2);
            for (ItemStack stack : items) {
                GlStateManager.pushMatrix();
                GlStateManager.enableLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, armorX, y);
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, armorX, y);
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
                GlStateManager.disableDepth();
                NBTTagList enchants = stack.getEnchantmentTagList();
                GlStateManager.pushMatrix();
                GlStateManager.scale(size, size, size);
                if (stack.getItem() == Items.golden_apple && stack.getMetadata() == 1) {
                    mc.fontRendererObj.drawString("op", armorX / size, y / size, 0xFFFF0000, true);
                }
                Enchantment[] important = new Enchantment[]{Enchantment.protection, Enchantment.sharpness, Enchantment.fireAspect, Enchantment.efficiency, Enchantment.power, Enchantment.flame};
                if (enchants != null) {
                    int ency = y + 8;
                    for (int index = 0; index < enchants.tagCount(); ++index) {
                        short id = enchants.getCompoundTagAt(index).getShort("id");
                        short level = enchants.getCompoundTagAt(index).getShort("lvl");
                        Enchantment enc = Enchantment.getEnchantmentById(id);
                        for (Enchantment importantEnchantment : important) {
                            if (enc == importantEnchantment) {
                                String encName = enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                                if (level > 99) encName = encName + "99+";
                                else encName = encName + level;
                                mc.fontRendererObj.drawString(encName, armorX / size + 4, ency / size, 0xDDD1E6, true);
                                ency -= 5;
                                break;
                            }
                        }
                    }
                }
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
                armorX += 18;
            }
        }
    }

    public boolean isValid(EntityLivingBase entity) {
        boolean players = true;
        boolean creatures = false;
        boolean villagers = false;
        boolean invisibles = false;

        return entity != null && (!(entity instanceof EntityArmorStand) && entity != mc.thePlayer && (!entity.isInvisible() || invisibles) && !entity.isDead &&
                (entity.getHealth() != 0 && (!(entity instanceof EntityAnimal || entity instanceof EntityMob || entity instanceof EntityIronGolem ||
                        entity instanceof EntitySquid || entity instanceof EntityBat) || creatures) && (!(entity instanceof EntityVillager) || villagers) &&
                        (!(entity instanceof EntityOtherPlayerMP) || (players
                                && !entity.getDisplayName().getUnformattedText().contains("NPC ") && !entity.getName().equalsIgnoreCase("[NPC]") && !entity.getDisplayName().getUnformattedText().contains("CIT-") && !entity.getName().equals("")))));
    }
}
