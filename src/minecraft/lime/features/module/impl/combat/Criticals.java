package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventAttack;
import lime.core.events.impl.EventPacket;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.other.ChatUtils;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {

    public Criticals() {
        super("Criticals", Category.COMBAT);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Packet", "Packet", "Verus", "Visual");
    private final NumberProperty delay = new NumberProperty("Delay", this, 100, 1000, 750, 50);

    private final Timer timer = new Timer();
    private int packets = 0;

    private int groundTicks = 0;

    @Override
    public void onEnable() {
        packets = 0;
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        groundTicks = mc.thePlayer.onGround ? groundTicks + 1 : 0;
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        this.setSuffix(mode.getSelected());
        if(e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
            if(packet.getAction() == C02PacketUseEntity.Action.ATTACK && mc.thePlayer.onGround && groundTicks > 1 && timer.hasReached(delay.intValue()) && mode.is("packet")) {
                double[] offsets = {0.06252f, 0.0f};
                for (double offset : offsets) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset + (Math.random() * 0.0003F), mc.thePlayer.posZ, false));
                }
                mc.thePlayer.onCriticalHit(packet.getEntityFromWorld(mc.theWorld));
                timer.reset();
            } else if(mode.is("visual") && !mc.thePlayer.onGround && groundTicks > 1) {
                mc.thePlayer.onCriticalHit(packet.getEntityFromWorld(mc.theWorld));
            }
        }
        if(e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer p = (C03PacketPlayer) e.getPacket();
            if(!p.isMoving() && !p.getRotating() && packets > 0) {
                e.setCanceled(true);
                ChatUtils.sendMessage("removed sus");
                packets--;
            }
        }
    }

    @EventTarget
    public void onAttack(EventAttack e) {
        if(mode.is("verus")) {
            if(groundTicks > 2 && mc.thePlayer.onGround && e.getEntity() != null && timer.hasReached(delay.intValue()) && packets < 20) {
                double posY = mc.thePlayer.posY;

                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY += 0.41999998688697815, mc.thePlayer.posZ, false));
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY += 0.08307781780646906, mc.thePlayer.posZ, false));
                packets += 2;
                mc.thePlayer.onCriticalHit(e.getEntity());
                timer.reset();
            }
        }
    }
}
