package lime.module.impl.movement.FlightMode;

import lime.Lime;
import lime.settings.Setting;
import lime.events.impl.*;
import net.minecraft.client.Minecraft;

public class Flight {
    protected Minecraft mc = Minecraft.getMinecraft();
    String name = "";
    public Flight(String name){
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
    public void onPacket(EventPacket e){

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Setting getSettingByName(String n){
        return Lime.setmgr.getSettingByNameAndMod(n, Lime.moduleManager.getModuleByName("Flight"));
    }
}
