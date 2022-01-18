package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventEntityAction;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.movement.MovementUtils;
import lime.utils.other.ChatUtils;
import lime.utils.other.Timer;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

public class Speed extends Module {

    public Speed() {
        super("Speed", Category.MOVE);
    }

    public final EnumProperty mode = new EnumProperty("Mode", this, "Vanilla", "Vanilla", "Vanilla_BHOP", "Funcraft New", "Hypixel", "ZoneCraft", "Cubecraft", "Verus", "Verus Ground", "Verus Float", "Verus_LOWHOP", "NCP", "Funcraft", "Funcraft_YPORT", "Dev");
    private final NumberProperty speed = new NumberProperty("Speed", this, 0.2, 5, 0.6, 0.1).onlyIf(mode.getSettingName(), "enum", "vanilla_bhop", "vanilla");
    private final BooleanProperty bps24 = new BooleanProperty("24 BPS", this, false).onlyIf(mode.getSettingName(), "enum", "Funcraft New");
    private double moveSpeed, lastDist;
    private int stage;
    private boolean spoofGround, firstHop, nigger;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        }
        this.moveSpeed = MovementUtils.getBaseMoveSpeed();
        stage = 0;
        spoofGround = false;
        firstHop = true;
        nigger = mc.thePlayer.onGround;
    }

    private final Timer timer = new Timer();

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        if(mc.thePlayer != null)
            mc.thePlayer.speedInAir = 0.02f;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        this.setSuffix(mode.getSelected());
        if(mode.is("vanilla")) {
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(speed.getCurrent());
            }
        }

        if(mode.is("dev")) {
            if(e.isPre()) {
                if (timer.hasReached(300L))
                    timer.reset();
                mc.timer.timerSpeed = 1F + (float)timer.hasTimeLeft(250L) / 250F * 1.5F;
                if (mc.thePlayer.onGround)
                    MovementUtils.strafe();
            }
        }

        if(mode.is("verus_lowhop")) {
            if(mc.thePlayer.motionY > 0.2) {
                mc.thePlayer.motionY = -0.0784000015258789;
            }

            if (mc.thePlayer.isMoving()) {
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
                    MovementUtils.setSpeed(0.3345 + speedBoost + motionBoost + boost);
                else
                    MovementUtils.setSpeed(0.333 + speedBoost + motionBoost + boost);

            } else
                MovementUtils.setSpeed(0);
        }

        if(mode.is("verus float")) {
            if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) return;
            if(!mc.thePlayer.isMoving()) {
                firstHop = true;
                stage = 0;
                spoofGround = false;
                return;
            }
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
                    double sped = .42;
                    MovementUtils.setSpeed(moveSpeed = sped);
                }
            }
        }
    }

    @EventTarget
    public void onEntityAction(EventEntityAction e) {
        if(mode.is("Verus Ground")) {
            e.setShouldJump(false);
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer.ticksExisted > 30) {
            if(mode.is("zonecraft")) {
                e.setCanceled(true);
                S08PacketPlayerPosLook p = (S08PacketPlayerPosLook) e.getPacket();
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch(), false));
            }
            lastDist = 0;
            if(mode.is("mineplex") || mode.is("funcraft") || mode.is("Funcraft New")) {
                moveSpeed = 0;
                stage = 0;
            }
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(mode.is("verus")) {
            if(mc.thePlayer.isMoving()) {
                if(mc.thePlayer.onGround) {
                    e.setY(mc.thePlayer.motionY = 0.41999998688697815);
                    moveSpeed = 0.6;
                } else {
                    moveSpeed = MovementUtils.getBaseMoveSpeed() * (MovementUtils.hasSpeed() ? 1.1 + Math.random() * 0.005 : 1.253);
                }

                MovementUtils.setSpeed(e, moveSpeed);
            }
        }

        if(mode.is("zonecraft")) {
            if(mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition().down(), 0, new ItemStack(Blocks.stone, 1), 0.54345F,0.523234F, 0.5435345F));
                MovementUtils.setSpeed(e, 0.5 + (Math.random() * 0.003));
            }
        }
        if(mode.is("Verus Ground")) {
            if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) return;

            if(mc.thePlayer.isMoving() && MovementUtils.isOnGround(0.02) && !mc.thePlayer.isCollidedHorizontally) {
                if(firstHop) {
                    mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0, 0.5f, 0));
                    e.setY(mc.thePlayer.motionY = 0.01);
                    firstHop = false;
                    stage = 0;
                    moveSpeed = 0;
                } else {
                    if(stage >= 8) {
                        stage = 0;
                        mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0, 0.5f, 0));
                        e.setY(mc.thePlayer.motionY = 0.01);
                        moveSpeed = 0;
                    }
                    if(mc.thePlayer.onGround) {
                        stage++;
                        MovementUtils.setSpeed(e, moveSpeed += RandomUtils.nextDouble(0.10, 0.18));
                    }
                }
            } else {
                firstHop = true;
                stage = 0;
                moveSpeed = 0;
            }
        }


        if(mode.is("cubecraft")) {
            mc.timer.timerSpeed = 1.2f;
            if(mc.thePlayer.onGround) {
                stage = 0;
            }

            if(mc.thePlayer.isMoving()) {
                switch(stage) {
                    case 0:
                        e.setY(mc.thePlayer.motionY = .3999);
                        moveSpeed = MovementUtils.getBaseMoveSpeed(0.25) * 1.64;
                        break;
                    case 1:
                        double difference = .86 * (this.lastDist - MovementUtils.getBaseMoveSpeed(0.25));
                        moveSpeed = lastDist - difference;
                        break;
                    default:
                        moveSpeed = lastDist - lastDist / 72;
                        break;
                }

                ++stage;
                MovementUtils.setSpeed(e, Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed(0.25)));
            }
        }

        if(mode.is("Funcraft New")) {
            if(!mc.thePlayer.isMoving()) {
                moveSpeed = 0;
                MovementUtils.setSpeed(e, 0);
                return;
            }
            if (mc.thePlayer.onGround) {
                for (int i = 0; i < (bps24.isEnabled() ? 7 : 5); i++) {
                    MovementUtils.hClip(0.15);
                    if(mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox()) && mc.thePlayer.getPosition().getBlock().isFullBlock()) {
                        MovementUtils.hClip(-0.15);
                        return;
                    }
                    mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                }
                e.setY(mc.thePlayer.motionY = 0.42);
                moveSpeed = MovementUtils.getBaseMoveSpeed(0.6);
                nigger = true;
            } else {
                if (nigger) {
                    moveSpeed = bps24.isEnabled() ? 1.2 : MovementUtils.getBaseMoveSpeed(0.9);
                    nigger = false;
                    MovementUtils.setSpeed(e, moveSpeed);
                    return;
                }
                moveSpeed = Math.max(moveSpeed - moveSpeed / 154F, MovementUtils.getBaseMoveSpeed());
            }
            MovementUtils.setSpeed(e, moveSpeed);
        }

        if(mode.is("ncp") || mode.is("funcraft") || mode.is("funcraft_yport") || mode.is("vanilla_bhop")) {
            if(mode.is("vanilla_bhop") && Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) {
                return;
            }
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
                            moveSpeed *= 2.14999;
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

        if(mode.is("verus float")) {
            if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) return;
            if(!mc.thePlayer.isMoving()) {
                firstHop = true;
                stage = 0;
                return;
            }
            if(!nigger) {
                nigger = mc.thePlayer.onGround;
                return;
            }
            if(firstHop) {
                if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                    e.setY(0.41999998688697815);
                    spoofGround = true;
                    stage = 0;
                } else if (this.stage <= 7) {
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
                MovementUtils.setSpeed(e, this.moveSpeed - 1E-4);
            }
        }
    }
}
