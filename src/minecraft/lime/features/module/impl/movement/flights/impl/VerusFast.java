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
import net.minecraft.network.play.client.C03PacketPlayer;
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
        received = sent = false;
        Block block = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ).getBlock();
        sus = block instanceof BlockSlab || block instanceof BlockLeaves || block instanceof BlockAir;
        if(!sus) {
            processBar = new ProcessBar((sr.getScaledWidth() / 2) - 25, (sr.getScaledHeight() / 2) + 20, getFlight().timerBypass.isEnabled() ? getFlight().damage.is("basic") ? 200 : 1500 : 0);
            if(!getFlight().timerBypass.isEnabled()) {
                sent = true;
                if(getFlight().damage.is("basic")) {
                    damage();
                } else {
                    PlayerUtils.verusDamage(!getFlight().latestVerus.isEnabled());
                }
                MovementUtils.vClip(.42);
            }
        }
        ticks = 0;
    }

    public void damage() {
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.05, mc.thePlayer.posZ, false));
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.41999998688697815, mc.thePlayer.posZ, true));
    }

    @Override
    public void onMotion(EventMotion e) {
        boolean b = Lime.getInstance().getModuleManager().getModuleC(Disabler.class).isToggled() && ((Disabler) Lime.getInstance().getModuleManager().getModuleC(Disabler.class)).mode.is("Verus Transaction");
        int defTicks = b ? 180 : 15;
        if(!sus) {
            if(!processBar.getTimer().hasReached(getFlight().damage.is("basic") ? 200 : 1500) && getFlight().timerBypass.isEnabled()) {
                e.setCanceled(true);
                return;
            }
            if(!sent && mc.thePlayer.onGround && e.isPre()) {
                sent = true;
                if(getFlight().damage.is("basic")) {
                    damage();
                } else {
                    PlayerUtils.verusDamage(!getFlight().latestVerus.isEnabled());
                }
            }

            if(received && mc.thePlayer.isMoving() && e.isPre()) {
                if(mc.thePlayer.isCollidedHorizontally && ticks < defTicks) {
                    ticks = defTicks+1;
                    Lime.getInstance().getNotificationManager().addNotification("Disabled boost for safety.", Notification.Type.WARNING);
                }
                if(ticks == defTicks+1) {
                    MovementUtils.setSpeed(0);
                } else if(ticks < defTicks) {
                    MovementUtils.setSpeed(getFlight().speed.getCurrent());
                }

                if(!mc.gameSettings.keyBindJump.isKeyDown() || !b) {
                    if(defTicks > ticks && b) {
                        e.setGround(!getFlight().verusHeavy.isEnabled());
                        mc.thePlayer.motionY = 0;
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.prevPosY, mc.thePlayer.posZ);
                        if(getFlight().verusHeavy.isEnabled())
                            e.setY(mc.thePlayer.ticksExisted % 2 == 0 ? mc.thePlayer.posY : mc.thePlayer.posY + 0.01);
                    } else {
                        if(getFlight().verusHeavy.isEnabled()) {
                            mc.thePlayer.motionY = -0.0784000015258789;
                        } else {
                            e.setGround(true);
                            mc.thePlayer.motionY = 0;
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.prevPosY, mc.thePlayer.posZ);
                        }
                    }
                }

                ticks++;

                if(b) {
                    if(ticks <= 20) {
                        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ?mc.thePlayer.motionY + .42 : mc.thePlayer.motionY;
                    } else {
                        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? mc.thePlayer.ticksExisted % 2 == 0 ? 0.41999998688697815 : 0.33319999363422426 : mc.thePlayer.motionY;
                    }
                }
            }
        } else {
            if(getFlight().verusHeavy.isEnabled()) {
                mc.thePlayer.motionY = -0.0784000015258789;
            } else {
                e.setGround(true);
                mc.thePlayer.motionY = 0;
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.prevPosY, mc.thePlayer.posZ);
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
                MovementUtils.vClip(.25);
            }
        }
    }

    @Override
    public void on2D(Event2D e) {
        processBar.draw();
    }
}
