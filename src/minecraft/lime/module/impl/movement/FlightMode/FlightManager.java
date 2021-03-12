package lime.module.impl.movement.FlightMode;

import lime.module.impl.movement.FlightMode.impl.*;

import java.util.ArrayList;

public class FlightManager {
    public ArrayList<Flight> flights = new ArrayList<>();
    public FlightManager(){
        flights.add(new BRWServ("BRWServ"));
        flights.add(new Funcraft("Funcraft"));
        flights.add(new Vanilla("Vanilla"));
        flights.add(new VanillaCreative("VanillaCreative"));
        flights.add(new Verus("Verus"));
        flights.add(new VerusFast("VerusFast"));
        flights.add(new Funcraft2("Funcraft2"));
    }
    public Flight getSpeedByName(String name){
        for(Flight fl : flights){
            if(fl.name.equals(name))
                return fl;
        }
        return null;
    }
}
