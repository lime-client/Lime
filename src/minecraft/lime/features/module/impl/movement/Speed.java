package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;

@ModuleData(name = "Speed", category = Category.MOVEMENT)
public class Speed extends Module {
    private enum Mode {
        VANILLA, VANILLA_BHOP, VERUS, VERUS_LOWHOP, NCP, FUNCRAFT
    }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.VANILLA);

    private double moveSpeed;
    private double lastDist;
    private int stage;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        }
        this.moveSpeed = MovementUtils.getBaseMoveSpeed();
        stage = 1;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(mode.is("vanilla") || mode.is("vanilla_bhop")) {
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(1);
                if(mode.is("vanilla_bhop")) {
                    if(mc.thePlayer.isMoving() && mc.thePlayer.onGround) mc.thePlayer.motionY = 0.42;
                    if(mc.thePlayer.isMoving()) {
                        MovementUtils.setSpeed(0.6);
                        if(mc.thePlayer.hurtTime > 0)
                            MovementUtils.setSpeed(1.4);
                    }
                    if(mc.thePlayer.ticksExisted % 3 == 0) mc.thePlayer.motionY -= 0.04;
                }
            }
        }
        if(mode.is("verus") || mode.is("verus_lowhop")) {
            if(!MovementUtils.isOnGround(0.4)) {
                if(mode.is("verus")) {
                    mc.thePlayer.motionY -= 0.0000000075;
                } else {
                    mc.thePlayer.motionY = -0.0784000015258789;
                }
                mc.timer.timerSpeed = 1;
            } else {
                if(mc.thePlayer.isMoving())
                    mc.timer.timerSpeed = 1.005f;
            }

            if (mc.thePlayer.isMoving()) {
                double tickBoost = mc.thePlayer.ticksExisted % 20 == 0 ? 0.1 : 0; // 0.25 max, mais le bhop est moins smooth
                double amplifier = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() : 0;
                double speedBoost = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? amplifier == 1 ? 0.035 : amplifier > 1 ? 0.035 * (amplifier / 2) : 0.035 / 2 : 0;
                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump();
                if(mc.thePlayer.moveStrafing == 0)
                    MovementUtils.setSpeed(0.3345 + speedBoost + tickBoost);
                else
                    MovementUtils.setSpeed(0.333 + speedBoost + tickBoost);
            } else
                MovementUtils.setSpeed(0);
        }

        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            lastDist = 0;
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(mode.is("ncp") || mode.is("funcraft")) {
            if (mc.thePlayer.isMoving()) {
                mc.timer.timerSpeed = 1.0866f;
                mc.thePlayer.motionX *= 1.0199999809265137;
                mc.thePlayer.motionZ *= 1.0199999809265137;
                if(mc.thePlayer.ticksExisted % 5 == 0 && mode.is("funcraft")) {
                    mc.timer.timerSpeed = 1.4f;
                }
                if(mc.thePlayer.onGround)
                    this.stage = 1;
                if(stage == 0) {
                    this.moveSpeed = 1.10 * MovementUtils.getBaseMoveSpeed() - 0.01;
                    this.stage = 1;
                } else if (stage == 1 && mc.thePlayer.onGround) {
                    stage = 2;
                    mc.thePlayer.motionY = 0.399399995803833;
                    e.setY(0.399399995803833);
                    this.moveSpeed *= 2.149;
                } else if (stage == 2) {
                    this.stage = 3;
                    double difference = (mode.is("funcraft") ? 0.66 : 0.84) * (this.lastDist - MovementUtils.getBaseMoveSpeed());
                    this.moveSpeed = this.lastDist - difference;
                } else {
                    if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically) {
                        this.stage = 0;
                    }
                    this.moveSpeed = this.lastDist - this.lastDist / 159.0;
                    mc.thePlayer.motionY -= 0.0000099;
                }
            } else {
                mc.timer.timerSpeed = 1;
                this.stage = -1;
                this.lastDist = 0;
                this.moveSpeed = MovementUtils.getBaseMoveSpeed();
            }

            this.moveSpeed = Math.max(this.moveSpeed, MovementUtils.getBaseMoveSpeed());
            MovementUtils.setSpeed(e, this.moveSpeed);
        }
    }
}