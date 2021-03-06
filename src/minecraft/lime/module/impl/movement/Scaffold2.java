package lime.module.impl.movement;

import java.util.Arrays;
import java.util.List;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventPacket;
import lime.module.Module;
import lime.utils.Timer;
import lime.utils.movement.MovementUtil;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;


public class Scaffold2 extends Module {
    public class BlockData {

        public BlockPos position;
        public EnumFacing face;

        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }

    }


    public static float yaw = 999, pitch = 999;
    private static List<Block> blacklistedBlocks;
    private Timer timer = new Timer();
    Timer tDelay = new Timer();
    public static String currentMode = "";
    BlockData blockdata;
    double y;
    boolean sneaking;
    int cubeSpoof;
    int count;
    public Scaffold2() {
        super("Scaffold", Keyboard.KEY_G, Category.MOVEMENT);
        blacklistedBlocks = Arrays.asList(
                Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
                Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars,
                Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore,
                Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt,
                Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
                Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
                Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily,
                Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace,
                Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence);
        Lime.setmgr.rSetting(new Setting("Scaffold", this, "Normal", "Normal"));
        Lime.setmgr.rSetting(new Setting("Tower Mode", this, "Normal", "Normal"));
        Lime.setmgr.rSetting(new Setting("Expand", this, 0.4, 0, 5, false));
        Lime.setmgr.rSetting(new Setting("Delay", this, 100, 100, 1000, true));
        Lime.setmgr.rSetting(new Setting("BlockPicker", this, true));
        Lime.setmgr.rSetting(new Setting("TowerMove", this, true));
        Lime.setmgr.rSetting(new Setting("NoSwing", this, true));
        Lime.setmgr.rSetting(new Setting("Tower", this, true));
        Lime.setmgr.rSetting(new Setting("SameY", this, false));

    }

    @Override
    public void onEnable() {
        tDelay.reset();
        yaw = 999;
        pitch = 999;
        count = 1;
        blockdata = null;
        y = mc.thePlayer.posY;
        sneaking = true;
        cubeSpoof = -1;
        super.onEnable();
    }

    public void onDisable() {
        yaw = 999;
        pitch = 999;
        blockdata = null;
        mc.timer.timerSpeed = 1f;
        if(sneaking && !mc.thePlayer.isSneaking()){
            C0BPacketEntityAction p = new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SNEAKING);
            mc.thePlayer.sendQueue.addToSendQueue(p);
        }
        if (mc.thePlayer.isSwingInProgress) {
            mc.thePlayer.swingProgress = 0;
            mc.thePlayer.swingProgressInt = 0;
            mc.thePlayer.isSwingInProgress = false;
        }
        if(cubeSpoof != mc.thePlayer.inventory.currentItem && cubeSpoof != -1){
            C09PacketHeldItemChange p = new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem);
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(p);
            cubeSpoof = -1;
        }
        super.onDisable();
    }

    
    @EventTarget
    public void onMotion(EventMotion em){
        if(!this.toggled) return;
        currentMode = getSettingByName("Scaffold").getValString();
        String towerMode = getSettingByName("Tower Mode").getValString();
        boolean shouldTower = getSettingByName("Tower").getValBoolean();
        boolean samey = getSettingByName("SameY").getValBoolean();
        if(em.getState() == EventMotion.State.PRE){
            getBestBlocks();
            if(!currentMode.equalsIgnoreCase("Legit") && !currentMode.equalsIgnoreCase("Cubecraft")){
                if(sneaking && !mc.thePlayer.isSneaking()){
                    C0BPacketEntityAction p = new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SNEAKING);
                    mc.thePlayer.sendQueue.addToSendQueue(p);
                    sneaking = !sneaking;
                }
                if(cubeSpoof != -1){
                    C09PacketHeldItemChange p = new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem);
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(p);
                    cubeSpoof = -1;
                }
            }else if(!sneaking){
                BlockPos ppos = new BlockPos(-1, -1, -1);
                ItemStack stack;
                if(cubeSpoof == -1){
                    stack = mc.thePlayer.inventory.getCurrentItem();
                }else{
                    stack = mc.thePlayer.inventory.getStackInSlot(cubeSpoof);
                }

                C08PacketPlayerBlockPlacement pl = new C08PacketPlayerBlockPlacement(ppos, 255, stack, 0, 0, 0);
                mc.thePlayer.sendQueue.addToSendQueue(pl);
            }
        }

        if (!hotbarContainBlock()) {
            blockdata = null;
            return;
        }
        double x = mc.thePlayer.posX;
        double z = mc.thePlayer.posZ;
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float YAW = mc.thePlayer.rotationYaw;
        if (!mc.thePlayer.isCollidedHorizontally){
            double[] coords = getExpandCoords(x,z,forward,strafe,YAW);
            x = coords[0];
            z = coords[1];
        }


        if (isAirBlock(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock())) {
            x = mc.thePlayer.posX;
            z = mc.thePlayer.posZ;
        }
        if(samey){
            if(mc.thePlayer.fallDistance > 1.2 + 1* MovementUtil.getJumpEffect() ||(!MovementUtil.isMoving() && mc.gameSettings.keyBindJump.pressed)){
                y = mc.thePlayer.posY;
            }
        }else{
            y = mc.thePlayer.posY;
        }
        BlockPos underPos = new BlockPos(x, y-1, z);
        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();

        BlockData data = getBlockData(underPos);

        if (em.getState() == EventMotion.State.PRE) {


            setSpeed(currentMode, em);
            if(getBlockCount()>0){
                if (shouldTower && (getSettingByName("TowerMove").getValBoolean() || !MovementUtil.isMoving())) {
                    if(!samey || !MovementUtil.isMoving()){
                        tower(towerMode, em);
                    }

                }
            }
        }
        if (isAirBlock(underBlock) && data != null) {

            if (em.getState() == EventMotion.State.PRE) {
                blockdata = data;
                float[] rot = getRotations(data.position, data.face);
                if (!currentMode.equalsIgnoreCase("Mineplex")) {
                    em.setYaw(rot[0]);
                    em.setPitch(rot[1]);
                    yaw = rot[0];
                    pitch = rot[1];
                }
                if (currentMode.equalsIgnoreCase("Normal") && !mc.gameSettings.keyBindJump.pressed && mc.thePlayer.onGround && MovementUtil.isOnGround(0.001) && mc.thePlayer.isCollidedVertically) {
                    em.setGround(false);
                }

            } else {
                
                if (!tDelay.hasReached(80) && currentMode.equalsIgnoreCase("Normal") && MovementUtil.isOnGround(0.01) && !mc.gameSettings.keyBindJump.pressed) {
                    return;
                }
                tDelay.reset();
                int slot = mc.thePlayer.inventory.currentItem;
                ItemSpoof();

                
                if (currentMode.equalsIgnoreCase("Normal") && !mc.gameSettings.keyBindJump.pressed && mc.thePlayer.onGround && MovementUtil.isOnGround(0.001) && mc.thePlayer.isCollidedVertically) {
                    em.setGround(false);
                }
                count ++;
                if((currentMode.equalsIgnoreCase("Legit") || currentMode.equalsIgnoreCase("Cubecraft")) && cubeSpoof != -1){
                    ;
                }else{
                    mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), data.position, data.face, getVec3(data.position, data.face));
                }
                if(timer.hasReached(250)){
                    timer.reset();
                }

                if (getSettingByName("NoSwing").getValBoolean()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                } else {
                    mc.thePlayer.swingItem();
                }
                mc.thePlayer.inventory.currentItem = slot;
                mc.playerController.updateController();



            }
        }
        
    }
    
    @EventTarget
    public void onPacket(EventPacket ep){
        Packet p = ep.getPacket();
        if(cubeSpoof != -1){
            if(p instanceof C08PacketPlayerBlockPlacement){
                C08PacketPlayerBlockPlacement pa = (C08PacketPlayerBlockPlacement)p;
                ItemStack stack =pa.getStack();
                if(!ItemStack.areItemsEqual(mc.thePlayer.inventory.getStackInSlot(cubeSpoof), stack)){
                    ep.setCancelled(true);
                }
            }
            if(p instanceof C07PacketPlayerDigging){
                C07PacketPlayerDigging pa = (C07PacketPlayerDigging)p;
                net.minecraft.network.play.client.C07PacketPlayerDigging.Action act  = pa.getStatus();
                if(act == net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM){
                    if(!ItemStack.areItemsEqual(mc.thePlayer.inventory.getStackInSlot(cubeSpoof), mc.thePlayer.inventory.getCurrentItem())){
                        ep.setCancelled(true);

                    }
                }
            }
        }
    }




    protected void swap(int slot, int hotbarNum) {

        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum,2, mc.thePlayer);

    }

    private boolean invCheck() {
        for (int i = 36; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                if (item instanceof ItemBlock && isValid(item)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && isValid(item)) {
                    blockCount += is.stackSize;
                }
            }
        }
        return blockCount;
    }

    public boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            if (block instanceof BlockSnow && block.getBlockBoundsMaxY() > 0.125) {
                return false;
            }
            return true;
        }

        return false;
    }
    public int getBiggestBlockSlotInv(){
        int slot = -1;
        int size = 0;
        if(getBlockCount() == 0)
            return - 1;
        for (int i = 9; i < 36; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (item instanceof ItemBlock && isValid(item)) {
                    if(is.stackSize > size){
                        size = is.stackSize;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }
    public int getBiggestBlockSlotHotbar(){
        int slot = -1;
        int size = 0;
        if(getBlockCount() == 0)
            return - 1;
        for (int i = 36; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (item instanceof ItemBlock && isValid(item)) {
                    if(is.stackSize > size){
                        size = is.stackSize;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }
    public void getBestBlocks(){

        if(getBlockCount() == 0)
            return;
        if(getSettingByName("BlockPicker").getValBoolean()){
            ItemStack is = new ItemStack(Item.getItemById(261));
            int bestInvSlot = getBiggestBlockSlotInv();
            int bestHotbarSlot = getBiggestBlockSlotHotbar();
            int bestSlot = getBiggestBlockSlotHotbar() > 0 ? getBiggestBlockSlotHotbar() : getBiggestBlockSlotInv();
            int spoofSlot = 42;
            if(bestHotbarSlot > 0 && bestInvSlot > 0){
                if (mc.thePlayer.inventoryContainer.getSlot(bestInvSlot).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getHasStack() ) {
                    if(mc.thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getStack().stackSize < mc.thePlayer.inventoryContainer.getSlot(bestInvSlot).getStack().stackSize){
                        bestSlot = bestInvSlot;
                    }
                }
            }
            if(hotbarContainBlock()){
                for (int a = 36; a < 45; a++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
                        Item item = mc.thePlayer.inventoryContainer.getSlot(a).getStack().getItem();
                        if(item instanceof ItemBlock && isValid(item)){
                            spoofSlot = a;
                            break;
                        }
                    }
                }
            }else{
                for (int a = 36; a < 45; a++) {
                    if (!mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
                        spoofSlot = a;
                        break;
                    }
                }
            }

            if (mc.thePlayer.inventoryContainer.getSlot(spoofSlot).slotNumber != bestSlot) {

                swap(bestSlot, spoofSlot - 36);
                mc.playerController.updateController();


            }
        }else{
            if (invCheck()) {

                ItemStack is = new ItemStack(Item.getItemById(261));
                for (int i = 9; i < 36; i++) {

                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                        int count = 0;
                        if (item instanceof ItemBlock && isValid(item)) {
                            for (int a = 36; a < 45; a++) {
                                if (mc.thePlayer.inventoryContainer.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(a), is, true)) {

                                    swap(i, a - 36);
                                    count++;
                                    break;
                                }
                            }

                            if (count == 0) {

                                swap(i, 7);
                            }
                            break;

                        }
                    }
                }
            }
        }
    }
    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        x += (double) face.getFrontOffsetX() / 2;
        z += (double) face.getFrontOffsetZ() / 2;
        y += (double) face.getFrontOffsetY() / 2;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += randomNumber(0.3, -0.3);
            z += randomNumber(0.3, -0.3);
        } else {
            y += randomNumber(0.3, -0.3);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += randomNumber(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += randomNumber(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    private boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if ((block.getMaterial().isSolid() || !block.isTranslucent() || block.isBlockSolid(mc.theWorld, pos, EnumFacing.DOWN) || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer)) {
            return true;
        }
        return false;
    }

    private void ItemSpoof() {
        ItemStack is = new ItemStack(Item.getItemById(261));
        try {
            for (int i = 36; i < 45; i++) {
                int theSlot = i - 36;

                if (!mc.thePlayer.inventoryContainer.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(i), is, true)
                        && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock && mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null) {
                    if (isValid(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) && mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize != 0) {
                        if (mc.thePlayer.inventory.currentItem != theSlot) {

                            if(currentMode.equalsIgnoreCase("Legit") || currentMode.equalsIgnoreCase("Cubecraft")){
                                ;
                            }else{
                                mc.thePlayer.inventory.currentItem = theSlot;
                                mc.playerController.updateController();
                            }

                        }
                        break;

                    }
                }
            }
        } catch (Exception e) {
        }
    }


    private BlockData getBlockData(BlockPos pos) {

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
        BlockPos pos19 = pos.add(-2, 0, 0);
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
        BlockPos pos29 = pos.add(2, 0, 0);
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
        BlockPos pos39 = pos.add(0, 0, 2);
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
        BlockPos pos49 = pos.add(0, 0, -2);
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
    public float[] getRotations(BlockPos block, EnumFacing face) {
        double x = block.getX() + 0.5 - mc.thePlayer.posX +  (double) face.getFrontOffsetX()/2;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ +  (double) face.getFrontOffsetZ()/2;
        double y = (block.getY() + 0.5);
        if(currentMode.equalsIgnoreCase("Legit")){
            double dist = mc.thePlayer.getDistance(block.getX()+ 0.5 + (double) face.getFrontOffsetX()/2, block.getY(), block.getZ()+ 0.5+  (double) face.getFrontOffsetZ()/2);
            //if(dist > 1.5){
            y += 0.5;
            //x += (double) face.getFrontOffsetX()/8;
            //z += (double) face.getFrontOffsetZ()/8;
            //}

        }
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
        if (yaw < 0.0F) {
            yaw += 360f;
        }
        return new float[]{yaw, pitch};
    }
    

    private boolean hotbarContainBlock() {
        int i = 36;

        while (i < 45) {
            try {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if ((stack == null) || (stack.getItem() == null) || !(stack.getItem() instanceof ItemBlock) || !isValid(stack.getItem())) {
                    i++;
                    continue;
                }
                return true;
            } catch (Exception e) {

            }
        }

        return false;

    }

    private boolean isValid(Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        } else {
            ItemBlock iBlock = (ItemBlock) item;
            Block block = iBlock.getBlock();
            if (blacklistedBlocks.contains(block)) {
                return false;
            }
        }
        return true;
    }


    public void tower(String mode, EventMotion em) {
        BlockPos underPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        BlockData data = getBlockData(underPos);
        if(!mc.gameSettings.keyBindJump.pressed){
            if((getSettingByName("TowerMove").getValBoolean() && MovementUtil.isMoving()) && (mode.equalsIgnoreCase("Normal" ))){
                if (MovementUtil.isOnGround(0.76) && !MovementUtil.isOnGround(0.75) && mc.thePlayer.motionY > 0.23 && mc.thePlayer.motionY < 0.25) {
                    mc.thePlayer.motionY = (Math.round(mc.thePlayer.posY) - mc.thePlayer.posY);
                }
                if (MovementUtil.isOnGround(0.0001)) {
                    
                }else if(mc.thePlayer.motionY > 0.1 && mc.thePlayer.posY >= Math.round(mc.thePlayer.posY) - 0.0001 && mc.thePlayer.posY <= Math.round(mc.thePlayer.posY) + 0.0001){

                    mc.thePlayer.motionY = 0;
                }
            }
            return;
        }
        if(mode.equalsIgnoreCase("Normal")){
            if(MovementUtil.isMoving()){
                if (MovementUtil.isOnGround(0.76) && !MovementUtil.isOnGround(0.75) && mc.thePlayer.motionY > 0.23 && mc.thePlayer.motionY < 0.25) {
                    mc.thePlayer.motionY = (Math.round(mc.thePlayer.posY) - mc.thePlayer.posY);
                }
                if (MovementUtil.isOnGround(0.0001)) {
                    mc.thePlayer.motionY = 0.42;
                    mc.thePlayer.motionX *= 0.9;
                    mc.thePlayer.motionZ *= 0.9;
                }else if(mc.thePlayer.posY >= Math.round(mc.thePlayer.posY) - 0.0001 && mc.thePlayer.posY <= Math.round(mc.thePlayer.posY) + 0.0001){
                    mc.thePlayer.motionY = 0;
                }
            }else{
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.jumpMovementFactor = 0;
                if (isAirBlock(underBlock) && data != null) {
                    mc.thePlayer.motionY = 0.4196;
                    mc.thePlayer.motionX *= 0.75;
                    mc.thePlayer.motionZ *= 0.75;
                }
            }
        }
    }

    public void setSpeed(String mode, EventMotion em) {
        
    }
    public double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW){
        BlockPos underPos = new BlockPos(x, mc.thePlayer.posY - 1, z);
        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        double xCalc = -999, zCalc = -999;
        double dist = 0;
        double expandDist = getSettingByName("Expand").getValDouble() *2;
        if(currentMode.equalsIgnoreCase("Cubecraft")){
            expandDist += randomNumber(0.1, -0.3);
        }
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

    public static double randomNumber(double max, double min) {
        return (Math.random() * (max - min)) + min;
    }
}
