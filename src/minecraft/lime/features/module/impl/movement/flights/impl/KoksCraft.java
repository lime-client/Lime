package lime.features.module.impl.movement.flights.impl;

import lime.core.Lime;
import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.ui.notifications.Notification;
import lime.utils.movement.MovementUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.other.PlayerUtils;
import lime.utils.other.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public class KoksCraft extends FlightValue {
    public KoksCraft()
    {
        super("KoksCraft");
    }

    private final ArrayList<Packet<?>> packets = new ArrayList<>();
    private double posY;
    private int stage;

    private final Timer timer = new Timer();

    @Override
    public void onEnable() {
        stage = 0;
        timer.reset();
        PlayerUtils.koksCraftDamage();
    }

    @Override
    public void onDisable() {
        for (Packet<?> packet : packets) {
            mc.getNetHandler().sendPacketNoEvent(packet);
        }
        packets.clear();
    }

    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity) {
            stage = 1;
            posY = mc.thePlayer.posY;
        }
    }

    @Override
    public void onMove(EventMove e) {
        if(stage == 0) {
            MovementUtils.setSpeed(e, 0);
        }
    }

    @Override
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            if(stage == 1) {
                if(timer.hasReached(500)) {
                    mc.timer.timerSpeed = 1f;
                    for (Packet<?> packet : packets) {
                        mc.getNetHandler().sendPacketNoEvent(packet);
                    }
                    packets.clear();
                    timer.reset();
                } else {
                    e.setCanceled(true);
                    mc.timer.timerSpeed = 2.5f;
                    packets.add(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                }
                mc.thePlayer.motionY = 0;
                mc.thePlayer.setPosition(mc.thePlayer.posX, (int) posY, mc.thePlayer.posZ);
            }
        }
    }
}
