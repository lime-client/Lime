package lime.module.impl.player;

import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastEat extends Module {
    public FastEat(){
        super("FastEat", 0, Category.PLAYER);
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
        if(mc.thePlayer.ticksExisted % 3 == 0 && mc.thePlayer.isEating()){
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
        }
    }
}
