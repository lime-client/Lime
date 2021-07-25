package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.exploit.Blink;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.ui.notifications.Notification;
import lime.utils.movement.MovementUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.other.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleData(name = "Long Jump", category = Category.MOVEMENT)
public class LongJump extends Module {

    private enum Mode {
        Vanilla, Funcraft, NCP_Bow, Verus, Verus_Bow, Mineplex, Kokscraft
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.Vanilla);
    private final SlideValue speed = new SlideValue("Speed", this, 1, 9, 5, 0.5).onlyIf(mode.getSettingName(), "enum", "verus_bow", "verus");

    private double moveSpeed = 0;
    private boolean receivedS12 = false;

    private boolean boosted;
    private boolean bowd;
    private int ticks, stage;

    @Override
    public void onEnable() {
        receivedS12 = false;
        boosted = false;
        bowd = false;
        moveSpeed = 0;
        stage = 0;
        ticks = 0;
        if(mode.is("verus") && (!mc.thePlayer.onGround || new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ).getBlock() instanceof BlockAir)) {
            Lime.getInstance().getNotificationManager().addNotification(new Notification("Error", "Can damage only on the ground!", Notification.Type.ERROR));
            this.toggle();
            return;
        }
        if(mode.is("ncp_bow") || mode.is("verus_bow") || mode.is("survivaldub")) {
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
                mc.getNetHandler().sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(bow));
            } else {
                this.toggle();
                return;
            }
        }

        if(mode.is("verus")) {
            PlayerUtils.verusDamage();
            mc.thePlayer.jump();
        }

        if(mode.is("kokscraft")) {
            double[] jumpValues = new double[] {
                    0,
                    0.41999998688698,
                    0.7531999805212,
                    1.00133597911215,
                    1.166109260938214,
                    1.24918707874468,
                    1.25220334025373,
                    1.17675927506424,
                    1.024424088213685,
                    0.7967356006687,
                    0.495200877005914,
                    0.121296840539195,
                    0
            };
            double startPosY = mc.thePlayer.posY;
            for (int i = 0;i < 3;i++) {
                for (double jumpValue : jumpValues) {
                    mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, startPosY + jumpValue, mc.thePlayer.posZ, false));
                }
            }
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, startPosY, mc.thePlayer.posZ, true));
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer(true));
        }
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected().name());

        // Auto Bow
        if((mode.is("verus_bow") || mode.is("ncp_bow") || mode.is("survivaldub")) && !bowd) {
            MovementUtils.setSpeed(0);
            e.setPitch(-90);
            if(ticks >= 3 && !bowd) {
                bowd = true;
                mc.getNetHandler().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                mc.getNetHandler().sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }

        if(mode.is("ncp_bow")) {
            if(receivedS12) {
                if(e.isPre()) {
                    if(mc.thePlayer.onGround) {
                        if(moveSpeed == 0) {
                            mc.thePlayer.jump();
                            MovementUtils.setSpeed(moveSpeed = .85);
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

        if(mode.is("verus")) {
            if(mc.thePlayer.motionY < 0) {
                mc.thePlayer.motionY = -0.0784000015258789;
                if(mc.thePlayer.onGround) this.toggle();
            }
            if(moveSpeed - 0.21 > 0.25) {
                moveSpeed -= 0.21;
                MovementUtils.setSpeed(moveSpeed);
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
    public void onMove(EventMove e) {
        if(!receivedS12) {
            if(mode.is("verus_bow") || mode.is("verus")) {
                e.setX(0);
                e.setZ(0);
            } else if(mode.is("ncp_bow") || mode.is("survivaldub")) {
                e.setCanceled(true);
            }
        }

        if(mode.is("funcraft")) {
            if(mc.thePlayer.isCollidedHorizontally)  {
                this.toggle();
                return;
            }
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
                } else if(moveSpeed < 1.42) {
                    e.setY(e.getY() * 0.7);
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

            if(mode.is("verus")) {
                mc.thePlayer.motionY += 0.8;
                MovementUtils.setSpeed(moveSpeed = speed.getCurrent());
            }

            if(mode.is("verus_bow")) {
                MovementUtils.vClip(4);
                moveSpeed = speed.getCurrent();
                MovementUtils.setSpeed(moveSpeed);
            }

            if(mode.is("kokscraft")) {
                e.setCanceled(true);
                mc.thePlayer.motionY = 0.8;
                MovementUtils.setSpeed(1);
                this.toggle();
            }
        }
        if(e.getPacket() instanceof C0FPacketConfirmTransaction && mode.is("survivaldub")) {
            e.setCanceled(true);
        }
    }
}
