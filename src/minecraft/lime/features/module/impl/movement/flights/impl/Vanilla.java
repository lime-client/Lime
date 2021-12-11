package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.exploit.Disabler;
import lime.features.module.impl.movement.TargetStrafe;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;

public class Vanilla extends FlightValue {
    public Vanilla()
    {
        super("Vanilla");
    }

    @Override
    public void onEnable() {
        if(((Disabler) Lime.getInstance().getModuleManager().getModuleC(Disabler.class)).mode.is("Watchdog 1.17") && Lime.getInstance().getModuleManager().getModuleC(Disabler.class).isToggled()) {
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer(true));
        }
        super.onEnable();
    }

    @Override
    public void onUpdate() {
        TargetStrafe targetStrafe = Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
        if(/*targetStrafe.stopJump.isEnabled() && targetStrafe.spaceOnly.isEnabled() && */KillAura.getEntity() != null && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
            mc.thePlayer.motionY = 0;
        } else {
            mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? .8 : mc.gameSettings.keyBindSneak.isKeyDown() ? -.8 : 0;
        }
        if(mc.thePlayer.isMoving()) {
            MovementUtils.setSpeed(getFlight().speed.getCurrent());
        } else {
            MovementUtils.setSpeed(0);
        }

        if(mc.thePlayer.ticksExisted % 25 == 0) {
            //handleVanillaKickBypass();
        }
    }

    private void handleVanillaKickBypass() {
        double y = posYGet();
        if (y != 0) {
            for(double posY = mc.thePlayer.posY; posY > y; posY -= 4) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));
                if (posY - 4 < y) {
                    break;
                }
            }
            y += .23;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));

            for(double posY = y; posY < mc.thePlayer.posY; posY += 4) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));
                if (posY + 4 > mc.thePlayer.posY) {
                    break;
                }
            }

            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }
    }

    private double posYGet() {
        AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox();
        for(double i = 0; i < mc.thePlayer.posY; i += .25) {
            AxisAlignedBB boundingBox2 = boundingBox.copy().offset(0, -i, 0);
            if (mc.theWorld.checkBlockCollision(boundingBox2)) {
                return mc.thePlayer.posY - i;
            }
        }
        return 0;
    }
}
