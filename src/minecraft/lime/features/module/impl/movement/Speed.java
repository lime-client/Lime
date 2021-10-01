package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.world.Scaffold;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.ChatUtils;
import net.minecraft.block.BlockStairs;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Speed extends Module {

    public Speed() {
        super("Speed", Category.MOVEMENT);
    }

    private final EnumValue mode = new EnumValue("Mode", this, "Vanilla", "Vanilla", "Vanilla_BHOP", "Hypixel", "Verus", "Verus_LOWHOP", "NCP", "Funcraft", "Funcraft2", "Funcraft_YPORT", "Mineplex");
    private final SlideValue speed = new SlideValue("Speed", this, 0.2, 5, 0.6, 0.1).onlyIf(mode.getSettingName(), "enum", "vanilla_bhop", "vanilla", "mineplex");
    private final BoolValue hypixelStrafe = new BoolValue("Hypixel Strafe", this, false).onlyIf(mode.getSettingName(), "enum", "hypixel");
    private double moveSpeed, lastDist;
    private final ArrayList<Packet<?>> packets = new ArrayList<>();
    private int stage, ticks;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        }
        this.moveSpeed = MovementUtils.getBaseMoveSpeed();
        stage = 0;
        ticks = 0;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        if(mc.thePlayer != null)
            mc.thePlayer.speedInAir = 0.02f;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(mode.is("vanilla") || mode.is("vanilla_bhop")) {
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(speed.getCurrent());
                if(mode.is("vanilla_bhop")) {
                    if(mc.thePlayer.isMoving() && mc.thePlayer.onGround)
                        mc.thePlayer.motionY = 0.42;
                    if(mc.thePlayer.isMoving()) {
                        MovementUtils.setSpeed(speed.getCurrent());
                    }
                    if(mc.thePlayer.ticksExisted % 3 == 0) mc.thePlayer.motionY -= 0.04;
                }
            }
        }

        if(mode.is("verus") || mode.is("verus_lowhop")) {
            if(!MovementUtils.isOnGround(0.4)) {
                if(mode.is("verus")) {
                    mc.thePlayer.motionY -= 0.0000000075;
                }
                mc.timer.timerSpeed = 1;
            } else {
                if(mc.thePlayer.isMoving())
                    mc.timer.timerSpeed = 1.005f;
            }

            if(mc.thePlayer.motionY > 0.2 && mode.is("verus_lowhop")) {
                mc.thePlayer.motionY = -0.0784000015258789;
            }

            if (mc.thePlayer.isMoving()) {
                double tickBoost = mc.thePlayer.ticksExisted % 20 == 0 ? 0.1 : 0;
                double amplifier = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() : 0;
                double speedBoost = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? amplifier == 1 ? 0.035 : amplifier > 1 ? 0.035 * (amplifier / 2) : 0.035 / 2 : 0;
                double motionBoost = MovementUtils.isOnGround(0.15) && !mc.thePlayer.onGround ? 0.045 : 0;

                double boost = 0;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    boost += 0.125;
                }

                if(MovementUtils.isOnGround(0.15) && boost == 0) {
                    mc.thePlayer.motionY -= 0.0075;
                }

                if(mc.thePlayer.moveStrafing == 0)
                    MovementUtils.setSpeed(0.3345 + speedBoost + tickBoost + motionBoost + boost);
                else
                    MovementUtils.setSpeed(0.333 + speedBoost + tickBoost + motionBoost + boost);

            } else
                MovementUtils.setSpeed(0);
        }

        if(mode.is("hypixel")) {
            if(mc.thePlayer.isMoving() && e.isPre()) {
                if(mc.thePlayer.isCollidedHorizontally) {
                    MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed(.258));
                }
                if(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ).getBlock() instanceof BlockStairs) {
                    MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed());
                }

                if(mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.39999998);
                    double sped = 0.47;
                    if(!hypixelStrafe.isEnabled()) {
                        sped = MovementUtils.getBaseMoveSpeed(0.47);
                    }
                    MovementUtils.setSpeed(moveSpeed = sped);
                } else {
                    //mc.timer.timerSpeed = 1.2f;
                    if(hypixelStrafe.isEnabled()) {
                        MovementUtils.setSpeed(MovementUtils.getSpeed());
                    }
                }
            }
        }

        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            lastDist = 0;
            if(mode.is("mineplex")) {
                moveSpeed = 0;
            }
            if(mode.is("funcraft2")) {
                moveSpeed = MovementUtils.getBaseMoveSpeed();
            }
        }
        if(e.getPacket() instanceof C0FPacketConfirmTransaction) {

        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(mode.is("mineplex")) {
            if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                moveSpeed = Math.min(moveSpeed < 0.5 ? 0.8 : moveSpeed + .4, speed.getCurrent());
                e.setY(mc.thePlayer.motionY = 0.42);
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, true));
            }
            if(mc.thePlayer.isCollidedHorizontally || !mc.thePlayer.isMoving()) {
                moveSpeed = 0.32;
            }
            if(KillAura.getEntity() != null && Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class).isToggled()) {
                TargetStrafe targetStrafe = (TargetStrafe) Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
                targetStrafe.setMoveSpeed(e, Math.max(Math.min(5, moveSpeed -= moveSpeed / 44), 0.4));
            } else {
                MovementUtils.setSpeed(e, Math.max(Math.min(5, moveSpeed -= moveSpeed / 44), 0.4));
            }
        }

        if(mode.is("funcraft2")) {
            if(!mc.thePlayer.isMoving()) {
                moveSpeed = 0;
                stage = 0;
                return;
            }
            mc.timer.timerSpeed = 1.0888F;
            if(stage == 1 && mc.thePlayer.isMoving()) {
                moveSpeed = 2.5 * MovementUtils.getBaseMoveSpeed() - 0.1;
            } else if(stage != 2 || !mc.thePlayer.isMoving()) {
                if(stage == 3) {
                    moveSpeed = lastDist - 0.66D * (lastDist - MovementUtils.getBaseMoveSpeed());
                } else {
                    List<AxisAlignedBB> collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, mc.thePlayer.motionY, 0));
                    if((collidingList.size() > 0 || mc.thePlayer.isCollidedVertically) && stage > 0) {
                        stage = mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0 ? 0 : 1;
                    }
                    moveSpeed = lastDist - lastDist / 159;
                }
            } else {
                e.setY(mc.thePlayer.motionY = 0.3999);
                moveSpeed *= 2.14999;
            }

            if (KillAura.getEntity() != null) {
                TargetStrafe targetStrafe2 = (TargetStrafe) Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
                targetStrafe2.setMoveSpeed(e, moveSpeed = Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed() * 1.3));
            } else {
                MovementUtils.setSpeed(e, moveSpeed = Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed() * 1.3));
            }
            if(mc.thePlayer.isMoving()) {
                ++stage;
            }
        }

        if(mode.is("ncp") || mode.is("funcraft") || mode.is("funcraft_yport")) {
            mc.timer.timerSpeed = 1.0866f;
            if(mc.thePlayer.isMoving()) {
                if(mc.thePlayer.onGround) {
                    stage = 1;
                }
                switch(stage) {
                    case 0:
                        MovementUtils.setSpeed(e, moveSpeed = 1.35 * MovementUtils.getBaseMoveSpeed() - 0.01);
                        break;
                    case 1:
                        if(mc.thePlayer.onGround) {
                            e.setY(0.399399995803833);
                            if(!mode.is("funcraft_yport")) {
                                mc.thePlayer.motionY = 0.399399995803833;
                            }
                            moveSpeed *= 2.149;
                        }
                        break;
                    case 2:
                        double difference = (mode.is("ncp") ? .84  : 0.66) * (this.lastDist - MovementUtils.getBaseMoveSpeed());
                        this.moveSpeed = this.lastDist - difference;
                        break;
                    default:
                        this.moveSpeed = this.lastDist - this.lastDist / 159;
                        break;
                }
                ++stage;
                MovementUtils.setSpeed(e, Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed()));
            }
        }
    }
}
