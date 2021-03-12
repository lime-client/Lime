package lime.module.impl.combat;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventPacket;
import lime.module.Module;
import lime.utils.Timer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {
    public Criticals(){
        super("Criticals", 0, Category.COMBAT);
        Lime.setmgr.rSetting(new Setting("Mode", this, "Basic", new String[]{"Basic"}));
    }
    private static Timer timer = new Timer();

    @Override
    public void onEnable(){
        super.onEnable();
    }
    @Override
    public void onDisable(){
        super.onDisable();
    }


    public void doCritPacket(){
        if(mc.thePlayer.onGround){
            if(mc.thePlayer.isCollidedVertically && timer.hasReached(500)){
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0627, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0627, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));

                timer.reset();

            }
        }
    }
    public void doCritSlient(){
        if(mc.thePlayer.onGround){
            if(mc.thePlayer.isCollidedVertically && timer.hasReached(500)){
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.00007, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                timer.reset();

            }
        }
    }
    public void doCritHop(){
        if(mc.thePlayer.onGround){
            if(mc.thePlayer.isCollidedVertically && timer.hasReached(45)){
                //mc.thePlayer.motionY = 0.195;
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.08, mc.thePlayer.posZ);
                timer.reset();
            }
        }
    }
    public void doCritJump(){
        if(mc.thePlayer.onGround){
            if(mc.thePlayer.isCollidedVertically && timer.hasReached(45)){
                //mc.thePlayer.motionY = 0.195;
                mc.thePlayer.jump();
                timer.reset();
            }
        }
    }




    @EventTarget
    public void onPacketSend(EventPacket e){
        if(e.getPacketType() == EventPacket.PacketType.SEND && ((e.getPacket() instanceof C02PacketUseEntity) && mc.thePlayer.onGround && ((C02PacketUseEntity) e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK)){
            switch (getSettingByName("Mode").getValString().toUpperCase()){
                case "BASIC":
                    doCritHop();
                    break;
            }
        }
    }
}
