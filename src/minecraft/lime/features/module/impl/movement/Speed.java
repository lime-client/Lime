package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.world.Scaffold;
import lime.features.setting.impl.EnumValue;
import lime.utils.movement.HopFriction;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;

@ModuleData(name = "Speed", category = Category.MOVEMENT)
public class Speed extends Module {

    private final EnumValue mode = new EnumValue("Mode", this, "Vanilla", "Vanilla", "Vanilla_BHOP", "Verus", "Verus_LOWHOP", "Strafe", "NCP", "Funcraft", "Funcraft_YPORT", "Mineplex", "Mineplex2");

    private double currentDistance, moveSpeed, lastDist;
    private int stage;
    private boolean prevOnGround;

    private final HopFriction hopFriction = new HopFriction();

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        }
        this.moveSpeed = MovementUtils.getBaseMoveSpeed();
        stage = 0;
    }

    @Override
    public void onDisable() {
        this.hopFriction.reset();
        mc.timer.timerSpeed = 1;
        if(mc.thePlayer != null)
            mc.thePlayer.speedInAir = 0.02f;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(mode.is("vanilla") || mode.is("vanilla_bhop")) {
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(1);
                if(mode.is("vanilla_bhop")) {
                    if(mc.thePlayer.isMoving() && mc.thePlayer.onGround)
                        mc.thePlayer.motionY = 0.42;
                    if(mc.thePlayer.isMoving()) {
                        MovementUtils.setSpeed(0.6);
                    }
                    if(mc.thePlayer.ticksExisted % 3 == 0) mc.thePlayer.motionY -= 0.04;
                }
            }
        }

        if(mode.is("strafe")) {
            MovementUtils.strafe();
            if(mc.thePlayer.onGround)
                mc.thePlayer.jump();
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
        if(mode.is("mineplex")) {
            if(mc.thePlayer.onGround) {
                e.setY(mc.thePlayer.motionY = 0.4);
                currentDistance = moveSpeed;
                prevOnGround = true;
                moveSpeed = 0;
            } else {
                if(prevOnGround) {
                    moveSpeed = currentDistance + 0.56;
                    prevOnGround = false;
                } else {
                    moveSpeed = lastDist * (0.985);
                }
            }

            double max = 0.8;
            if (KillAura.getEntity() != null) {
                TargetStrafe targetStrafe2 = (TargetStrafe) Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
                targetStrafe2.setMoveSpeed(e, Math.max(Math.min(moveSpeed, max), prevOnGround ? 0 : 0.42));
            } else {
                MovementUtils.setSpeed(e, Math.max(Math.min(moveSpeed, max), prevOnGround ? 0 : 0.42));
            }
        }

        if(mode.is("mineplex2")) {
            if(!mc.thePlayer.isMoving()) {
                moveSpeed = MovementUtils.getBaseMoveSpeed();
            }
            if(mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                prevOnGround = true;
                e.setY(mc.thePlayer.motionY = 0.42);
                MovementUtils.setSpeed(e, -0.07);
                moveSpeed += 0.35;
            } else {
                if(mc.thePlayer.isCollidedHorizontally) moveSpeed = MovementUtils.getBaseMoveSpeed();
                if (KillAura.getEntity() != null) {
                    TargetStrafe targetStrafe2 = (TargetStrafe) Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
                    targetStrafe2.setMoveSpeed(e, mc.thePlayer.isMoving() ? Math.max(Math.min(moveSpeed *= 0.98, 1), MovementUtils.getBaseMoveSpeed()) : 0);
                } else {
                    MovementUtils.setSpeed(e, mc.thePlayer.isMoving() ? Math.max(Math.min(moveSpeed *= 0.98, 1), MovementUtils.getBaseMoveSpeed()) : 0);
                }
            }
        }

        if(mode.is("funcraft") || mode.is("funcraft_yport") || mode.is("ncp")) {
            mc.timer.timerSpeed = 1.0866f;
            mc.thePlayer.motionX *= 1.0199999809265137;
            mc.thePlayer.motionZ *= 1.0199999809265137;
            if(mc.thePlayer.ticksExisted % 5 == 0 && (mode.is("funcraft") || mode.is("funcraft_yport"))) {
                mc.timer.timerSpeed = 1.75f;
            }
            this.hopFriction.updateFriction(e, !mode.is("funcraft_yport"),0.399399995803833, 2.14999, mode.is("funcraft") || mode.is("funcraft_yport") ? Lime.getInstance().getModuleManager().getModuleC(Scaffold.class).isToggled() ? .80 : .66 : .84, 159, lastDist);
        }
    }
}
