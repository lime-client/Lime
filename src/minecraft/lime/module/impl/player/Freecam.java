package lime.module.impl.player;

import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventPacket;
import lime.module.Module;
import lime.utils.movement.MovementUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class Freecam extends Module {
    public Freecam(){
        super("Freecam", 0, Category.PLAYER);
    }
    EntityOtherPlayerMP ent;

    @Override
    public void onEnable() {
        ent = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        ent.rotationYawHead = mc.thePlayer.rotationYawHead;
        ent.renderYawOffset = mc.thePlayer.renderYawOffset;
        ent.copyLocationAndAnglesFrom(mc.thePlayer);
        mc.theWorld.addEntityToWorld(-6969, ent);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setPosition(ent.posX, ent.posY, ent.posZ);
        mc.theWorld.removeEntity(ent);
        super.onDisable();
    }
    @EventTarget
    public void onPacket(EventPacket e){
        if(e.getPacketType() == EventPacket.PacketType.SEND){
            e.setCancelled(true);
        }
    }
    @EventTarget
    public void onMotion(EventMotion e){
        mc.thePlayer.motionY = 0;
        if(MovementUtil.isMoving()) MovementUtil.hClip(1);
        if(mc.gameSettings.keyBindJump.isKeyDown()) MovementUtil.vClip(0.5);
        if(mc.gameSettings.keyBindSneak.isKeyDown()) MovementUtil.vClip(-0.5);
    }
}
