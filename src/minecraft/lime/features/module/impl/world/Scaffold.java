package lime.features.module.impl.world;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.exploit.Disabler;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.management.FontManager;
import lime.ui.notifications.Notification;
import lime.utils.combat.CombatUtils;
import lime.utils.movement.MovementUtils;
import lime.utils.other.BlockUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.other.MathUtils;
import lime.utils.other.Timer;
import lime.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class Scaffold extends Module {

    public Scaffold() {
        super("Scaffold", Category.WORLD);
    }

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
            Blocks.yellow_flower, Blocks.red_flower, Blocks.flower_pot, Blocks.dragon_egg, Blocks.monster_egg, Blocks.standing_banner, Blocks.wall_banner);

    private final EnumProperty state = new EnumProperty("State", this, "POST", "PRE", "POST");
    private final EnumProperty rotations = new EnumProperty("Rotations", this, "Basic", "None", "Basic", "Hypixel", "Cubecraft", "Legit", "Legit2");
    private final EnumProperty itemSpoof = new EnumProperty("Item Spoof", this, "Spoof", "Spoof", "KeepSpoof", "Pick");
    private final EnumProperty tower = new EnumProperty("Tower", this, "NCP", "None", "NCP");
    public final EnumProperty search = new EnumProperty("Search", this, "Basic", "Basic", "Advanced");
    private final NumberProperty speedModifier = new NumberProperty("Speed Modifier", this, 0.1, 5, 1, 0.1);
    private final NumberProperty expand = new NumberProperty("Expand", this, 0, 5, 0, 0.05);
    private final NumberProperty delay = new NumberProperty("Delay", this, 0, 300, 0, 25);
    private final BooleanProperty keepRotations = new BooleanProperty("Keep Rotations", this, true);
    private final BooleanProperty towerMove = new BooleanProperty("Tower Move", this, true).onlyIf(tower.getSettingName(), "enum", "ncp");
    private final BooleanProperty safeWalk = new BooleanProperty("Safewalk", this, false);
    private final BooleanProperty noSprint = new BooleanProperty("No Sprint", this, false);
    private final BooleanProperty spoofSprint = new BooleanProperty("Spoof Sprint", this, false).onlyIf(noSprint.getSettingName(), "bool", "false");
    private final BooleanProperty sameY = new BooleanProperty("Same Y", this, false);
    private final BooleanProperty noSwing = new BooleanProperty("No Swing", this, false);
    private final BooleanProperty swapper = new BooleanProperty("Swapper", this, true);
    private final BooleanProperty downwards = new BooleanProperty("Downwards", this, false);
    private final BooleanProperty randomVec = new BooleanProperty("Random Vec", this, false);
    private final BooleanProperty rayCast = new BooleanProperty("RayCast", this, false);
    private final BooleanProperty slowSpeed = new BooleanProperty("Slow Speed", this, false);
    private final BooleanProperty blockInfo = new BooleanProperty("Block Info", this, false);
    private final BooleanProperty blockEsp = new BooleanProperty("Block ESP", this, false);

    //ToDo : KeepSpoof

    private final MouseFilter yawMouseFilter = new MouseFilter();
    private final Timer timer = new Timer(), timerTower = new Timer();

    private float yaw, pitch;
    private int slot, y;

    private BlockUtils.BlockData blockData;
    private ItemStack itemStack;

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
        if(blockInfo.isEnabled() && itemStack != null) {
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (e.getScaledResolution().getScaledWidth() / 2) + 8, e.getScaledResolution().getScaledHeight() / 2 - 8);
            FontManager.ProductSans20.getFont().drawStringWithShadow(getBlocksCount()+"", (e.getScaledResolution().getScaledWidth() / 2F) + 24, e.getScaledResolution().getScaledHeight() / 2F - 8, -1);
            RenderHelper.disableStandardItemLighting();
        }
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
        if(blockData != null && blockEsp.isEnabled()) {
            RenderUtils.drawBox(blockData.getBlockPos().getX(), blockData.getBlockPos().getY(), blockData.getBlockPos().getZ(), 1, new Color(255, 0, 0, 100), true, true);
            RenderUtils.drawBox(blockData.getBlockPos().getX(), blockData.getBlockPos().getY(), blockData.getBlockPos().getZ(), 1, new Color(255, 0, 0), true, false);
        }
    }

    @EventTarget
    public void onMotion(EventMotion e)
    {
        if(InventoryUtils.hasBlock(blacklistedBlocks, true, true) == -1 && !safeWalk.isEnabled()) {
            Lime.getInstance().getNotificationManager().addNotification("Disabled scaffold because you have no blocks!", Notification.Type.FAIL);
            this.toggle();
            return;
        }

        if(slowSpeed.isEnabled() && e.isPre() && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            mc.thePlayer.motionX *= .81;
            mc.thePlayer.motionZ *= .81;
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

        if(!isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ).getBlock()) && e.isPre()) {
            mc.thePlayer.motionX *= speedModifier.getCurrent();
            mc.thePlayer.motionZ *= speedModifier.getCurrent();
        }

        if(!noSprint.isEnabled() && spoofSprint.isEnabled())
            e.setSprint(false);

        if(noSprint.isEnabled())
            mc.thePlayer.setSprinting(false);

        if(!timer.hasReached((long)delay.getCurrent())) { return; }

        boolean downFlag = downwards.isEnabled() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ).getBlock());

        double x = Double.MAX_VALUE;
        double z = Double.MAX_VALUE;
        if(!mc.thePlayer.isCollidedHorizontally && (int) expand.getCurrent() != 0) {
            double[] coords = getExpandCoords(mc.thePlayer.posX, mc.thePlayer.posZ, mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw, downFlag ? 1 : expand.getCurrent());
            x = coords[0];
            z = coords[1];
        }

        boolean isAirUnderPos = isAirBlock(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)).getBlock());

        if(isAirUnderPos || (downwards.isEnabled() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()))) {
            x = mc.thePlayer.posX;
            z = mc.thePlayer.posZ;
        }

        if(x == Double.MAX_VALUE && z == Double.MAX_VALUE) {
            return;
        }

        if(sameY.isEnabled() && !downFlag) {
            if(mc.thePlayer.fallDistance > 1.2 || (!mc.thePlayer.isMoving() && mc.gameSettings.keyBindJump.pressed)) {
                this.y = (int) mc.thePlayer.posY;
            }
        } else {
            this.y = (int) mc.thePlayer.posY;
        }

        if(MovementUtils.isOnGround(0.42)) {
            tower(e);
        }

        BlockPos underPosition = new BlockPos(x, this.y - 1 - (isAirBlock(new BlockPos(x, y - 0.5, z).getBlock()) ? BlockUtils.getBlockData(new BlockPos(x, this.y - 2, z)) == null ? 0 : downFlag ? 1 : 0 : 0), z);
        BlockUtils.BlockData blockData = BlockUtils.getBlockData(underPosition);

        if(blockData != null) {
            this.blockData = new BlockUtils.BlockData(new BlockPos(x, this.y-0.5, z), null);
        }

        if((blockData != null && isAirBlock(underPosition.getBlock())) || downwards.isEnabled())
        {
            if(blockData != null && !rotations.is("none")) {
                float[] rotations = getRotations(blockData.getBlockPos(), blockData.getEnumFacing());
                assert rotations != null;
                if(Lime.getInstance().getModuleManager().getModuleC(Disabler.class).isToggled() && ((Disabler) Lime.getInstance().getModuleManager().getModuleC(Disabler.class)).mode.is("watchdog") && mc.thePlayer.ticksExisted % 2 != 0) {
                    mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1], e.isGround()));
                }
                e.setYaw(yaw = rotations[0]);
                e.setPitch(pitch = this.rotations.is("cubecraft") ? 81 + (RandomUtils.nextFloat(0, 1) - 0.5f) : rotations[1]);

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

                        if(blockSlot != -1) {
                            InventoryUtils.swap(blockSlot, 6);
                        }

                        blockSlot = 42;
                    }
                }
                itemStack = InventoryUtils.getSlot(blockSlot).getStack();

                switch(itemSpoof.getSelected().toLowerCase())
                {
                    case "pick":
                    case "spoof":
                        mc.thePlayer.inventory.currentItem = blockSlot - 36;
                        mc.playerController.updateController();
                        KillAura.isBlocking = false;
                        break;
                }
                if(blockData != null && mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), blockData.getBlockPos(), blockData.getEnumFacing(), getVec3(blockData)))
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
        float yaw = (float) (Math.atan2(z, x) * 360.0D / Math.PI) - 90.0F;

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
        } else if(rotations.is("basic")) {
            yaw = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(z,x)) - 90);
        } else if(rotations.is("legit2"))
        {
            yaw = (float) MathHelper.wrapDegrees(Math.toDegrees(MovementUtils.getDirection(mc.thePlayer)) - (180 - mc.theWorld.rand.nextFloat() / 100));
        } else if(rotations.is("hypixel") || rotations.is("cubecraft"))  {
            Vec3 positionEyes = this.mc.thePlayer.getPositionEyes(2.0F);
            Vec3 add = (new Vec3((double)block.getX() + 0.5D, (double)block.getY() + 0.5D, (double)block.getZ() + 0.5D)).add(new Vec3(face.getDirectionVec()).scale(0.49000000953674316D));
            double n = add.xCoord - positionEyes.xCoord;
            double n2 = add.yCoord - positionEyes.yCoord;
            double n3 = add.zCoord - positionEyes.zCoord;
            return new float[]{(float)(Math.atan2(n3, n) * 180.0D / 3.141592653589793D - 90.0D), -((float)(Math.atan2(n2, (double)((float)Math.hypot(n, n3))) * 180.0D / 3.141592653589793D))};
        }

        return new float[]{yaw, 81.5f};
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
                            mc.thePlayer.motionY = 0.41982;
                            mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                            if(timerTower.hasReached(2500)) {
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
