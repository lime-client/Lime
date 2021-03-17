package lime.module.impl.movement.FlightMode.impl;

import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventMove;
import lime.events.impl.EventPacket;
import lime.module.Module;
import lime.module.impl.movement.FlightMode.Flight;
import lime.utils.movement.MovementUtil;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Funcraft3 extends Flight {
    public Funcraft3(String name) {
        super(name);
    }
    double funcraftspeed = 0f;

    @Override
    public void onEnable() {

        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        super.onDisable();
    }


    @EventTarget
    public void onMotion(EventMotion e) {
          if (e.getState() == EventMotion.State.PRE) {
              mc.timer.timerSpeed = 1.65f;
              if (mc.thePlayer.onGround) {
                  mc.thePlayer.jump();
                  funcraftspeed = 1.4;
              } else {
                  if (!MovementUtil.isMoving() || funcraftspeed < 0.25)
                      funcraftspeed = 0.25;

                  if (funcraftspeed == 0.25)
                      mc.thePlayer.motionY = -0.005;
                 else {
                      mc.thePlayer.motionY = 0;
                      mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3.33315597345063e-11, mc.thePlayer.posZ);
                  }

                  MovementUtil.setSpeed(funcraftspeed);
                  funcraftspeed -= 0.01;
                  mc.thePlayer.jumpMovementFactor = 0;
              }
          }

    }


    @EventTarget public void onPacket(EventPacket e){
        if (e.getPacket() instanceof S08PacketPlayerPosLook)
            funcraftspeed = 0.25f;
    }


}

