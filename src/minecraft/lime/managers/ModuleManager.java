package lime.managers;

import lime.Lime;
import lime.module.impl.combat.*;
//import lime.module.impl.misc.*;
import lime.module.impl.movement.*;
import lime.module.impl.player.*;
import lime.module.impl.render.*;
import lime.module.Module;

import java.util.ArrayList;
import java.util.Comparator;

public class ModuleManager {
    ArrayList<Module> modules = new ArrayList<>();
    public ArrayList<Module> filteredLengthModules = new ArrayList<>();
    public ModuleManager(){
        // COMBAT
        modules.add(new KillAura());
        modules.add(new Velocity());
        modules.add(new InfiniteAura());

        // MOVEMENT
        modules.add(new Flight());
        modules.add(new Speed());
        //modules.add(new Scaffold());
        modules.add(new Scaffold2());

        // PLAYER
        modules.add(new InvMove());
        modules.add(new ChestStealer());
        modules.add(new InvManager());
        modules.add(new ChestAura());
        modules.add(new AutoArmor());
        modules.add(new NoRotate());


        // RENDER
        modules.add(new ClickGUI());
        modules.add(new Tracers());
        modules.add(new HUD());
        modules.add(new HUD2());
        modules.add(new NoFire());
        modules.add(new NoHurtCam());
        modules.add(new NoScoreboard());
        modules.add(new FullBright());
        modules.add(new ChestESP());

        // MISC




        filteredLengthModules = modules;

    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public ArrayList<Module> getFilteredLengthModules() {
        return filteredLengthModules;
    }

    public Module getModuleByName(String name){
        for(Module mod : this.getModules()){
            if(mod.name.equalsIgnoreCase(name.toLowerCase()))
                return mod;
        }
        return null;
    }
    public ArrayList<Module> getModulesByCategory(Module.Category cat){
        ArrayList<Module> m = new ArrayList<>();
        for(Module mod : getModules()){
            if(mod.getCat() == cat)
                m.add(mod);
        }
        return m;
    }
}
