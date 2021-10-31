package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.core.events.impl.*;
import lime.features.module.impl.exploit.Disabler;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.ui.gui.ProcessBar;
import lime.ui.notifications.Notification;
import lime.utils.movement.MovementUtils;
import lime.utils.other.ChatUtils;
import lime.utils.other.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSlab;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class VerusFast extends FlightValue {
    public VerusFast() {
        super("Verus Fast");
    }

    private ProcessBar processBar;
    private int ticks;
    private boolean received, sent, sus;

    @Override
    public void onEnable() {
        ScaledResolution sr = new ScaledResolution(mc);
        processBar = new ProcessBar((sr.getScaledWidth() / 2) - 25, (sr.getScaledHeight() / 2) + 20, getFlight().timerBypass.isEnabled() ? 1500 : 0);
        received = sent = false;
        Block block = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ).getBlock();
        System.out.println(block.getClass());
        sus = block instanceof BlockSlab || block instanceof BlockLeaves || block instanceof BlockAir;
        ticks = 0;

        if(!getFlight().timerBypass.isEnabled()) {
            sent = true;
            PlayerUtils.verusDamage();
            MovementUtils.vClip(.42);
        }
    }

    @Override
    public void onMotion(EventMotion e) {
        if(!sus) {
            if(!processBar.getTimer().hasReached(1500) && getFlight().timerBypass.isEnabled()) {
                e.setCanceled(true);
                return;
            }
            if(!sent && mc.thePlayer.onGround && e.isPre()) {
                sent = true;
                PlayerUtils.verusDamage();
            }

            if(received && mc.thePlayer.isMoving() && e.isPre()) {
                if(mc.thePlayer.isCollidedHorizontally && ticks < 20) {
                    ticks = 21;
                    Lime.getInstance().getNotificationManager().addNotification("Disabled boost for safety.", Notification.Type.WARNING);
                }
                if(ticks == 21) {
                    MovementUtils.setSpeed(0);
                } else if(ticks < 21) {
                    MovementUtils.setSpeed(getFlight().speed.getCurrent());
                }
                ticks++;

                boolean b = Lime.getInstance().getModuleManager().getModuleC(Disabler.class).isToggled() && ((Disabler) Lime.getInstance().getModuleManager().getModuleC(Disabler.class)).mode.is("Verus Transaction");

                if(getFlight().verusHeavy.isEnabled() && !((ticks <= 20 || b) && mc.gameSettings.keyBindJump.isKeyDown())) {
                    mc.thePlayer.motionY = -0.0784000015258789;
                }

                if(b) {
                    if(ticks <= 20) {
                        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ?mc.thePlayer.motionY + .42 : getFlight().verusHeavy.isEnabled() ? -0.0784000015258789 : mc.thePlayer.motionY;
                    } else {
                        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? mc.thePlayer.ticksExisted % 2 == 0 ? 0.41999998688697815 : 0.33319999363422426 : getFlight().verusHeavy.isEnabled() ? -0.0784000015258789 : mc.thePlayer.motionY;
                    }
                }
            }
        } else {
            if(getFlight().verusHeavy.isEnabled()) {
                mc.thePlayer.motionY = -0.0784000015258789;
            }
        }
    }

    @Override
    public void onMove(EventMove e) {
        if(!received && !sus) {
            e.setZ(0);
            if(sent) {
                e.setY(0);
            }
            e.setX(0);
        }
    }

    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity p = (S12PacketEntityVelocity) e.getPacket();
            if(p.getEntityID() == mc.thePlayer.getEntityId() && !sus) {
                received = true;
                MovementUtils.vClip(getFlight().vClip.getCurrent());
            }
        }
    }

    @Override
    public void onBoundingBox(EventBoundingBox e) {
        if((received || sus) && !getFlight().verusHeavy.isEnabled()) {
            if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox())) {
                e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
            }
        }
    }

    @Override
    public void on2D(Event2D e) {
        processBar.draw();
    }
}
