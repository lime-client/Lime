package lime.module.impl.player;

import lime.Lime;
import lime.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventPacket;
import lime.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {
    public NoFall(){
        super("NoFall", 0, Category.PLAYER);
        Lime.setmgr.rSetting(new Setting("Mode", this, "Vanilla", new String[]{"Vanilla", "SpoofGround", "NoGround", "Funcraft"}));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
    @EventTarget
    public void onMotion(EventMotion e){
        switch(getSettingByName("Mode").getValString().toLowerCase()){
            case "vanilla":
                if(mc.thePlayer.fallDistance >= 1.5){
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    mc.thePlayer.fallDistance = 0.00000000000000000000000001F;
                }
                break;
        }
    }
    @EventTarget
    public void onPacket(EventPacket e){
        switch(getSettingByName("Mode").getValString().toLowerCase()){
            case "noground":
                if(e.getPacketType() == EventPacket.PacketType.SEND){
                    if(e.getPacket() instanceof C03PacketPlayer){
                        C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) e.getPacket();
                        c03PacketPlayer.onGround = false;
                    }
                }
                break;
            case "spoofground":
                if(e.getPacketType() == EventPacket.PacketType.SEND){
                    if(e.getPacket() instanceof C03PacketPlayer){
                        C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) e.getPacket();
                        c03PacketPlayer.onGround = true;
                    }
                }
        }
    }

}
