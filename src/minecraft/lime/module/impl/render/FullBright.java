package lime.module.impl.render;

import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Module {
    public FullBright(){
        super("FullBright", 0, Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
        super.onDisable();
    }
    @EventTarget
    public void onMotion(EventMotion e){
        mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 2000000, 1));
    }
}
