package lime.module.impl.misc;

import lime.Lime;
import lime.settings.impl.BooleanValue;
import lime.settings.impl.SlideValue;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import lime.module.impl.combat.KillAura;
import lime.utils.other.OtherUtil;
import net.minecraft.block.BlockChest;

import java.util.Random;

public class Derp extends Module {
    public Derp(){
        super("Derp", 0, Category.MISC);
    }
    int yaw = 0;
    SlideValue speed = new SlideValue("Speed", this, 25, 1, 100, true);

    BooleanValue random = new BooleanValue("Random Yaw", this, false);
    @EventTarget
    public void onMotion(EventMotion e){
        try{
            KillAura killAura= (KillAura) Lime.moduleManager.getModuleByName("KillAura");
            if((!killAura.validEntity || !killAura.isToggled() || killAura.entity == null) && (!Lime.moduleManager.getModuleByName("Scaffold").isToggled() && !Lime.moduleManager.getModuleByName("Scaffold3").isToggled()) && !(mc.objectMouseOver.getBlockPos().getBlock() instanceof BlockChest)){
                if(yaw > 180) yaw = -180;
                if(yaw < -180) yaw = 180;
                yaw += speed.getIntValue();
                e.setYaw(random.getValue() ? random(-180, 180) : yaw);
                e.setPitch(new Random().nextBoolean() ? 90 : -90);
                OtherUtil.doRotationsInThirdPerson(e);
            }
        } catch (Exception ignored){}

    }

    public int random(int min, int max){
        return (int) (Math.random() * max - min);
    }


}
