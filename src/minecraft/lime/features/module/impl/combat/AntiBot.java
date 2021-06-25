package lime.features.module.impl.combat;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleData(name = "Anti Bot", category = Category.COMBAT)
public class AntiBot extends Module {
    @EventTarget
    public void onMotion(EventMotion e) {
        for (Entity ent : mc.theWorld.getLoadedEntityList()) {
            if(ent instanceof AbstractClientPlayer && ent != mc.thePlayer && ent.ticksExisted < 30) {
                if((((AbstractClientPlayer) ent).getLocationSkin().getResourcePath().equalsIgnoreCase("textures/entity/alex.png") || ((AbstractClientPlayer) ent).getLocationSkin().getResourcePath().equalsIgnoreCase("textures/entity/steve.png")) && ent.ticksExisted < 30) {
                    AbstractClientPlayer player = (AbstractClientPlayer) ent;
                    if(mc.thePlayer.getDistanceToEntity(player) <= 5) {
                        mc.theWorld.removeEntity(player);
                    }
                }
            }
        }
    }
}
