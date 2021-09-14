package lime.managers;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.*;
import lime.features.module.impl.exploit.*;
import lime.features.module.impl.movement.*;
import lime.features.module.impl.player.*;
import lime.features.module.impl.render.*;
import lime.features.module.impl.world.*;

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
        registerModule(new AutoPot());
        registerModule(new TeleportAura());

        // EXPLOIT
        registerModule(new InfiniteChat());
        registerModule(new Disabler());
        registerModule(new FastBow());
        registerModule(new Crasher());
        registerModule(new NoClip());
        registerModule(new Blink());
        registerModule(new Phase());

        // MOVEMENT
        registerModule(new TargetStrafe());
        registerModule(new CustomFlight());
        registerModule(new LongJump());
        registerModule(new NoSlow());
        registerModule(new Flight());
        ((Flight) this.getModuleC(Flight.class)).init();
        registerModule(new Sprint());
        registerModule(new Speed());
        registerModule(new Glide());
        registerModule(new Step());

        // PLAYER
        registerModule(new InventoryManager());
        registerModule(new LagbackChecker());
        registerModule(new InventoryMove());
        registerModule(new ClickTeleport());
        registerModule(new ChestStealer());
        registerModule(new ChatBypass());
        registerModule(new AutoArmor());
        registerModule(new SpeedMine());
        registerModule(new FastPlace());
        registerModule(new Streamer());
        registerModule(new AutoTool());
        registerModule(new KillSult());
        registerModule(new AntiVoid());
        registerModule(new Spammer());
        registerModule(new Freecam());
        registerModule(new FastEat());
        registerModule(new NoFall());
        registerModule(new Derp());

        // RENDER
        registerModule(new BlockOverlay());
        registerModule(new NoScoreboard());
        registerModule(new Projectiles());
        registerModule(new Animations());
        registerModule(new FullBright());
        registerModule(new PointerESP());
        registerModule(new Crosshair());
        registerModule(new DreamESP());
        registerModule(new Nametags());
        registerModule(new ChinaHat());
        registerModule(new ClickGUI());
        registerModule(new ChestESP());
        registerModule(new NoRender());
        registerModule(new Tracers());
        registerModule(new Camera());
        registerModule(new Chams());
        registerModule(new ESP());
        registerModule(new HUD());

        //registerModule(new ClickGUITest());

        // WORLD
        registerModule(new TimeChanger());
        registerModule(new Scaffold());
        registerModule(new NoRotate());
        registerModule(new Breaker());
        registerModule(new Eater());
        registerModule(new Timer());
        registerModule(new Eagle());
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
