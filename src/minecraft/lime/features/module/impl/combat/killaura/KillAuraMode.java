package lime.features.module.impl.combat.killaura;

import lime.core.events.impl.Event2D;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventMotion;
import lime.features.module.impl.combat.KillAura;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public abstract class KillAuraMode {
    protected final Minecraft mc = Minecraft.getMinecraft();
    public KillAura killAura;

    public KillAuraMode(KillAura killAura) {
        this.killAura = killAura;
    }

    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void on3D(Event3D e);
    public abstract void onMotion(EventMotion e);
    public abstract void on2D(Event2D e);
    public abstract EntityLivingBase getTargetedEntity();
}
