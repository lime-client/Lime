package lime.module.impl.movement;

import com.sun.javafx.geom.Vec3d;
import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.*;
import lime.module.Module;
import lime.module.impl.movement.FlightMode.FlightManager;
import lime.utils.ChatUtils;
import lime.utils.Timer;
import lime.utils.movement.MovementUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Flight extends Module {
    public FlightManager flightManager;
    public Flight(){
        super("Flight", Keyboard.KEY_F, Category.MOVEMENT);
        Lime.setmgr.rSetting(new Setting("Flight", this, "Vanilla", true, "Vanilla", "VanillaCreative", "BRWServ", "Funcraft", "Funcraft2", "Verus", "VerusFast", "ZoneCraft"));
        Lime.setmgr.rSetting(new Setting("Flight", this, 1, 0, 10, false));
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

        for(lime.module.impl.movement.FlightMode.Flight fl : flightManager.flights){
            if(fl.getName().toLowerCase().equals(getSettingByName("Flight").getValString().toLowerCase())){
                fl.onMotion(e);
                return;
            }
        }
    }

    @EventTarget
    public void flagCheck(EventPacket e){
        if(e.getPacket() instanceof S08PacketPlayerPosLook){
            ChatUtils.sendMsg("Disabled " + this.name + " for lagback reasons");
            this.toggle();
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
