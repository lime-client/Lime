package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.movement.MovementUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleData(name = "Freecam", category = Category.PLAYER)
public class Freecam extends Module {
    private EntityOtherPlayerMP entity;

    @Override
    public void onEnable() {
        entity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        entity.rotationYawHead = mc.thePlayer.rotationYawHead;
        entity.renderYawOffset = mc.thePlayer.renderYawOffset;
        entity.copyLocationAndAnglesFrom(mc.thePlayer);
        mc.theWorld.addEntityToWorld(-6969, entity);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setPosition(entity.posX, entity.posY, entity.posZ);
        mc.theWorld.removeEntity(entity);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof C03PacketPlayer)
            e.setCanceled(true);
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        mc.thePlayer.motionY = 0;
        if(mc.thePlayer.isMoving())
            MovementUtils.hClip(1);
        if(mc.gameSettings.keyBindJump.isKeyDown()) MovementUtils.vClip(0.5);
        if(mc.gameSettings.keyBindSneak.isKeyDown()) MovementUtils.vClip(-0.5);
    }
}
