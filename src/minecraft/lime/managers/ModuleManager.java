package lime.managers;

import lime.module.impl.combat.*;
//import lime.module.impl.misc.*;
import lime.module.impl.misc.AntiBlindness;
import lime.module.impl.misc.Disabler;
import lime.module.impl.movement.*;
import lime.module.impl.player.*;
import lime.module.impl.render.*;
import lime.module.Module;

import java.util.ArrayList;

public class ModuleManager {
    ArrayList<Module> modules = new ArrayList<>();
    public ModuleManager(){
        // COMBAT
        modules.add(new KillAura());
        modules.add(new Velocity());
        modules.add(new InfiniteAura());
        modules.add(new AntiBot());
        modules.add(new AutoPot());
        modules.add(new Criticals());
        modules.add(new KillAuraNew());

        // MOVEMENT
        modules.add(new Flight());
        modules.add(new Speed());
        //modules.add(new Scaffold());
        modules.add(new Scaffold2());
        modules.add(new Scaffold3());
        modules.add(new SafeWalk());
        modules.add(new Sprint());
        modules.add(new NoSlow());
        modules.add(new Step());

        // PLAYER
        modules.add(new InvMove());
        modules.add(new ChestStealer());
        modules.add(new InvManager());
        modules.add(new ChestAura());
        modules.add(new AutoArmor());
        modules.add(new NoRotate());
        //modules.add(new Blink());
        modules.add(new Freecam());
        modules.add(new FastEat());
        modules.add(new NoFall());


        // RENDER
        modules.add(new ClickGUI());
        modules.add(new Tracers());
        modules.add(new HUD());
        modules.add(new NoFire());
        modules.add(new NoHurtCam());
        modules.add(new NoScoreboard());
        modules.add(new FullBright());
        modules.add(new ChestESP());
        modules.add(new SkeletonESP());
        modules.add(new Nametags());

        // MISC
        modules.add(new AntiBlindness());
        modules.add(new Disabler());




    }

    public ArrayList<Module> getModules() {
        return modules;
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
    public Module getModuleByClass(Class<? extends Module> classe){
        return modules.stream().filter(module -> module.getClass() == classe).findFirst().orElse(null);
    }
}
