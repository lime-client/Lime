package lime.module.impl.movement.SpeedMode;

import lime.module.impl.movement.SpeedMode.impl.BRWServ;
import lime.module.impl.movement.SpeedMode.impl.Funcraft;
import lime.module.impl.movement.SpeedMode.impl.Vanilla;
import lime.module.impl.movement.SpeedMode.impl.Verus;

import java.util.ArrayList;

public class SpeedManager {
    public ArrayList<Speed> speeds = new ArrayList<>();
    public SpeedManager(){
        speeds.add(new Funcraft("Funcraft"));
        speeds.add(new Verus("Verus"));
        speeds.add(new BRWServ("BRWServ"));
        speeds.add(new Vanilla("Vanilla"));
    }
    public Speed getSpeedByName(String name){
        for(Speed sp : speeds){
            if(sp.name.equals(name))
                return sp;
        }
        return null;
    }
}
