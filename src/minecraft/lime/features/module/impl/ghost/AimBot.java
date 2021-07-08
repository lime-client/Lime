package lime.features.module.impl.ghost;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.combat.CombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(name = "Aim Bot", category = Category.GHOST)
public class AimBot extends Module {
    private final SlideValue range = new SlideValue("Range", this, 2.7, 6, 3.5, 0.1);
    private final BoolValue onlyIfClicking = new BoolValue("Only if clicking", this, true);
    private final BoolValue smooth = new BoolValue("Smooth", this, false);

    @EventTarget
    public void onMotion(EventMotion e) {
        if(e.isPre()) {
            if(!smooth.isEnabled()) {
                if(onlyIfClicking.isEnabled() && !mc.gameSettings.keyBindAttack.isKeyDown())
                    return;

                if(CombatUtils.raycastEntity(range.getCurrent(), new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch}) != null)
                    return;
                Entity entity = getNearestEntity();
                if(entity != null) {
                    float[] rots = CombatUtils.getEntityRotations((EntityLivingBase) entity, false);
                    mc.thePlayer.rotationYaw = rots[0];
                    mc.thePlayer.rotationPitch = rots[1];
                }
            }
        }
    }

    private Entity getNearestEntity() {
        ArrayList<Entity> entities = new ArrayList<>();

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(entity instanceof EntityPlayer && entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) <= range.getCurrent()) {
                entities.add(entity);
            }
        }

        if(entities.isEmpty()) return null;

        entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
        return entities.get(0);
    }
}
