package lime.features.module.impl.movement.flights;

import lime.core.events.impl.EventBoundingBox;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.impl.movement.Flight;
import net.minecraft.client.Minecraft;

public class FlightValue
{
    private final String name;
    private Flight flight;
    protected final Minecraft mc = Minecraft.getMinecraft();

    public FlightValue(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public void onBoundingBox(EventBoundingBox e) {

    }

    public void onUpdate() {

    }

    public void onMotion(EventMotion e) {

    }

    public void onPacket(EventPacket e) {

    }

    public void onMove(EventMove e) {

    }
}
