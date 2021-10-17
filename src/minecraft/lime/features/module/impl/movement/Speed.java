package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.block.BlockStairs;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public class Speed extends Module {

    public Speed() {
        super("Speed", Category.MOVE);
    }

    public final EnumValue mode = new EnumValue("Mode", this, "Vanilla", "Vanilla", "Vanilla_BHOP", "Hypixel", "Verus", "Verus2", "Verus_LOWHOP", "NCP", "Funcraft", "Funcraft_YPORT");
    private final SlideValue speed = new SlideValue("Speed", this, 0.2, 5, 0.6, 0.1).onlyIf(mode.getSettingName(), "enum", "vanilla_bhop", "vanilla");
    private final BoolValue hypixelStrafe = new BoolValue("Hypixel Strafe", this, false).onlyIf(mode.getSettingName(), "enum", "hypixel");
    private double moveSpeed, lastDist;
    private int stage, ticks;
    private boolean spoofGround, firstHop, nigger;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        }
        this.moveSpeed = MovementUtils.getBaseMoveSpeed();
        stage = 0;
        ticks = 0;
        spoofGround = false;
        firstHop = true;
        nigger = mc.thePlayer.onGround;
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
        if(mode.is("vanilla")) {
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(speed.getCurrent());
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

        if(mode.is("verus2")) {
            if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) return;
            e.setGround(mc.thePlayer.onGround || spoofGround);
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
            if(mode.is("mineplex") || mode.is("funcraft")) {
                moveSpeed = 0;
                stage = 0;
            }
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(mode.is("mineplex")) {
            if(mc.thePlayer.isMoving()) {
                if(mc.thePlayer.onGround) {
                    stage = 0;
                }
                switch(stage) {
                    case 0:
                        if(mc.thePlayer.onGround) {
                            e.setY(mc.thePlayer.motionY = 0.42);
                            moveSpeed *= 2.149;
                        }
                        break;
                    case 1:
                        double difference = .84 * (this.lastDist - MovementUtils.getBaseMoveSpeed());
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

        if(mode.is("ncp") || mode.is("funcraft") || mode.is("funcraft_yport") || mode.is("vanilla_bhop")) {
            mc.timer.timerSpeed = 1.0866f;
            if(mc.thePlayer.isMoving()) {
                if(mc.thePlayer.onGround) {
                    stage = 0;
                }
                switch(stage) {
                    case 0:
                        if(mc.thePlayer.onGround) {
                            e.setY(0.399399995803833);
                            if(!mode.is("funcraft_yport")) {
                                mc.thePlayer.motionY = 0.399399995803833;
                            }
                            moveSpeed *= 2.149;
                            stage++;
                        }
                        break;
                    case 1:
                        double difference = (mode.is("ncp") ? .84  : 0.66) * (this.lastDist - MovementUtils.getBaseMoveSpeed());
                        this.moveSpeed = this.lastDist - difference;
                        ++stage;
                        break;
                    default:
                        this.moveSpeed = this.lastDist - this.lastDist / 159;
                        break;
                }
                MovementUtils.setSpeed(e, Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed()));
            } else {
                lastDist = 0;
                stage = 0;
                moveSpeed = 0;
            }
        }

        if(mode.is("verus2")) {
            if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) return;
            if(!nigger) {
                nigger = mc.thePlayer.onGround;
                return;
            }
            if(firstHop) {
                if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                    e.setY(0.41999998688697815);
                    spoofGround = true;
                    stage = 0;
                } else if (this.stage <= 6) {
                    e.setY(0);
                    ++stage;
                } else {
                    spoofGround = false;
                    firstHop = false;
                }

                mc.thePlayer.motionY = e.getY();
            } else {
                if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                    moveSpeed = 0.5;
                    e.setY(0.41999998688697815);
                    spoofGround = true;
                    stage = 0;
                } else if (this.stage <= 7) {
                    this.moveSpeed += 0.12;
                    e.setY(0);
                    ++stage;
                } else {
                    moveSpeed = 0.24;
                    spoofGround = false;
                }

                mc.thePlayer.motionY = e.getY();
                MovementUtils.setSpeed(e, this.moveSpeed - 1.0E-4D);
            }
        }
    }
}
