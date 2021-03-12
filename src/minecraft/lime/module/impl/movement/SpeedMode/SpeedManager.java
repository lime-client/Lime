package lime.module.impl.movement.SpeedMode;

import lime.module.impl.movement.SpeedMode.impl.*;

import java.util.ArrayList;

public class SpeedManager {
    public ArrayList<Speed> speeds = new ArrayList<>();
    public SpeedManager(){
        speeds.add(new Funcraft("Funcraft"));
        speeds.add(new Verus("Verus"));
        speeds.add(new BRWServ("BRWServ"));
        speeds.add(new Vanilla("Vanilla"));
        speeds.add(new FuncraftYPort("FuncraftYPort"));
    }
    public Speed getSpeedByName(String name){
        for(Speed sp : speeds){
            if(sp.name.equals(name))
                return sp;
        }
        return null;
    }
}
