package lime.features.module.impl.world;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventEntityAction;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventSafeWalk;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.combat.CombatUtils;
import lime.utils.movement.MovementUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.other.MathUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.block.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@ModuleData(name = "Scaffold", category = Category.WORLD)
public class Scaffold extends Module {

    private class BlockData {
        private final BlockPos blockPos;
        private final EnumFacing enumFacing;
        
        public BlockData(BlockPos blockPos, EnumFacing enumFacing) {
            this.blockPos = blockPos;
            this.enumFacing = enumFacing;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public EnumFacing getEnumFacing() {
            return enumFacing;
        }
    }

    private enum Rotations {
        NONE, BASIC, BASIC_2, HYPIXEL, LEGIT
    }

    private enum State {
        PRE, POST
    }

    private enum ItemSpoof {
        PICK, SPOOF, KEEPSPOOF
    }

    private final EnumValue state = new EnumValue("State", this, State.POST);
    private final EnumValue itemSpoof = new EnumValue("Item Spoof", this, ItemSpoof.SPOOF);
    private final EnumValue rotations = new EnumValue("Rotations", this, Rotations.BASIC_2);
    private final SlideValue expand = new SlideValue("Expand", this, 0, 5, 0.3, 0.05);
    private final SlideValue eagle = new SlideValue("Eagle", this, 0, 5, 1, 1);
    private final BoolValue keepRotations = new BoolValue("Keep Rotations", this, true);
    private final BoolValue safeWalk = new BoolValue("SafeWalk", this, false);
    private final BoolValue tower = new BoolValue("Tower", this, true);
    private final BoolValue towerMove = new BoolValue("Tower Move", this, false);
    private final BoolValue noSprint = new BoolValue("No Sprint", this, false);
    private final BoolValue noSwing = new BoolValue("No Swing", this, false);
    private final BoolValue sameY = new BoolValue("Same Y", this, false);
    private final BoolValue swapper = new BoolValue("Swapper", this, true);
    private final BoolValue down = new BoolValue("Down", this, true);

    private static final List<Block> blacklistedBlocks = Arrays.asList(
            Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
            Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars,
            Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore,
            Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt,
            Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily,
            Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace,
            Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence);
    private ItemStack currentItemStack;
    private final Animate animation = new Animate();
    private BlockData blockData;
    private double posY;
    private int blocksWithoutEagle;
    private int slot;

    private final lime.utils.other.Timer timer = new lime.utils.other.Timer();

    // Keep rotations
    private float yaw;
    private float pitch;

    private final MouseFilter yawMouseFilter =  new MouseFilter();

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        }
        this.posY = mc.thePlayer.posY;
        this.animation.reset();
        this.animation.setEase(Easing.CUBIC_IN_OUT);
        this.animation.setMin(5);
        //this.animation.setMax(new ScaledResolution(mc).getScaledWidth() / 2 - (mc.fontRendererObj.getStringWidth(getBlocksCount() + " blocks") / 2));
        this.animation.setMax(new ScaledResolution(mc).getScaledHeight() / 2F);
        this.animation.setSpeed(250);
        this.animation.setReversed(false);
        if(noSprint.isEnabled())
            mc.thePlayer.setSprinting(false);

