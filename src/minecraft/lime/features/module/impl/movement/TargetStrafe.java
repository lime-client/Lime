package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventMove;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.combat.KillAura;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.combat.CombatUtils;
import lime.utils.movement.MovementUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

// From moon
@ModuleData(name = "Target Strafe", category = Category.MOVEMENT)
public class TargetStrafe extends Module {

    public final SlideValue distance = new SlideValue("Distance", this, 1, 5, 2.5, 0.1);
    private final BoolValue circle = new BoolValue("Circle", this, true);
    private final BoolValue onlySpeed = new BoolValue("Only Speed", this, false);

    public static int direction = 1;
    public static boolean canMove;
    public double moveSpeed;

    @Override
    public void onDisable() {
        canMove = false;
    }

    @EventTarget
    public void on3D(Event3D e) {
        if(KillAura.getEntity() == null || !(KillAura.getEntity() instanceof EntityLivingBase) || !canMove || !circle.isEnabled()) return;
        RenderUtils.drawRadius((EntityLivingBase) KillAura.getEntity(), new Color(0, 255, 0).getRGB());
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(Lime.getInstance().getModuleManager().getModuleC(KillAura.class).isToggled()) {
            if(KillAura.getEntity() == null) {
                return;
            }
            float rotations[] = CombatUtils.getEntityRotations((EntityLivingBase) KillAura.getEntity(), false);
            moveSpeed = MovementUtils.getBaseMoveSpeed();
            if(KillAura.getEntity().getDistanceToEntity(mc.thePlayer) < distance.getCurrent()) {
                if(mc.gameSettings.keyBindRight.isPressed()) {
                    direction = -1;
                }
                if(mc.gameSettings.keyBindLeft.isPressed()) {
                    direction = 1;
                }
                canMove = true;

                /*
                 * Speed Only Value
                 */

                if(Lime.getInstance().getModuleManager().getModuleC(Speed.class).isToggled()) {
                    if (mc.gameSettings.keyBindRight.isPressed()) {
                        direction = -1;
                    }
                    if (mc.gameSettings.keyBindLeft.isPressed()) {
                        direction = 1;
                    }
                    canMove = true;
                    if (direction == -1 || direction == 1) {
                        mc.gameSettings.keyBindForward.pressed = true;
                    }
                }

                /*
                 * Normal
                 */

                if(mc.gameSettings.keyBindRight.isPressed()) {
                    direction = -1;
                }
                if(mc.gameSettings.keyBindLeft.isPressed()) {
                    direction = 1;
                }

                canMove = true;

                if(canMove) {
                    MovementUtils.setSpeed(e, MovementUtils.getSpeed(), rotations[0], direction, 0.0D);
                }
            } else {
                if(canMove) {
                    MovementUtils.setSpeed(e, MovementUtils.getSpeed(), rotations[0], direction, 1.0D);
                }
                canMove = false;
            }
        } else {
            canMove = false;
        }
    }
}
