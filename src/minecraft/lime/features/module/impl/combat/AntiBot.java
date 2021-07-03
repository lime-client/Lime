package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.EnumValue;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ModuleData(name = "Anti Bot", category = Category.COMBAT)
public class AntiBot extends Module {

    private enum Mode { FUNCRAFT, HYPIXEL }

    private final EnumValue mode = new EnumValue("Mode", this, Mode.FUNCRAFT);
    private final Map<Integer, Double> distanceMap = new HashMap<>();

    @EventTarget
    public void onMotion(EventMotion e) {
        if(mode.is("funcraft")) {
            for (Entity ent : mc.theWorld.getLoadedEntityList()) {
                if(ent instanceof AbstractClientPlayer && ent != mc.thePlayer && ent.ticksExisted < 30) {
                    if((((AbstractClientPlayer) ent).getLocationSkin().getResourcePath().equalsIgnoreCase("textures/entity/alex.png") || ((AbstractClientPlayer) ent).getLocationSkin().getResourcePath().equalsIgnoreCase("textures/entity/steve.png")) && ent.ticksExisted < 30) {
                        AbstractClientPlayer player = (AbstractClientPlayer) ent;
                        if(mc.thePlayer.getDistanceToEntity(player) <= 5 && player.motionY == 0 && !player.onGround && player.rotationYaw != -180 && player.rotationPitch != -8.4375) {
                            mc.theWorld.removeEntity(player);
                        }
                    }
                }
            }
        }

        if(mode.is("hypixel")) {
            for(Entity entity : mc.theWorld.getLoadedEntityList()) {

                if(entity.getName().contains("npc")) continue;

                if((entity instanceof EntityPlayer)) {
                    double distance = 0;
                    if(distanceMap.containsKey(entity.getEntityId())) {
                        distance = distanceMap.get(entity.getEntityId());
                    }
                    if(entity.getName().contains("\247") || entity.getDisplayName().getFormattedText().startsWith("ยง") || (distance > 14.5 && distance < 17)) {
                        mc.theWorld.removeEntity(entity);
                    }

                    if(!isOnTab(entity) && mc.thePlayer.ticksExisted > 100 && entity.ticksExisted > 5 && entity.ticksExisted < 200) {
                        mc.theWorld.removeEntity(entity);
                    }
                }

                if(entity.isInvisible()) {
                    if(!isOnTab(entity) && mc.thePlayer.ticksExisted > 100 && entity.ticksExisted > 5)
                        mc.theWorld.removeEntity(entity);
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S0CPacketSpawnPlayer) {
            S0CPacketSpawnPlayer p = (S0CPacketSpawnPlayer) e.getPacket();
            distanceMap.put(p.getEntityID(), mc.thePlayer.getDistance(p.getX(), p.getY(), p.getZ()));
        }
    }

    private boolean isOnTab(Entity entity){
        return mc.getNetHandler().getPlayerInfoMap()
                .stream()
                .anyMatch(info -> info.getGameProfile().getName().equals(entity.getName()));
    }
}
