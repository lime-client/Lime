package lime.features.managers;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.*;
import lime.features.module.impl.exploit.Blink;
import lime.features.module.impl.exploit.FastBow;
import lime.features.module.impl.exploit.NoClip;
import lime.features.module.impl.movement.*;
import lime.features.module.impl.player.*;
import lime.features.module.impl.render.*;
import lime.features.module.impl.world.NoRotate;
import lime.features.module.impl.world.Scaffold;
import lime.features.module.impl.world.TimeChanger;
import lime.features.module.impl.world.Timer;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ModuleManager {
    private final ArrayList<Module> modules;

    public ModuleManager() {
        modules = new ArrayList<>();

        // COMBAT
        registerModule(new AutoGapple());
        registerModule(new AutoSword());
        registerModule(new Criticals());
        registerModule(new KillAura());
        registerModule(new Velocity());
        registerModule(new AntiBot());
        registerModule(new TPAura());

        // EXPLOIT
        registerModule(new FastBow());
        registerModule(new NoClip());
        registerModule(new Blink());

        // MOVEMENT
        registerModule(new TargetStrafe());
        registerModule(new CustomFlight());
        registerModule(new LongJump());
        registerModule(new NoSlow());
        registerModule(new Flight());
        registerModule(new Sprint());
        registerModule(new Speed());
        registerModule(new Glide());
        registerModule(new Step());

        // PLAYER
        registerModule(new InventoryManager());
        registerModule(new InventoryMove());
        registerModule(new ClickTeleport());
        registerModule(new ChestStealer());
        registerModule(new AutoArmor());
        registerModule(new SpeedMine());
        registerModule(new FastPlace());
        registerModule(new AutoTool());
        registerModule(new AntiVoid());
        registerModule(new Freecam());
        registerModule(new FastEat());
        registerModule(new NoFall());

        // RENDER
        registerModule(new RenderTestModule());
        registerModule(new NoScoreboard());
        registerModule(new Animations());
        registerModule(new FullBright());
        registerModule(new ClickGUI());
        registerModule(new Tracers());
        registerModule(new Camera());
        registerModule(new Chams());
        registerModule(new ESP());
        registerModule(new HUD());


        // WORLD
        registerModule(new TimeChanger());
        registerModule(new Scaffold());
        registerModule(new NoRotate());
        registerModule(new Timer());
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    private void registerModule(Module module) {
        this.modules.add(module);
    }

    public Module getModuleC(Class<? extends Module> clazz) {
        return getModules().stream().filter(module -> module.getClass() == clazz).findFirst().orElse(null);
    }

    public Module getModule(String name) {
        return getModules().stream().filter(module -> name.equalsIgnoreCase(module.getName())).findFirst().orElse(null);
    }


    public ArrayList<Module> getModulesFromCategory(Category category) {
        return getModules().stream().filter(module -> category == module.getCategory()).collect(Collectors.toCollection(ArrayList::new));
    }
}