        this.slot = mc.thePlayer.inventory.currentItem;
        blocksWithoutEagle = 0;
    }

    @Override
    public void onDisable() {
        this.yaw = 999;
        this.pitch = 999;
        this.currentItemStack = null;
        this.blockData = null;

        if(itemSpoof.is("pick") || itemSpoof.is("keepspoof")) {
            mc.thePlayer.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        this.animation.update();
        if(currentItemStack != null) {
            this.animation.setMax(e.getScaledResolution().getScaledHeight() / 2F + 25);
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(currentItemStack, new ScaledResolution(mc).getScaledWidth() / 2 - 8, (int) this.animation.getValue() - 24);
        }
        int blocks = getBlocksCount();
        mc.fontRendererObj.drawStringWithShadow(blocks + " blocks", e.getScaledResolution().getScaledWidth() / 2F - (mc.fontRendererObj.getStringWidth(getBlocksCount() + " blocks") / 2F), this.animation.getValue(), blocks > 64 ? new Color(0, 255, 0).getRGB() : new Color(255, 0, 0).getRGB());
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(InventoryUtils.hasBlock(blacklistedBlocks, true, true) == -1)
            return;

        if(keepRotations.isEnabled() && !(rotations.is("none") || yaw == 999)) {
            e.setYaw(yaw);
            e.setPitch(pitch);
        }
        mc.thePlayer.setRotationsTP(e);
        if(eagle.getCurrent() != 0 && e.isPre()) {
            if((int) eagle.getCurrent() <= blocksWithoutEagle) {
                blocksWithoutEagle = 0;
                mc.gameSettings.keyBindSneak.pressed = true;
            } else {
                mc.gameSettings.keyBindSneak.pressed = false;
            }
        }

        boolean downFlag = down.isEnabled() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ).getBlock());

        double x = mc.thePlayer.posX;
        double z = mc.thePlayer.posZ;
        if(!mc.thePlayer.isCollidedHorizontally && (int) expand.getCurrent() != 0) {
            double[] coords = getExpandCoords(x, z, mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw, downFlag ? 1 : expand.getCurrent());
            x = coords[0];
            z = coords[1];
        }

        boolean isAirUnderPos = isAirBlock(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock());

        if(isAirUnderPos) {
            x = mc.thePlayer.posX;
            z = mc.thePlayer.posZ;
        }

        if(sameY.isEnabled() && !downFlag) {
            if(mc.thePlayer.fallDistance > 1.2 || (!mc.thePlayer.isMoving() && mc.gameSettings.keyBindJump.pressed)) {
                this.posY = mc.thePlayer.posY;
            }
        } else {
            this.posY = mc.thePlayer.posY;
        }

        BlockPos underPos = new BlockPos(x, this.posY - 1 - (isAirUnderPos ? getBlockData(new BlockPos(x, this.posY - 2, z)) == null ? 0 : downFlag ? 1 : 0 : 0), z);
        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();

        BlockData blockData = getBlockData(underPos);

        if(getBlocksCount() > 0 && e.isPre() && mc.gameSettings.keyBindJump.isKeyDown()) {
            if(tower.isEnabled() && (towerMove.isEnabled() || !mc.thePlayer.isMoving())) {
                if(!sameY.isEnabled() || !mc.thePlayer.isMoving()) {
                    underPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
                    underBlock = mc.theWorld.getBlockState(underPos).getBlock();
                    blockData = getBlockData(underPos);
                    if(mc.thePlayer.isMoving()) {
                        if (MovementUtils.isOnGround(0.76) && !MovementUtils.isOnGround(0.75) && mc.thePlayer.motionY > 0.23 && mc.thePlayer.motionY < 0.25) {
                            mc.thePlayer.motionY = (Math.round(mc.thePlayer.posY) - mc.thePlayer.posY);
                        }
                        if (MovementUtils.isOnGround(0.0001)) {
                            mc.thePlayer.motionY = 0.42;
                            mc.thePlayer.motionX *= 0.9;
                            mc.thePlayer.motionZ *= 0.9;
                        } else if(mc.thePlayer.posY >= Math.round(mc.thePlayer.posY) - 0.0001 && mc.thePlayer.posY <= Math.round(mc.thePlayer.posY) + 0.0001){
                            mc.thePlayer.motionY = 0;
                        }
                    } else {
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                        mc.thePlayer.jumpMovementFactor = 0;
                        if (isAirBlock(underBlock) && blockData != null) {
                            mc.timer.timerSpeed = 5f;
                            mc.thePlayer.motionY = 0.41982;
                            mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                            if(timer.hasReached(2500)) {
                                mc.timer.timerSpeed = 1;
                                mc.thePlayer.motionY -= 0.28;
                                timer.reset();
                            }
                        }
                    }
                }
            }
        } else
            mc.timer.timerSpeed = 1;

        if((isAirBlock(underBlock) && blockData != null) || down.isEnabled()) {
            if(e.isPre()) {
                if(blockData != null && (isAirBlock(underBlock)) && !rotations.is("none")) {
                    this.blockData = blockData;
                    float[] rots = getRotations(blockData.getBlockPos(), blockData.getEnumFacing());
                    e.setYaw(rots[0]);
                    e.setPitch(rots[1]);
                    this.yaw = rots[0];
                    this.pitch = rots[1];
                    mc.thePlayer.setRotationsTP(e);

                }
            }

            if(e.getState().name().equalsIgnoreCase(state.getSelected().name())) {
                int slot = mc.thePlayer.inventory.currentItem;

                int blockSlot = InventoryUtils.hasBlock(blacklistedBlocks, false, true);

                if(blockSlot == -1) {
                    if(!swapper.isEnabled())
                        return;
                    int blockInvSlot = InventoryUtils.hasBlock(blacklistedBlocks, true, false);

                    if(blockInvSlot != -1) {
                        InventoryUtils.swap(blockInvSlot, 6);
                    } else
                        return;
                }

                this.currentItemStack = InventoryUtils.getSlot(blockSlot).getStack();

                blockSlot = blockSlot - 36;

                if(slot != blockSlot) {
                    if(itemSpoof.is("spoof") || itemSpoof.is("keepspoof")) {
                        mc.thePlayer.inventory.currentItem = blockSlot;
                        mc.playerController.updateController();
                    } else if(itemSpoof.is("pick")) {
                        mc.thePlayer.inventory.currentItem = blockSlot;
                        mc.playerController.updateController();
                    }
                }

                if(blockData != null && isAirBlock(underBlock) && mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), blockData.blockPos, blockData.enumFacing, getVec3(blockData.getBlockPos(), blockData.getEnumFacing()))) {
                    if (noSwing.isEnabled())
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    else
                        mc.thePlayer.swingItem();
                    blocksWithoutEagle++;
                }

                // Downwards
                if(down.isEnabled() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && !downFlag) {
                    BlockPos underPosDown = new BlockPos(underPos.add(0, -1, 0));

                    if(isAirBlock(underPosDown.getBlock())) {
                        BlockData blockData1 = getBlockData(underPosDown);

                        if(blockData1 != null) {
                            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(),
                                    blockData1.getBlockPos(), blockData1.enumFacing, getVec3(blockData1.getBlockPos(), blockData1.enumFacing));
                            if (noSwing.isEnabled())
                                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                            else
                                mc.thePlayer.swingItem();
                        }
                    }
                }

                if(slot != blockSlot) {
                    if(itemSpoof.is("spoof") || itemSpoof.is("keepSpoof")) {
                        mc.thePlayer.inventory.currentItem = slot;
                        if(itemSpoof.is("spoof"))
                            mc.playerController.updateController();
                    }
                }
            }
        }
    }

    @EventTarget
    public void onSafeWalk(EventSafeWalk e) {
        e.setCanceled(safeWalk.isEnabled());
    }

    @EventTarget
    public void onEntityAction(EventEntityAction e) {
        if(down.isEnabled()) {
            e.setShouldSneak(false);
        }
        if(noSprint.isEnabled()) {
            e.setShouldSprint(false);
        }
    }

    private int getBlocksCount() {
        int size = 0;
        for(int i = 9; i < 45; ++i) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemBlock) {
                    ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                    if(!blacklistedBlocks.contains(itemBlock) && !itemStack.getUnlocalizedName().equalsIgnoreCase("tile.cactus")) {
                        size += itemStack.stackSize;
                    }
                }
            }
        }

        return size;
    }

    private boolean hotBarContainsBlock() {
        for(int i = 36; i < 45; ++i) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemBlock) {
                    ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                    if(!blacklistedBlocks.contains(itemBlock.getBlock()) && !itemStack.getUnlocalizedName().equalsIgnoreCase("tile.cactus")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private float[] getRotations(BlockPos block, EnumFacing face) {
        if(mc.theWorld == null) return null;
        double x = block.getX() + 0.5 - mc.thePlayer.posX +  (double) face.getFrontOffsetX()/2;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ +  (double) face.getFrontOffsetZ()/2;
        double y = (block.getY() + 0.5);

        double diffXZ = MathHelper.sqrt_double(x * x + z * z);
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 360.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
        if(rotations.is("legit")) {
            switch(face){
                case NORTH:
                    yaw = 0;
                    break;
                case WEST:
                    yaw = -90;
                    break;
                case EAST:
                    yaw = 90;
                    break;
                case SOUTH:
                    yaw = 180;
                    break;
            }
        } else if(rotations.is("basic_2")) {
            yaw = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(z,x)) - 90);
        } else if(rotations.is("hypixel")) {
            Vec3 vec = getVec3(block, face);
            float[] rots = CombatUtils.getRotations(vec.xCoord, vec.yCoord, vec.zCoord);
            yaw = rots[0];
            pitch = rots[1];
        }
        if (yaw < 0.0F) {
            yaw += 360f;
        }
        //yaw += 90;

        return new float[]{rotations.is("legit") ? this.yawMouseFilter.smooth(yaw, 0.4f) : yaw, pitch};
    }

    private double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW, double expand){
        BlockPos underPos = new BlockPos(x, mc.thePlayer.posY - 1, z);
        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        double xCalc = -999, zCalc = -999;
        double dist = 0;
        double expandDist = expand * 2;
        while(!isAirBlock(underBlock)){
            xCalc = x;
            zCalc = z;
            dist ++;
            if(dist > expandDist){
                dist = expandDist;
            }
            xCalc += (forward * 0.45 * Math.cos(Math.toRadians(YAW + 90.0f)) + strafe * 0.45 * Math.sin(Math.toRadians(YAW + 90.0f))) * dist;
            zCalc += (forward * 0.45 * Math.sin(Math.toRadians(YAW + 90.0f)) - strafe * 0.45 * Math.cos(Math.toRadians(YAW + 90.0f))) * dist;
            if(dist == expandDist){
                break;
            }
            underPos = new BlockPos(xCalc, mc.thePlayer.posY - 1, zCalc);
            underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        }
        return new double[]{xCalc,zCalc};
    }

    private boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            return !(block instanceof BlockSnow) || !(block.getBlockBoundsMaxY() > 0.125);
        }

        return false;
    }

    private Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        x += (double) face.getFrontOffsetX() / 2;
        z += (double) face.getFrontOffsetZ() / 2;
        y += (double) face.getFrontOffsetY() / 2;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtils.random(0.3, -0.3);
            z += MathUtils.random(0.3, -0.3);
        } else {
            y += MathUtils.random(0.3, -0.3);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtils.random(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtils.random(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    private boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isBlockSolid(mc.theWorld, pos, EnumFacing.DOWN) || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }

    private BlockData getBlockData(BlockPos pos) {
        if(isPosSolid(pos.add(0, 1, 0))) {
            return new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (isPosSolid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (isPosSolid(pos6.add(0, -1, 0))) {
            return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos6.add(-1, 0, 0))) {
            return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos6.add(1, 0, 0))) {
            return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos6.add(0, 0, 1))) {
            return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos6.add(0, 0, -1))) {
            return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (isPosSolid(pos7.add(0, -1, 0))) {
            return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos7.add(-1, 0, 0))) {
            return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos7.add(1, 0, 0))) {
            return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos7.add(0, 0, 1))) {
            return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos7.add(0, 0, -1))) {
            return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (isPosSolid(pos8.add(0, -1, 0))) {
            return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos8.add(-1, 0, 0))) {
            return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos8.add(1, 0, 0))) {
            return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos8.add(0, 0, 1))) {
            return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos8.add(0, 0, -1))) {
            return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (isPosSolid(pos9.add(0, -1, 0))) {
            return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos9.add(-1, 0, 0))) {
            return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos9.add(1, 0, 0))) {
            return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos9.add(0, 0, 1))) {
            return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos9.add(0, 0, -1))) {
            return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

}
