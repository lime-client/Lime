package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleData(name = "Full Bright", category = Category.RENDER)
public class FullBright extends Module {
    @Override
    public void onDisable() {
        mc.thePlayer.removePotionEffectClient(Potion.nightVision.getId());
    }

    @EventTarget
    public void onUpdate(EventUpdate e)
    {
        mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 32767));
    }
}
