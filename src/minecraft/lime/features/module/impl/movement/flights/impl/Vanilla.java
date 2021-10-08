package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.movement.TargetStrafe;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import org.lwjgl.input.Keyboard;

public class Vanilla extends FlightValue {
    public Vanilla()
    {
        super("Vanilla");
    }

    @Override
    public void onUpdate() {
        TargetStrafe targetStrafe = Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
        if(targetStrafe.stopJump.isEnabled() && targetStrafe.spaceOnly.isEnabled() && KillAura.getEntity() != null && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
            mc.thePlayer.motionY = 0;
        } else {
            mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? .80 : mc.gameSettings.keyBindSneak.isKeyDown() ? -.80 : 0;
        }
        if(mc.thePlayer.isMoving()) {
            MovementUtils.setSpeed(getFlight().speed.getCurrent());
        } else {
            MovementUtils.setSpeed(0);
        }
    }
}
