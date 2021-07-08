package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.InventoryUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.StringUtils;

@ModuleData(name = "Long Jump", category = Category.MOVEMENT)
public class LongJump extends Module {

    private enum Mode {
        Vanilla, Funcraft, NCP_Bow, Verus_Bow, Mineplex
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.Vanilla);
    private final SlideValue speed = new SlideValue("Speed", this, 1, 9, 5, 0.5);

    private double moveSpeed = 0;
    private boolean receivedS12 = false;

    private boolean boosted;
    private boolean bowd;
    private int ticks;

    @Override
    public void onEnable() {
        receivedS12 = false;
        boosted = false;
        ticks = 0;
        bowd = false;
        moveSpeed = 0;
        if(mode.is("ncp_bow") || mode.is("verus_bow")) {
            ItemStack bow = null;
            int slot = -1;
            for(int i = 36; i < 45; ++i) {
                if(InventoryUtils.getSlot(i).getHasStack()) {
                    ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                    if(itemStack.getItem() instanceof ItemBow) {
                        bow = itemStack;
                        slot = i - 36;
                    }
                }
            }

            if(bow != null) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slot));
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(bow));
            } else {
                this.toggle();
                return;
            }
        }
        /*mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY , mc.thePlayer.posZ, false));



        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));


        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688697815, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.36502771690226155, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1647732818260721, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.08307781780646721, mc.thePlayer.posZ, true));
        */
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected().name());

        // Auto Bow
        if((mode.is("verus_bow") || mode.is("ncp_bow")) && !bowd) {
            MovementUtils.setSpeed(0);
            e.setPitch(-90);
            if(ticks >= 3 && !bowd) {
                bowd = true;
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }

        if(mode.is("ncp_bow")) {
            if(receivedS12) {
                if(e.isPre()) {
                    if(mc.thePlayer.onGround) {
                        if(moveSpeed == 0) {
                            mc.thePlayer.jump();
                            MovementUtils.setSpeed(moveSpeed = 0.85);
                        } else {
                            this.toggle();
                        }
                    } else {
                        MovementUtils.setSpeed(moveSpeed -= moveSpeed / 15);
                        MovementUtils.strafe();
                    }
                }
            }
        }
        if(mode.is("verus_bow")) {
            if(!receivedS12) {
                MovementUtils.setSpeed(0);
                //mc.thePlayer.motionY = -0.0784000015258789;
                e.setPitch(-90);
            } else {
                if(e.isPre()) {
                    if(MovementUtils.isOnGround(3) && mc.thePlayer.motionY < 0) {
                        moveSpeed = 0.25;
                        mc.thePlayer.motionY = -0.0784000015258789;
                    }
                    moveSpeed -= 0.0001;
                    MovementUtils.setSpeed(moveSpeed);
                    if(mc.thePlayer.onGround) this.toggle();
                }
            }
        }

        if(mode.is("mineplex")) {
            if(!mc.thePlayer.isMoving()) return;
            MovementUtils.strafe();
            e.setGround(true);
            if(mc.thePlayer.fallDistance > 0.58) {
                System.out.println(mc.thePlayer.fallDistance);
                boosted = false;
                moveSpeed = MovementUtils.getSpeed();
                mc.thePlayer.motionY = 0.3;
                mc.thePlayer.fallDistance = 0;
                MovementUtils.setSpeed(-0.07);
            } else {
                if(!boosted && !mc.thePlayer.onGround) {
                    MovementUtils.setSpeed(moveSpeed + 0.2);
                    boosted = true;
                }
            }
        }

        if(e.isPre())
            ticks++;
    }

    @EventTarget
    public void onBoundingBox(EventBoundingBox e) {
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(!receivedS12 ) {
            if(mode.is("verus_bow")) {
                e.setX(0);
                e.setZ(0);
            } else if(mode.is("ncp_bow")) {
                e.setCanceled(true);
            }
        }


        if(mode.is("funcraft")) {
            if(MovementUtils.isOnGround(0.01)) {
                if(boosted) {
                    this.toggle();
                    return;
                }
                e.setY(mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.42));
                moveSpeed = MovementUtils.getBaseMoveSpeed() * 2.15;
            } else {
                if(!boosted) {
                    moveSpeed = 1.7;
                    boosted = true;
                } else {
                    moveSpeed -= moveSpeed / 159;
                }

                if(mc.thePlayer.motionY < -0.12 && moveSpeed > 1.42) {
                    e.setY(mc.thePlayer.motionY = -0.12);
                }
            }
            MovementUtils.setSpeed(e, moveSpeed);
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if(packet.getEntityID() != mc.thePlayer.getEntityId()) return;
            receivedS12 = true;

            if(mode.is("verus_bow")) {
                mc.thePlayer.motionY = 1;
                moveSpeed = speed.getCurrent();
                MovementUtils.setSpeed(moveSpeed);
            }
        }
    }
}
