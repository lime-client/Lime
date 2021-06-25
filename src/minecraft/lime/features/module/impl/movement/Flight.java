package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

@ModuleData(name = "Flight", category = Category.MOVEMENT)
public class Flight extends Module {

    private enum Mode {
        VANILLA, FUNCRAFT, VERUS
    }

    //Settings
    private final EnumValue mode = new EnumValue("Mode", this, Mode.VANILLA);

    private double moveSpeed;

    @Override
    public void onEnable() {
        if(mode.is("funcraft")) {
            if(mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                moveSpeed = 1.875;
            } else
                moveSpeed = 0.25;
        }
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
    public void onMotion(EventMotion e) {
        if(mode.is("vanilla")) {
            mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.80 : mc.gameSettings.keyBindSneak.isKeyDown() ? -0.80 : 0;
            //MovementUtils.setSpeed(1.5);
        }
        if(mode.is("verus") && mc.gameSettings.keyBindJump.isKeyDown() && e.isPre()) {
            mc.thePlayer.motionY = -0.0784000015258789;
        }
        if(mode.is("funcraft")) {
            e.setGround(true);
            mc.thePlayer.jumpMovementFactor = 0;
            mc.thePlayer.motionY = 0;
            if(mc.thePlayer.isMoving()) {
                mc.timer.timerSpeed = 1.0866f;
                if(mc.thePlayer.ticksExisted % 5 == 0) {
                    mc.timer.timerSpeed = 1.4f;
                }
                MovementUtils.setSpeed(moveSpeed);
                if(moveSpeed > 0.25)
                    moveSpeed -= moveSpeed / 159;
            } else {
                MovementUtils.setSpeed(0);
                moveSpeed = 0.25;
            }
            if(e.isPre()) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3.33315597345063e-11, mc.thePlayer.posZ);
            }
        }
    }

    @EventTarget
    public void onBoundingBox(EventBoundingBox e) {
        if(mode.is("verus")) {
            if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox()))
                e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
        }
    }
}
