package lime.features.module.impl.world;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.combat.KillAura;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.ui.notifications.Notification;
import lime.utils.combat.CombatUtils;
import lime.utils.movement.MovementUtils;
import lime.utils.other.BlockUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.other.MathUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import lime.utils.other.Timer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


@ModuleData(name = "Scaffold", category = Category.WORLD)
public class Scaffold extends Module {

    private static final List<Block> blacklistedBlocks = Arrays.asList(
            Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
            Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars,
            Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore,
            Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt,
            Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily,
            Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace,
            Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence,
            Blocks.yellow_flower, Blocks.red_flower);

    private final EnumValue state = new EnumValue("State", this, "POST", "PRE", "POST");
    private final EnumValue rotations = new EnumValue("Rotations", this, "Basic", "None", "Basic", "Hypixel", "Legit", "Legit2");
    private final EnumValue itemSpoof = new EnumValue("Item Spoof", this, "Spoof", "Spoof", "KeepSpoof", "Pick");
    private final EnumValue tower = new EnumValue("Tower", this, "NCP", "None", "NCP");
    private final SlideValue speedModifier = new SlideValue("Speed Modifier", this, 0.1, 5, 1, 0.1);
    private final SlideValue expand = new SlideValue("Expand", this, 0, 5, 0, 0.05);
    private final SlideValue delay = new SlideValue("Delay", this, 0, 300, 0, 25);
    private final BoolValue keepRotations = new BoolValue("Keep Rotations", this, true);
    private final BoolValue towerMove = new BoolValue("Tower Move", this, true).onlyIf(tower.getSettingName(), "enum", "ncp");
    private final BoolValue safeWalk = new BoolValue("Safewalk", this, false);
    private final BoolValue noSprint = new BoolValue("No Sprint", this, false);
    private final BoolValue spoofSprint = new BoolValue("Spoof Sprint", this, false).onlyIf(noSprint.getSettingName(), "bool", "false");
    private final BoolValue sameY = new BoolValue("Same Y", this, false);
    private final BoolValue noSwing = new BoolValue("No Swing", this, false);
    private final BoolValue swapper = new BoolValue("Swapper", this, true);
    private final BoolValue downwards = new BoolValue("Downwards", this, false);
    private final BoolValue randomVec = new BoolValue("Random Vec", this, false);
    private final BoolValue rayCast = new BoolValue("RayCast", this, false);

    //ToDo : downwards, keepspoof

    private final MouseFilter yawMouseFilter = new MouseFilter();
    private final Timer timer = new Timer(), timerTower = new Timer();

    private ItemStack itemStack;

    private float yaw, pitch;
    private int slot, y;

