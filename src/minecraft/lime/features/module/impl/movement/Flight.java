package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.movement.flights.FlightValue;
import lime.features.module.impl.movement.flights.impl.*;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.movement.MovementUtils;
import lime.utils.render.Graph;

import java.util.ArrayList;

public class Flight extends Module {

    private final Graph speedGraph = new Graph("Speed");

    //Settings
    public final EnumProperty mode = new EnumProperty("Mode", this, "Vanilla", "Vanilla", "ZoneCraft", "Hypixel", "Funcraft", "Funcraft2", "Verus", "Verus_No_Damage", "Verus Float", "Verus Fast", "Astral", "KoksCraft");
    public final EnumProperty damage = new EnumProperty("Damage", this, "Bypass", "Basic", "Bypass", "New").onlyIf(mode.getSettingName(), "enum", "Verus Fast");
    public final NumberProperty speed = new NumberProperty("Speed", this, 0.5, 10, 1.5, 0.5).onlyIf(mode.getSettingName(), "enum", "vanilla", "Verus Fast");
    public final NumberProperty vClip = new NumberProperty("V Clip", this, 1, 5, 2, 0.5).onlyIf(mode.getSettingName(), "enum", "Verus Fast");
    public final NumberProperty funcraftSpeed = new NumberProperty("Funcraft Speed", this, 0.2, 2, 1.6, 0.05).onlyIf(mode.getSettingName(), "enum", "funcraft");
    public final NumberProperty funcraftTimerSpeed = new NumberProperty("Funcraft Timer Speed", this, 1, 5, 1.75, 0.05).onlyIf(mode.getSettingName(), "enum", "funcraft");
    private final BooleanProperty bobbing = new BooleanProperty("Bobbing", this, true);
    public final BooleanProperty cancelPackets = new BooleanProperty("Cancel Packets", this, false).onlyIf(mode.getSettingName(), "enum", "Verus Fast");
    public final BooleanProperty timerBypass = new BooleanProperty("Timer Bypass", this, true).onlyIf(mode.getSettingName(), "enum", "Verus Fast");
    public final BooleanProperty verusHeavy = new BooleanProperty("Verus Heavy", this, false).onlyIf(mode.getSettingName(), "enum", "verus fast", "Verus Float");
    public final BooleanProperty latestVerus = new BooleanProperty("Latest Verus", this, false).onlyIf(mode.getSettingName(), "enum", "Verus Fast");

    private int ticks;

    private final ArrayList<FlightValue> flights = new ArrayList<>();

    public Flight()
    {
        super("Flight", Category.MOVE);

        this.flights.add(new Funcraft2Fly());
        this.flights.add(new FuncraftFly());
        this.flights.add(new VanillaFly());
        this.flights.add(new ZoneCraftFly());
        this.flights.add(new HypixelFly());
        this.flights.add(new CollisionFly());
        this.flights.add(new VerusFastFly());
        this.flights.add(new VerusNoDamageFly());
        this.flights.add(new VerusFloatFly());
        this.flights.add(new AstralFly());
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

        flights.stream().filter(flight -> flight.getName().equalsIgnoreCase(mode.getSelected())).findFirst().ifPresent(f -> f.on2D(e));
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
