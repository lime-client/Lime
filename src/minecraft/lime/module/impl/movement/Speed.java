package lime.module.impl.movement;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.*;
import lime.module.Module;
import lime.module.impl.movement.SpeedMode.SpeedManager;
import lime.utils.ChatUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {
    public SpeedManager speedManager;
    public Speed(){
        super("Speed", Keyboard.KEY_V, Category.MOVEMENT);
        Lime.setmgr.rSetting(new Setting("Speed", this, "Vanilla", true, "Vanilla", "BRWServ", "Funcraft", "FuncraftYPort", "Verus"));
        Lime.setmgr.rSetting(new Setting("Speed Power", this, 1, 0, 10, false));
        speedManager = new SpeedManager();
    }
    @Override
    public void onEnable() {
        super.onEnable();
        for(lime.module.impl.movement.SpeedMode.Speed sp : speedManager.speeds){
            if(sp.getName().toLowerCase().equals(getSettingByName("Speed").getValString().toLowerCase())){
                sp.onEnable();
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

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0f;
        for(lime.module.impl.movement.SpeedMode.Speed sp : speedManager.speeds){
            if(sp.getName().toLowerCase().equals(getSettingByName("Speed").getValString().toLowerCase())){
                sp.onDisable();
                return;
            }
        }

    }
    @EventTarget
    public void onMotion(EventMotion e){
        for(lime.module.impl.movement.SpeedMode.Speed sp : speedManager.speeds){
            if(sp.getName().toLowerCase().equals(getSettingByName("Speed").getValString().toLowerCase())){
                sp.onMotion(e);
                return;
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate eu){
        for(lime.module.impl.movement.SpeedMode.Speed sp : speedManager.speeds){
            if(sp.getName().toLowerCase().equals(getSettingByName("Speed").getValString().toLowerCase())){
                sp.onUpdate(eu);
                return;
            }
        }
    }
    @EventTarget
    public void onMove(EventMove event){

        for(lime.module.impl.movement.SpeedMode.Speed sp : speedManager.speeds){
            if(sp.getName().toLowerCase().equals(getSettingByName("Speed").getValString().toLowerCase())){
                sp.onMove(event);
                return;
            }
        }
    }

    @EventTarget
    public void onBB(EventBoundingBox ebb){
        for(lime.module.impl.movement.SpeedMode.Speed sp : speedManager.speeds){
            if(sp.getName().toLowerCase().equals(getSettingByName("Speed").getValString().toLowerCase())){
                sp.onBB(ebb);
                return;
            }
        }
    }

}
