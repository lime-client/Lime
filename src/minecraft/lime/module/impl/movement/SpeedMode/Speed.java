package lime.module.impl.movement.SpeedMode;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.impl.EventBoundingBox;
import lime.events.impl.EventMotion;
import lime.events.impl.EventMove;
import lime.events.impl.EventUpdate;
import net.minecraft.client.Minecraft;

public class Speed {
    protected Minecraft mc = Minecraft.getMinecraft();
    String name = "";
    public Speed(String name){
        this.name = name;
    }
    public void onMove(EventMove event){

    }
    public void onUpdate(EventUpdate e){

    }

    public void onDisable(){

    }

    public void onEnable(){

    }

    public void onMotion(EventMotion e){

    }

    public void onBB(EventBoundingBox e){

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Setting getSettingByName(String n){
        return Lime.setmgr.getSettingByNameAndMod(n, Lime.moduleManager.getModuleByName("Speed"));
    }
}
