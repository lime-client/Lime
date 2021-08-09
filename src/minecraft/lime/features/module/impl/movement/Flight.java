package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.features.module.impl.movement.flights.impl.*;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.EnumValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.movement.MovementUtils;
import lime.utils.render.Graph;

import java.util.ArrayList;

@ModuleData(name = "Flight", category = Category.MOVEMENT)
public class Flight extends Module {

    private final Graph speedGraph = new Graph("Speed");

    //Settings
    public final EnumValue mode = new EnumValue("Mode", this, "Vanilla", "Vanilla", "Funcraft", "Funcraft_Gamer", "Verus", "Verus_No_Damage", "Verus_Fast", "Survival_Dub", "Mineplex", "Astral");
    public final SlideValue speed = new SlideValue("Speed", this, 0.5, 10, 1.5, 0.5).onlyIf(mode.getSettingName(), "enum", "vanilla", "verus_fast");
    private final BoolValue bobbing = new BoolValue("Bobbing", this, true);
    public final BoolValue verusGlide = new BoolValue("Verus Glide", this, false).onlyIf(mode.getSettingName(), "enum", "verus_fast");

    private int ticks;

    private final ArrayList<FlightValue> flights = new ArrayList<>();

    public Flight()
    {
        this.flights.add(new FuncraftGamer());
        this.flights.add(new VerusFast());
        this.flights.add(new Funcraft());
        this.flights.add(new Vanilla());
        this.flights.add(new Astral());
        this.flights.add(new Verus());
        this.flights.add(new VerusNoDamage());
        this.flights.add(new SurvivalDub());
        this.flights.add(new Mineplex());
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public void init()
    {
        this.flights.forEach(flight -> flight.setFlight(this));
    }

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            this.toggle();
            return;
        }
        flights.stream().filter(flight -> flight.getName().equalsIgnoreCase(mode.getSelected())).findFirst().ifPresent(FlightValue::onEnable);
        ticks = 0;
    }

    @Override
    public void onDisable() {
        if(mc.thePlayer != null) {
            MovementUtils.setSpeed(0);
            mc.timer.timerSpeed = 1;
        }

        flights.stream().filter(flight -> flight.getName().equalsIgnoreCase(mode.getSelected())).findFirst().ifPresent(FlightValue::onDisable);
    }

    @EventTarget
    public void on2D(Event2D e) {
        speedGraph.drawGraph(3, 30, 200, 100);
        speedGraph.update((float) MovementUtils.getBPS());
    }

    @EventTarget
    public void onMove(EventMove e) {
        flights.stream().filter(flight -> flight.getName().equalsIgnoreCase(mode.getSelected())).findFirst().ifPresent(flightV -> flightV.onMove(e));
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        flights.stream().filter(flight -> flight.getName().equalsIgnoreCase(mode.getSelected())).findFirst().ifPresent(FlightValue::onUpdate);
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        this.setSuffix(mode.getSelected());
        if(bobbing.isEnabled() && mc.thePlayer.isMoving()) {
            mc.thePlayer.cameraYaw = 0.116f;
        }
        flights.stream().filter(flight -> flight.getName().equalsIgnoreCase(mode.getSelected())).findFirst().ifPresent(flight -> flight.onMotion(e));

        if(e.isPre())
            ticks++;
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        flights.stream().filter(flight -> flight.getName().equalsIgnoreCase(mode.getSelected())).findFirst().ifPresent(flightV -> flightV.onPacket(e));
    }

    @EventTarget
    public void onBoundingBox(EventBoundingBox e) {
        flights.stream().filter(flight -> flight.getName().equalsIgnoreCase(mode.getSelected())).findFirst().ifPresent(flightV -> flightV.onBoundingBox(e));
    }
}
