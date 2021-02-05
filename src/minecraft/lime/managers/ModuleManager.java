package lime.managers;

import lime.Lime;
import lime.module.impl.combat.InfiniteAura;
import lime.module.impl.combat.KillAura;
import lime.module.impl.combat.Velocity;
import lime.module.impl.misc.NoServAsync;
import lime.module.impl.movement.Flight;
import lime.module.impl.movement.Scaffold;
import lime.module.impl.movement.Speed;
import lime.module.impl.player.AutoArmor;
import lime.module.impl.player.ChestStealer;
import lime.module.impl.player.InvManager;
import lime.module.impl.player.InvMove;
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
        modules.add(new Scaffold());

        // PLAYER
        modules.add(new InvMove());
        modules.add(new ChestStealer());
        modules.add(new InvManager());
        modules.add(new AutoArmor());


        // RENDER
        modules.add(new ClickGUI());
        modules.add(new Tracers());
        modules.add(new HUD());

        // MISC
        modules.add(new NoServAsync());



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
