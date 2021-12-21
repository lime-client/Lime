package lime.features.module.impl.movement.flights.impl;

import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.utils.movement.MovementUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class ZoneCraftFly extends FlightValue {
    public ZoneCraftFly() {
        super("ZoneCraft");
    }

    private int ticks;

    @Override
    public void onEnable() {
        for (int i = 0; i < 10; i++) {
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, false));
            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        ticks = 6;
    }

    @Override
    public void onDisable() {
        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        super.onDisable();
    }

    @Override
    public void onMove(EventMove e) {
        ticks++;
        e.setY(mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? .20 : mc.gameSettings.keyBindSneak.isKeyDown() ? -.41 : 0);
        if(mc.thePlayer.isMoving() && ticks > 5) {
            MovementUtils.setSpeed(e, 2);
        } else {
            MovementUtils.setSpeed(e, 0);
        }
    }

    @Override
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C03PacketPlayer) {
            mc.getNetHandler().sendPacketNoEvent(new C02PacketUseEntity(new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile()), C02PacketUseEntity.Action.ATTACK));
            mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition().down(), 0, new ItemStack(Blocks.stone, 1), 0.54345F,0.523234F, 0.5435345F));
            C03PacketPlayer p = (C03PacketPlayer) e.getPacket();
            p.onGround = true;
            e.setPacket(p);
        }

        if(e.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer.ticksExisted > 20) {
            S08PacketPlayerPosLook p = (S08PacketPlayerPosLook) e.getPacket();
            double x = p.getX() - mc.thePlayer.posX;
            double y = p.getY() - mc.thePlayer.posY;
            double z = p.getZ() - mc.thePlayer.posZ;
            if(Math.sqrt(x * x + y * y + z * z) < 50) {
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch(), false));
                e.setCanceled(true);
            }

        }
        super.onPacket(e);
    }
}
