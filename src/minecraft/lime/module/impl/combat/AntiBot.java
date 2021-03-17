package lime.module.impl.combat;

import lime.Lime;
import lime.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventUpdate;
import lime.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {
    public AntiBot(){
        super("AntiBot", 0, Category.COMBAT);
        Lime.setmgr.rSetting(new Setting("Mode", this, "Invisible", new String[]{"Invisible", "Ticks Existed"}));
        Lime.setmgr.rSetting(new Setting("Ticks Existed", this, 30, 0, 300, true));
    }

    public void removeBot() {
        switch(getSettingByName("Mode").getValString()){
            case "Invisible":
                for (Object entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityPlayer) {
                        if (((Entity)entity).isInvisible()) {
                            mc.theWorld.removeEntity(((EntityPlayer) entity));
                        }
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate e){
        setSuffix(getSettingByName("Mode").getValString());
    }


    public boolean skipEntity(Entity ent) {
        return ent.ticksExisted < getSettingByName("Ticks Existed").getValDouble() && getSettingByName("Mode").getValString().equalsIgnoreCase("Ticks Existed");
    }
}
