package lime.module.impl.movement;

import lime.settings.impl.BooleanValue;
import lime.settings.impl.ListValue;
import lime.settings.impl.SlideValue;
import lime.events.EventTarget;
import lime.events.impl.*;
import lime.module.Module;
import lime.module.impl.movement.FlightMode.FlightManager;
import lime.utils.ChatUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.lwjgl.input.Keyboard;

public class Flight extends Module {
    public FlightManager flightManager;

    public ListValue flightMode = new ListValue("Flight", this, "Vanilla", "Vanilla", "VanillaCreative", "BRWServ", "Funcraft", "Funcraft2","Funcraft3", "Verus", "VerusFast", "ZoneCraft");
    public SlideValue flightSpeed = new SlideValue("Flight", this, 1, 0, 10, false);
    public BooleanValue bobbing = new BooleanValue("Bobbing", this, false);
    public Flight(){
        super("Flight", Keyboard.KEY_F, Category.MOVEMENT);
        flightManager = new FlightManager();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for(lime.module.impl.movement.FlightMode.Flight fl : flightManager.flights){
            if(fl.getName().toLowerCase().equals(getSettingByName("Flight").getValString().toLowerCase())){
                fl.onEnable();
                return;
            }
        }

    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        for(lime.module.impl.movement.FlightMode.Flight fl : flightManager.flights){
            if(fl.getName().toLowerCase().equals(getSettingByName("Flight").getValString().toLowerCase())){
                fl.onDisable();
                return;
            }
        }

    }
    @EventTarget
    public void onMotion(EventMotion e){
        if(bobbing.getValue()) {
            mc.thePlayer.cameraYaw = 0.105f;
        }
        setSuffix(getSettingByName("Flight").getValString());
        for(lime.module.impl.movement.FlightMode.Flight fl : flightManager.flights){
            if(fl.getName().toLowerCase().equals(getSettingByName("Flight").getValString().toLowerCase())){
                fl.onMotion(e);
                return;
            }
        }
    }

    @EventTarget
    public void flagCheck(EventPacket e){
        if(e.getPacket() instanceof S08PacketPlayerPosLook && !getSettingByName("Flight").getValString().equalsIgnoreCase("Funcraft2")){
            ChatUtils.sendMsg("Disabled " + this.name + " for lagback reasons");
            this.toggle();
        }
    }
    @EventTarget
    public void packetCall(EventPacket e){
        for(lime.module.impl.movement.FlightMode.Flight fl : flightManager.flights){
            if(fl.getName().toLowerCase().equals(getSettingByName("Flight").getValString().toLowerCase())){
                fl.onPacket(e);
                return;
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate eu){
        for(lime.module.impl.movement.FlightMode.Flight fl : flightManager.flights){
            if(fl.getName().toLowerCase().equals(getSettingByName("Flight").getValString().toLowerCase())){
                fl.onUpdate(eu);
                return;
            }
        }
    }
    @EventTarget
    public void onMove(EventMove event){

        for(lime.module.impl.movement.FlightMode.Flight fl : flightManager.flights){
            if(fl.getName().toLowerCase().equals(getSettingByName("Flight").getValString().toLowerCase())){
                fl.onMove(event);
                return;
            }
        }
    }

    @EventTarget
    public void onBB(EventBoundingBox ebb){
        for(lime.module.impl.movement.FlightMode.Flight fl : flightManager.flights){
            if(fl.getName().toLowerCase().equals(getSettingByName("Flight").getValString().toLowerCase())){
                fl.onBB(ebb);
                return;
            }
        }
    }
}
