package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import lime.utils.other.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;

@ModuleData(name = "Flight", category = Category.MOVEMENT)
public class Flight extends Module {

    private enum Mode {
        Vanilla, Funcraft, Funcraft_Gamer, Verus, Verus_Fast, Astral
    }

    //Settings
    private final EnumValue mode = new EnumValue("Mode", this, Mode.Vanilla);
    private final SlideValue speed = new SlideValue("Speed", this, 0.5, 10, 1.5, 0.5).onlyIf(mode.getSettingName(), "enum", "vanilla", "verus_fast");
    private final BoolValue bobbing = new BoolValue("Bobbing", this, true);
    private final BoolValue verusGlide = new BoolValue("Verus Glide", this, false).onlyIf(mode.getSettingName(), "enum", "verus_fast");

    private int ticks;
    private double moveSpeed;
    private boolean receivedVelocityPacket;
    private double lastDist;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        }
        if(mode.is("funcraft")) {
            if(mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                moveSpeed = 1.7;
            } else
                moveSpeed = 0.25;
        }
        if(mode.is("verus_fast")) {
            mc.thePlayer.jump();
            PlayerUtils.verusDamage();
        }
        lastDist = 0;
        ticks = 0;
        receivedVelocityPacket = false;
    }

    @Override
    public void onDisable() {
        MovementUtils.setSpeed(0);
        mc.timer.timerSpeed = 1;
    }

    @EventTarget
    public void on2D(Event2D e) {
        mc.fontRendererObj.drawStringWithShadow("BP/S: " + MovementUtils.getBPS(), 3, 3, -1);
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(mode.is("verus_fast") && !receivedVelocityPacket) {
            e.setX(0);
            e.setZ(0);
        }
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected().name());
        if(bobbing.isEnabled() && mc.thePlayer.isMoving()) {
            mc.thePlayer.cameraYaw = 0.116f;
        }
        if(mode.is("vanilla")) {
            mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.80 : mc.gameSettings.keyBindSneak.isKeyDown() ? -0.80 : 0;
            if(mc.thePlayer.isMoving()) {
                MovementUtils.setSpeed(speed.getCurrent());
            } else {
                MovementUtils.setSpeed(0);
            }
        }
        if(mode.is("verus")) {
            //mc.thePlayer.motionY = -0.0784000015258789;
        }

        if(mode.is("verus_fast")) {
            if(verusGlide.isEnabled() && receivedVelocityPacket && mc.thePlayer.motionY < 0) {
                mc.thePlayer.motionY = -0.0784000015258789;
            }
            if(ticks <=  24 && receivedVelocityPacket && e.isPre()) {
                MovementUtils.setSpeed(speed.getCurrent());
            } else if(ticks == 25) {
                MovementUtils.setSpeed(0);
            }
        }

        if(mode.is("funcraft")) {
            e.setGround(true);
            mc.thePlayer.jumpMovementFactor = 0;
            if(e.isPre()) {
                mc.thePlayer.motionY = 0;
                if(ticks > 175 && moveSpeed < 0.26) {
                    ticks = 0;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.03, mc.thePlayer.posZ);
                }
            }

            if(mc.thePlayer.isCollidedHorizontally)
                moveSpeed = 0.25;
            if(mc.thePlayer.isMoving()) {
                mc.timer.timerSpeed = 1.0866f;
                if(mc.thePlayer.ticksExisted % 5 == 0) {
                    mc.timer.timerSpeed = 1.75f;
                }
                MovementUtils.setSpeed(moveSpeed);
                if(moveSpeed > 0.25)
                    moveSpeed -= moveSpeed / 159;
                else
                    moveSpeed = 0.25;
            } else {
                MovementUtils.setSpeed(0);
                moveSpeed = 0.25;
            }
            if(e.isPre() && !MovementUtils.isOnGround(0.1)) {
                // 3.33315597345063e-11
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0000000000333315597345063, mc.thePlayer.posZ);
            }
        }

        if(mode.is("funcraft_gamer")) {
            mc.timer.timerSpeed = 1.5f;
            mc.thePlayer.setSprinting(false);
            mc.thePlayer.onGround = true;
            mc.thePlayer.motionY *= 0.3;
        }

        if(mode.is("astral") && e.isPre()) {
            if(mc.thePlayer.isMoving())
                mc.timer.timerSpeed = 1.5f;
            else
                mc.timer.timerSpeed = 1;
            if(mc.thePlayer.motionY < -0.20) {
                e.setGround(true);
                mc.thePlayer.motionY = 0.2;
                if(mc.thePlayer.isMoving()) {
                    MovementUtils.setSpeed(0.8);
                }
            } else if(mc.thePlayer.motionY > 0.1) {
                if(mc.thePlayer.isMoving()) {
                    MovementUtils.setSpeed(0.8);
                }
            }
        }

        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);

        if(e.isPre())
            ticks++;
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                receivedVelocityPacket = true;
            }
        }
    }

    @EventTarget
    public void onBoundingBox(EventBoundingBox e) {
        if(mode.is("verus") || (mode.is("verus_fast") && receivedVelocityPacket && !verusGlide.isEnabled())) {
            if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox())) {
                e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
            }
        }
    }
}
