package lime.utils.other;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketSleepThread extends Thread {
    private final Packet<?> packet;
    private final long sleep;
    public PacketSleepThread(Packet<?> packet, long sleep) {
        this.packet = packet;
        this.sleep = sleep;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleep);
            Minecraft.getMinecraft().getNetHandler().sendPacketNoEvent(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.run();
    }
}
