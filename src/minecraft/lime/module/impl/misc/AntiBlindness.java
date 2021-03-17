package lime.module.impl.misc;

import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import net.minecraft.potion.Potion;

public class AntiBlindness extends Module {

    public AntiBlindness(){
        super("AntiBlindness", 0, Category.MISC);
    }

    @EventTarget
    public void onMotion(EventMotion e){
        mc.thePlayer.removePotionEffectClient(Potion.blindness.id);
    }
}