    private BlockUtils.BlockData blockData;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null)
        {
            this.toggle();
            return;
        }
        y = (int) mc.thePlayer.posY;
        slot = mc.thePlayer.inventory.currentItem;
        timerTower.reset();
        blockData = null;

        yaw = -1;
        pitch = -1;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        if(itemSpoof.is("pick"))
        {
            mc.thePlayer.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        GL11.glEnable(3089);
        Gui.drawRect(e.getScaledResolution().getScaledWidth() / 2F - 45, e.getScaledResolution().getScaledHeight() / 2F + 15, e.getScaledResolution().getScaledWidth() / 2F + 45,e.getScaledResolution().getScaledHeight() / 2F + 25, 2013265920);
        RenderUtils.prepareScissorBox(e.getScaledResolution().getScaledWidth() / 2F - 45, e.getScaledResolution().getScaledHeight() / 2F + 15, e.getScaledResolution().getScaledWidth() / 2F + 45,e.getScaledResolution().getScaledHeight() / 2F + 25);
        double percentage = MathUtils.scale(getBlocksCount(), 0, 128, -45, 45);
        Gui.drawRect(e.getScaledResolution().getScaledWidth() / 2F - 45, e.getScaledResolution().getScaledHeight() / 2F + 15, e.getScaledResolution().getScaledWidth() / 2F + percentage, e.getScaledResolution().getScaledHeight() / 2F + 25, new Color(255, 0, 0).getRGB());
        GL11.glDisable(3089);
    }

    public int getBlocksCount() {
        int blocks = 0;
        for(int i = 9; i < 45; ++i) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack is = InventoryUtils.getSlot(i).getStack();
                if(is.getItem() instanceof ItemBlock) {
                    ItemBlock itemBlock = (ItemBlock) is.getItem();
                    if(!blacklistedBlocks.contains(itemBlock.getBlock())) {
                        blocks += is.stackSize;
                    }
                }
            }
        }

        return blocks;
    }

    @EventTarget
    public void on3D(Event3D e) {
        if(isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ).getBlock())) {
        }
        if(blockData != null) {
            RenderUtils.drawBox(blockData.getBlockPos().getX(), blockData.getBlockPos().getY(), blockData.getBlockPos().getZ(), 1, new Color(255, 0, 0, 100), true, true);
            RenderUtils.drawBox(blockData.getBlockPos().getX(), blockData.getBlockPos().getY(), blockData.getBlockPos().getZ(), 1, new Color(255, 0, 0), true, false);
        }
    }

    @EventTarget
    public void onMotion(EventMotion e)
    {
        if(InventoryUtils.hasBlock(blacklistedBlocks, true, true) == -1 && !safeWalk.isEnabled()) {
            Lime.getInstance().getNotificationManager().addNotification(new Notification("Scaffold", "Disabled scaffold because you have no blocks!", Notification.Type.ERROR));
            this.toggle();
            return;
        }

        if(keepRotations.isEnabled() && !rotations.is("none"))
        {
            if(yaw != -1 && pitch != -1)
            {
                e.setYaw(yaw);
                e.setPitch(pitch);
                mc.thePlayer.setRotationsTP(e);
            }
        }

        if(!isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ).getBlock())) {
            mc.thePlayer.motionX *= speedModifier.getCurrent();
            mc.thePlayer.motionZ *= speedModifier.getCurrent();
        }

        if(!noSprint.isEnabled() && spoofSprint.isEnabled())
            e.setSprint(false);

        if(noSprint.isEnabled())
            mc.thePlayer.setSprinting(false);

        if(!timer.hasReached((long)delay.getCurrent())) { return; }

        boolean downFlag = downwards.isEnabled() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ).getBlock());

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
                this.y = (int) mc.thePlayer.posY;
            }
        } else {
            this.y = (int) mc.thePlayer.posY;
        }

        tower(e);

        BlockPos underPosition = new BlockPos(x, this.y - 1 - (isAirBlock(new BlockPos(x, y - 1, z).getBlock()) ? BlockUtils.getBlockData(new BlockPos(x, this.y - 2, z)) == null ? 0 : downFlag ? 1 : 0 : 0), z);
        BlockUtils.BlockData blockData = BlockUtils.getBlockData(underPosition);

        if(blockData != null) {
            this.blockData = new BlockUtils.BlockData(new BlockPos(x, this.y-1, z), null);
        }

        if((blockData != null && isAirBlock(underPosition.getBlock())) || downwards.isEnabled())
        {
            if(blockData != null && !rotations.is("none")) {
                float[] rotations = getRotations(blockData.getBlockPos(), blockData.getEnumFacing());
                e.setYaw(yaw = rotations[0]);
                e.setPitch(pitch = rotations[1]);
                if(rayTrace(yaw, pitch, blockData) && rayCast.isEnabled()) {
                    return;
                }
                mc.thePlayer.setRotationsTP(e);
            }
            // Item Spoof and place block
            if((e.isPre() && state.is("pre")) || (!e.isPre() && state.is("post")))
            {
                int slot = mc.thePlayer.inventory.currentItem;

                int blockSlot = InventoryUtils.hasBlock(blacklistedBlocks, false, true);

                if(blockSlot == -1)
                {
                    if(!swapper.isEnabled())
                        return;
                    else {
                        blockSlot = InventoryUtils.hasBlock(blacklistedBlocks, true, false);

                        if(blockSlot != -1)
                            InventoryUtils.swap(blockSlot, 6);

                        blockSlot = 42;
                    }
                }

                switch(itemSpoof.getSelected().toLowerCase())
                {
                    case "pick":
                    case "spoof":
                        mc.thePlayer.inventory.currentItem = blockSlot - 36;
                        mc.playerController.updateController();
                        KillAura.isBlocking = false;
                        break;
                }

                if(blockData != null && mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack = mc.thePlayer.inventory.getCurrentItem(), blockData.getBlockPos(), blockData.getEnumFacing(), getVec3(blockData)))
                {
                    if(noSwing.isEnabled())
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    else
                        mc.thePlayer.swingItem();

                    timer.reset();
                }

                if(downwards.isEnabled() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && !downFlag) {
                    BlockPos underPosDown = new BlockPos(underPosition.add(0, -1, 0));

                    if(isAirBlock(underPosDown.getBlock())) {
                        BlockUtils.BlockData blockData1 = BlockUtils.getBlockData(underPosDown);

                        if(blockData1 != null) {
                            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(),
                                    blockData1.getBlockPos(), blockData1.getEnumFacing(), getVec3(blockData1));
                            if (noSwing.isEnabled())
                                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                            else
                                mc.thePlayer.swingItem();
                        }
                    }
                }

                if(itemSpoof.is("spoof"))
                {
                    mc.thePlayer.inventory.currentItem = slot;
                    mc.playerController.updateController();
                }
            }
        }
    }

    private boolean rayTrace(float yaw, float pitch, BlockUtils.BlockData blockData) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 vec31 = CombatUtils.getVectorForRotation(yaw, pitch);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * 5, vec31.yCoord * 5, vec31.zCoord * 5);

        MovingObjectPosition result = mc.theWorld.rayTraceBlocks(vec3, vec32, false);

        return result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && (blockData.getBlockPos()).equals(result.getBlockPos()) && blockData.getEnumFacing() == result.sideHit;
    }

    @EventTarget
    public void onSafewalk(EventSafeWalk e)
    {
        if(safeWalk.isEnabled())
            e.setCanceled(true);
    }

    @EventTarget
    public void onEntityAction(EventEntityAction e)
    {
        if(noSprint.isEnabled())
            e.setShouldSprint(false);

        if(downwards.isEnabled())
            e.setShouldSneak(false);
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
            final double cos = Math.cos(Math.toRadians(YAW + 90.0f));
            final double sin = Math.sin(Math.toRadians(YAW + 90.0f));
            xCalc += (forward * 0.45 * cos + strafe * 0.45 * sin) * dist;
            zCalc += (forward * 0.45 * sin - strafe * 0.45 * cos) * dist;
            if(dist == expandDist){
                break;
            }
            underPos = new BlockPos(xCalc, mc.thePlayer.posY - 1, zCalc);
            underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        }
        return new double[]{xCalc,zCalc};
    }

    private float[] getRotations(BlockPos block, EnumFacing face) {
        if(mc.theWorld == null) return null;
        double x = block.getX() + 0.5 - mc.thePlayer.posX +  (double) face.getFrontOffsetX()/2;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ +  (double) face.getFrontOffsetZ()/2;
        double y = (block.getY() + 0.5);

        //double diffXZ = MathHelper.sqrt_double(x * x + z * z);
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 360.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
        if(rotations.is("legit") || rotations.is("hypixel")) {
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
        } else if(rotations.is("basic")) {
            yaw = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(z,x)) - 90);
        } else if(rotations.is("legit2"))
        {
            yaw = (float) MathHelper.wrapDegrees(Math.toDegrees(MovementUtils.getDirection(mc.thePlayer)) - (180 - mc.theWorld.rand.nextFloat() / 100));
        }

        return new float[]{rotations.is("hypixel") ? this.yawMouseFilter.smooth(yaw, 0.4f) : yaw, 85};
    }

    private Vec3 getVec3(BlockUtils.BlockData blockData)
    {
        double x = blockData.getBlockPos().getX() + 0.5;
        double y = blockData.getBlockPos().getY() + 0.5;
        double z = blockData.getBlockPos().getZ() + 0.5;

        x += blockData.getEnumFacing().getFrontOffsetX() / 2F;
        y += blockData.getEnumFacing().getFrontOffsetY() / 2F;
        z += blockData.getEnumFacing().getFrontOffsetZ() / 2F;

        if(randomVec.isEnabled())
        {
            if (blockData.getEnumFacing() == EnumFacing.UP || blockData.getEnumFacing() == EnumFacing.DOWN) {
                x += MathUtils.random(0.3, -0.3);
                z += MathUtils.random(0.3, -0.3);
            } else {
                y += MathUtils.random(0.3, -0.3);
            }
            if (blockData.getEnumFacing() == EnumFacing.WEST || blockData.getEnumFacing() == EnumFacing.EAST) {
                z += MathUtils.random(0.3, -0.3);
            }
            if (blockData.getEnumFacing() == EnumFacing.SOUTH || blockData.getEnumFacing() == EnumFacing.NORTH) {
                x += MathUtils.random(0.3, -0.3);
            }
        }

        return new Vec3(x, y, z);
    }

    private void tower(EventMotion e)
    {
        if(e.isPre() && mc.gameSettings.keyBindJump.isKeyDown()) {
            if(this.tower.is("ncp") && (towerMove.isEnabled() || !mc.thePlayer.isMoving())) {
                if(!sameY.isEnabled() || !mc.thePlayer.isMoving()) {
                    BlockPos underPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
                    Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
                    BlockUtils.BlockData blockData = BlockUtils.getBlockData(underPos);
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
                            mc.timer.timerSpeed = 10f;
                            mc.thePlayer.motionY = 0.41982;
                            mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                            if(timerTower.hasReached(2500)) {
                                mc.timer.timerSpeed = 1f;
                                mc.thePlayer.motionY -= 0.28;
                                timerTower.reset();
                            }
                        }
                    }
                }
            }
        } else
            if(!Lime.getInstance().getModuleManager().getModuleC(lime.features.module.impl.world.Timer.class).isToggled())
                mc.timer.timerSpeed = 1;
    }

    private boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            return !(block instanceof BlockSnow) || !(block.getBlockBoundsMaxY() > 0.125);
        }
        return false;
    }
}
