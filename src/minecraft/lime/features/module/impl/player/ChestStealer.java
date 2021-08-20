package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.combat.CombatUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class ChestStealer extends Module {

    public ChestStealer() {
        super("Chest Stealer", Category.PLAYER);
    }

    private final SlideValue delayBeforeClose = new SlideValue("Delay before close", this, 0, 150, 100, 10);
    private final SlideValue delay = new SlideValue("Delay", this, 0, 150, 50, 5);
    private final BoolValue ignoreJunk = new BoolValue("Ignore Junk", this, true);
    private final BoolValue randomizer = new BoolValue("Randomizer", this, true);
    public final BoolValue silent = new BoolValue("Silent", this, false);
    public final BoolValue showChest = new BoolValue("Show Chest", this, true).onlyIf(silent.getSettingName(), "bool", "true");
    private final BoolValue aura = new BoolValue("Aura", this, false);

    private boolean chestOpened;
    private final Timer closeTimer = new Timer();
    private final Timer timer = new Timer();

    private ArrayList<TileEntityChest> openedChests = new ArrayList<>();

    @Override
    public void onEnable() {
        openedChests.clear();
        chestOpened = mc.currentScreen instanceof GuiChest;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(!chestOpened && mc.thePlayer.openContainer instanceof ContainerChest) {
            chestOpened = true;
            closeTimer.reset();
        } else if(chestOpened && !(mc.thePlayer.openContainer instanceof ContainerChest)) {
            chestOpened = false;
            closeTimer.reset();
            return;
        }

        if(!chestOpened && this.aura.isEnabled() && !mc.thePlayer.isSpectator()) {
            TileEntityChest chest = getNearestChest();

            if(!openedChests.contains(chest) && chest != null) {
                float[] rots = CombatUtils.getRotations(chest.getPos().getX(), chest.getPos().getY(), chest.getPos().getZ());
                e.setYaw(rots[0]);
                e.setPitch(rots[1]);

                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(chest.getPos(), getFacingDirection(chest.getPos()).getIndex(), mc.thePlayer.getCurrentEquippedItem(), chest.getPos().getX(), chest.getPos().getY(), chest.getPos().getZ()));
                this.openedChests.add(chest);
            }
        }

        if(mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;

            // Close if inventory full or chest empty
            if(((isChestEmpty(chest) && closeTimer.hasReached((long) delayBeforeClose.getCurrent())) || (InventoryUtils.isInventoryFull())) && (isValidChest(chest))) {
                mc.thePlayer.closeScreen();
            }

            if(isValidChest(chest)) {
                if(randomizer.isEnabled()) {
                    ArrayList<Integer> slots = new ArrayList<>();
                    for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                        if(chest.getLowerChestInventory().getStackInSlot(i) != null) {
                            slots.add(i);
                        }
                    }
                    Random random = new Random();
                    for (int i = 0; i < slots.size(); i++) {
                        int index = slots.get(random.nextInt(slots.size()));
                        if(chest.getLowerChestInventory().getStackInSlot(index) != null) {
                            if(timer.hasReached((long) delay.getCurrent()) && isValidItem(chest.getLowerChestInventory().getStackInSlot(index))) {
                                mc.playerController.windowClick(chest.windowId, index, 0, 1, mc.thePlayer);
                                slots.remove(i);
                                timer.reset();
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                        if(chest.getLowerChestInventory().getStackInSlot(i) != null) {
                            if(timer.hasReached((long) delay.getCurrent()) && isValidItem(chest.getLowerChestInventory().getStackInSlot(i))) {
                                mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                                timer.reset();
                            }
                        }
                    }
                }
            }
        }
    }

    private EnumFacing getFacingDirection(final BlockPos pos) {
        EnumFacing direction = null;
        if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isFullBlock()) {
            direction = EnumFacing.UP;
        }
        final MovingObjectPosition rayResult = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null) {
            return rayResult.sideHit;
        }
        return direction;
    }

    private TileEntityChest getNearestChest() {
        ArrayList<TileEntityChest> tec = new ArrayList<>();
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if(tileEntity instanceof TileEntityChest && mc.thePlayer.getDistanceSq(tileEntity.getPos()) <= 3) {
                tec.add((TileEntityChest) tileEntity);
            }
        }

        tec.sort(Comparator.comparingDouble(te -> mc.thePlayer.getDistanceSq(te.getPos())));

        if(tec.isEmpty()) return null;

        return tec.get(0);
    }

    public boolean isValidItem(ItemStack itemStack) {
        if(!ignoreJunk.isEnabled()) return true;

        Item item = itemStack.getItem();

        return item instanceof ItemSword || item instanceof ItemBlock || item instanceof ItemTool || item instanceof ItemFood
                || item instanceof ItemArmor || item instanceof ItemBow || item.getUnlocalizedName().contains("arrow") ||
                item instanceof ItemEnderPearl || (item instanceof ItemPotion && !InventoryUtils.isBadPotion(itemStack));
    }

    public boolean isChestEmpty(ContainerChest chest) {
        for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); ++i) {
            if (chest.getLowerChestInventory().getStackInSlot(i) != null && isValidItem(chest.getLowerChestInventory().getStackInSlot(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidChest(ContainerChest chest) {
        return chest.getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase().contains("chest") || chest.getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase().contains("coffre");
    }
}
