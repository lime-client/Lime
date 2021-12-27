package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AntiBot extends Module {

    public AntiBot() {
        super("Anti Bot", Category.COMBAT);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Funcraft", "Funcraft", "Hypixel", "Mineplex", "NPC");
    private final BooleanProperty remove = new BooleanProperty("Remove", this, false);
    private final Map<Integer, Double> distanceMap = new HashMap<>();

    private final ArrayList<Integer> bots = new ArrayList<>();
    @Override
    public void onEnable() {
        bots.clear();
    }

    public boolean checkBot(EntityPlayer ent) {
        if(mode.is("funcraft")) {
            return !ent.getDisplayName().getUnformattedText().contains("§");
        }

        if(mode.is("mineplex")) {
            return bots.contains(ent.getEntityId());
        }

        if(mode.is("npc")) {
            return ent.getDisplayName().getUnformattedText().contains("NPC") || ent.getDisplayName().getUnformattedText().contains("CIT-");
        }
        return false;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());

        if(mode.is("hypixel")) {
            for(Entity entity : mc.theWorld.getLoadedEntityList()) {

                if(entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")) continue;

                if((entity instanceof EntityPlayer)) {
                    double distance = 0;
                    if(distanceMap.containsKey(entity.getEntityId())) {
                        distance = distanceMap.get(entity.getEntityId());
                    }
                    if(entity.getName().contains("\247") || entity.getDisplayName().getFormattedText().startsWith("ยง") || (distance > 14.5 && distance < 17)) {
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

            if(mode.is("mineplex")) {
                if(p.func_148944_c().size() < 3) {
                    bots.add(p.getEntityID());
                }
            }
        }
    }

    private boolean isOnTab(Entity entity){
        return mc.getNetHandler().getPlayerInfoMap().stream().anyMatch(info -> info.getGameProfile().getName().equals(entity.getName()));
    }
}
