package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.ui.notifications.Notification;
import lime.utils.movement.MovementUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.other.MathUtils;
import lime.utils.other.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class LongJump extends Module {

    public LongJump() {
        super("Long Jump", Category.MOVEMENT);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Vanilla", "Vanilla", "Funcraft", "Mineplex", "Hypixel", "Hypixel_Bow", "Verus", "Verus_Bow", "Kokscraft");
    private final SlideValue speed = new SlideValue("Speed", this, 1, 9, 5, 0.5).onlyIf(mode.getSettingName(), "enum", "verus_bow", "verus");

    private double moveSpeed = 0, y;

    private boolean boosted, bowd, receivedS12, back;
    private int ticks, stage;

    @Override
    public void onEnable() {
        receivedS12 = back = boosted = bowd = false;
        moveSpeed = y = 0;
        if(mode.is("mineplex")) {
            moveSpeed = 0.25;
        }
        stage = ticks = 0;
        if(mode.is("verus") && (!mc.thePlayer.onGround || new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ).getBlock() instanceof BlockAir)) {
            Lime.getInstance().getNotificationManager().addNotification("Error", "Can damage only on the ground!", Notification.Type.FAIL);
            this.toggle();
            return;
        }
        if(mode.is("hypixel_bow") || mode.is("verus_bow")) {
            ItemStack bow = null;
            int slot = -1;
            for(int i = 36; i < 45; ++i) {
                if(InventoryUtils.getSlot(i).getHasStack()) {
                    ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                    if(itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemFishingRod) {
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
            PlayerUtils.koksCraftDamage();
        }
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());

        // Auto Bow
        if((mode.is("verus_bow") || mode.is("hypixel_bow")) && !bowd) {
            MovementUtils.setSpeed(0);
            e.setPitch(-90);
            if(ticks >= 3 && !bowd) {
                bowd = true;
                mc.getNetHandler().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                mc.getNetHandler().sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
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

        if(e.isPre())
            ticks++;
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(!receivedS12) {
            if(mode.is("verus_bow") || mode.is("verus")) {
                e.setX(0);
                e.setZ(0);
            } else if(mode.is("hypixel_bow") || mode.is("survivaldub")) {
                e.setCanceled(true);
            }
        }

        if(mode.is("hypixel_bow")) {
            if(receivedS12) {
                if(mc.thePlayer.onGround) {
                    if(moveSpeed == 0) {
                        e.setY(mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.6));
                        int amplifier = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() : -1;
                        MovementUtils.setSpeed(e, moveSpeed = (amplifier == -1 ? .7 : amplifier == 0 ? .8 : .85));
                        ticks = 0;
                    } else {
                        this.toggle();
                    }
                } else {
                    MovementUtils.setSpeed(e, Math.max(moveSpeed -= moveSpeed / 30, MovementUtils.getBaseMoveSpeed()));
                    e.setY(e.getY() * 0.7);
                }
            }
        }

        if(mode.is("hypixel")) {
            if(mc.thePlayer.onGround) {
                if(moveSpeed == 0) {
                    e.setY(mc.thePlayer.motionY = 0.42);
                    int amplifier = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() : -1;
                    MovementUtils.setSpeed(e, moveSpeed = (amplifier == -1 ? .55 : amplifier == 0 ? .8 : .85));
                } else {
                    this.toggle();
                }
            } else {
                MovementUtils.setSpeed(e, Math.max(moveSpeed -= moveSpeed / 30, MovementUtils.getBaseMoveSpeed()));
                if(ticks > 10 && ticks < 30) {
                    e.setY(e.getY() * 0.8);
                }
            }
        }

        if(mode.is("mineplex")) {
            if(stage == 0 && mc.thePlayer.isMoving()) {
                if(mc.thePlayer.onGround) {
                    e.setY(mc.thePlayer.motionY = 0.42 + y);
                    MovementUtils.setSpeed(e, -0.07);
                    moveSpeed += 0.45;
                    ticks = 0;
                    y += 0.04;
                    return;
                }

                moveSpeed -= moveSpeed / 80;
                MovementUtils.setSpeed(e, back ? -moveSpeed : moveSpeed);
                back = !back;

                if(moveSpeed > 1.25) {
                    stage = 1;
                    moveSpeed = 1.45;
                    back = false;
                }
            }
            if(stage == 1) {
                mc.timer.timerSpeed = 1;
                if(mc.thePlayer.onGround) {
                    MovementUtils.setSpeed(e, -0.07);
                    e.setY(mc.thePlayer.motionY = 0.42 + y);
                    back = true;
                } else {
                    if(moveSpeed > 0.8) {
                        e.setY(mc.thePlayer.motionY += 0.028888888888888 + MathUtils.random(0.00005, 0.00105));
                    }
                    MovementUtils.setSpeed(e, moveSpeed -= moveSpeed / 49);
                    if(back && mc.thePlayer.onGround)
                        this.toggle();
                }
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

                if(mc.thePlayer.motionY < -0.12) {
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
                //mc.timer.timerSpeed = 0.8f;
                mc.thePlayer.motionY = 0.2;
                MovementUtils.setSpeed(1.4);
                this.toggle();
            }
        }
        if(e.getPacket() instanceof C0FPacketConfirmTransaction && mode.is("survivaldub")) {
            e.setCanceled(true);
        }
    }
}
