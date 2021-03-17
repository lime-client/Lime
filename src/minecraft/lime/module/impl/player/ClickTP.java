package lime.module.impl.player;

import lime.settings.impl.ListValue;
import lime.events.EventTarget;
import lime.events.impl.Event3D;
import lime.events.impl.EventMotion;
import lime.module.Module;
import lime.utils.ChatUtils;
import lime.utils.Timer;
import lime.utils.movement.MovementUtil;
import lime.utils.render.UtilGL;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ClickTP extends Module {

    private boolean canTP;
    private int delay;
    public BlockPos endPos;
    Timer cooldown = new Timer();
    String MODE = "MODE";
    private Timer timer = new Timer();
    private ListValue mode = new ListValue("Mode", this, "Basic", "Basic", "NCP");

    public ClickTP() {
        super("ClickTP", 0, Category.PLAYER);
    }
    @Override
    public void onEnable(){
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static MovingObjectPosition getBlinkBlock() {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3 var4 = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        Vec3 var5 = mc.thePlayer.getLook(mc.timer.renderPartialTicks);
        Vec3 var6 = var4.addVector(var5.xCoord * 70, var5.yCoord * 70, var5.zCoord * 70);
        return mc.thePlayer.worldObj.rayTraceBlocks(var4, var6, false, false, true);
    }

    @EventTarget
    public void onEvent(EventMotion event) {

        try {
            if (mc.thePlayer.getHeldItem() != null && (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword))) {
                return;
            }
        } catch (Exception e) {
        }
        if (event.isPre()) {
            if(mode.getValue().equalsIgnoreCase("Basic")){
                if (canTP && Mouse.isButtonDown(1) && !mc.thePlayer.isSneaking() && delay == 0 && mc.inGameHasFocus && getBlinkBlock().entityHit == null && !(getBlock(getBlinkBlock().getBlockPos()) instanceof BlockChest)) {

                    event.setCancelled(true);
                    endPos = getBlinkBlock().getBlockPos().add(0,1,0);
                    teleport(endPos);
                    delay = 5;
                    event.setCancelled(false);
                }
            }else if(mode.getValue().equalsIgnoreCase("NCP")){
                if (cooldown.hasReached(500) && canTP && Mouse.isButtonDown(2) && !mc.thePlayer.isSneaking() && delay == 0 && mc.inGameHasFocus && getBlinkBlock().entityHit == null && !(getBlock(getBlinkBlock().getBlockPos()) instanceof BlockChest)) {
                    cooldown.reset();
                    event.setCancelled(true);
                    endPos = getBlinkBlock().getBlockPos();

                    if(endPos.getY()+1 > mc.thePlayer.posY){
                        ChatUtils.sendMsg("§cInvalid Position!");
                        return;
                    }
                    endPos = new BlockPos(endPos.getX(), mc.thePlayer.posY, endPos.getZ());
                    final double[] startPos = {mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
                    ncpTeleport(startPos, endPos);
                    delay = 5;
                    event.setCancelled(false);
                }
            }
            if (delay > 0) {
                --delay;
            }
        }


    }

    @EventTarget
    public void on3D(Event3D e){
        try {
            if (mc.thePlayer.getHeldItem() != null && (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword))) {
                return;
            }
            final int x = getBlinkBlock().getBlockPos().getX();
            final int y = getBlinkBlock().getBlockPos().getY();
            final int z = getBlinkBlock().getBlockPos().getZ();
            final Block block1 = getBlock(x, y, z);
            final Block block2 = getBlock(x, y + 1, z);
            final Block block3 = getBlock(x, y + 2, z);
            final boolean blockBelow = !(block1 instanceof BlockSign) && block1.getMaterial().isSolid();
            final boolean blockLevel = !(block2 instanceof BlockSign) && block1.getMaterial().isSolid();
            final boolean blockAbove = !(block3 instanceof BlockSign) && block1.getMaterial().isSolid();
            if (getBlock(getBlinkBlock().getBlockPos()).getMaterial() != Material.air && blockBelow && blockLevel && blockAbove && !(getBlock(getBlinkBlock().getBlockPos()) instanceof BlockChest)) {
                canTP = true;
                GL11.glPushMatrix();
                UtilGL.pre3D();
                mc.entityRenderer.setupCameraTransform(e.getPartialTicks(), 2);

                GL11.glColor4d(0, 0.6, 0, 0.25);
                if(mode.getValue().equalsIgnoreCase("NCP")){
                    if(mc.thePlayer.posY < (y+1)){
                        GL11.glColor4d(0.6, 0, 0, 0.25);
                    }
                }
                UtilGL.drawBoxFilled(new AxisAlignedBB(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ, x - RenderManager.renderPosX + 1.0, y + getBlock(getBlinkBlock().getBlockPos()).getBlockBoundsMaxY() - RenderManager.renderPosY, z - RenderManager.renderPosZ + 1.0));
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
                UtilGL.post3D();
                GL11.glPopMatrix();
            } else {
                canTP = false;
            }
        } catch (Exception ez) {

        }
    }

    public Block getBlock(final int x, final int y, final int z) {
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public Block getBlock(final BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public void teleport(final BlockPos endPos){
        double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
        double distanceEntreLesPackets = 5;
        double xtp, ytp, ztp = 0;

        if(dist> distanceEntreLesPackets){
            double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;
            xtp = mc.thePlayer.posX;
            ytp = mc.thePlayer.posY;
            ztp = mc.thePlayer.posZ;
            for (int i = 1; i < nbPackets;i++){
                double xdi = (endPos.getX() - mc.thePlayer.posX)/( nbPackets);
                xtp += xdi;

                double zdi = (endPos.getZ() - mc.thePlayer.posZ)/( nbPackets);
                ztp += zdi;

                double ydi = (endPos.getY() - mc.thePlayer.posY)/( nbPackets);
                ytp += ydi;
                C03PacketPlayer.C04PacketPlayerPosition Packet= new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp, ztp, true);

                mc.thePlayer.sendQueue.addToSendQueue(Packet);
            }

            mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);
        }else{
            mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());
        }
    }

    public void ncpTeleport(final double[] startPos, final BlockPos endPos){

        double distx = startPos[0] - endPos.getX()+ 0.5;
        double disty = startPos[1] - endPos.getY();
        double distz = startPos[2] - endPos.getZ()+ 0.5;
        double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
        double distanceEntreLesPackets = 0.31 + MovementUtil.getSpeedEffect()/20;
        double xtp, ytp, ztp = 0;
        if(dist> distanceEntreLesPackets){

            double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;

            xtp = mc.thePlayer.posX;
            ytp = mc.thePlayer.posY;
            ztp = mc.thePlayer.posZ;
            double count = 0;
            for (int i = 1; i < nbPackets;i++){
                double xdi = (endPos.getX() - mc.thePlayer.posX)/( nbPackets);
                xtp += xdi;

                double zdi = (endPos.getZ() - mc.thePlayer.posZ)/( nbPackets);
                ztp += zdi;

                double ydi = (endPos.getY() - mc.thePlayer.posY)/( nbPackets);
                ytp += ydi;
                count ++;

                if(!mc.theWorld.getBlockState(new BlockPos(xtp, ytp-1, ztp)).getBlock().isFullCube()){
                    if (count <= 2) {
                        ytp += 2E-8;
                    } else if (count >= 4) {
                        count = 0;
                    }
                }
                C03PacketPlayer.C04PacketPlayerPosition Packet= new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp, ztp, false);
                mc.thePlayer.sendQueue.addToSendQueue(Packet);
            }

            mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);

        }else{
            mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());

        }
    }

}
