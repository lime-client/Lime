package lime.module.impl.movement;

import com.sun.javafx.geom.Vec3d;
import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventBoundingBox;
import lime.events.impl.EventMotion;
import lime.events.impl.EventUpdate;
import lime.module.Module;
import lime.utils.Timer;
import lime.utils.movement.MovementUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Flight extends Module {
    public Flight(){
        super("Flight", Keyboard.KEY_F, Category.MOVEMENT);
        Lime.setmgr.rSetting(new Setting("Flight", this, "Vanilla", true, "Vanilla", "VanillaCreative", "BRWServ", "Funcraft", "Verus", "Verus-Fast", "ZoneCraft"));
        Lime.setmgr.rSetting(new Setting("Speed", this, 1, 0, 10, false));
    }
    private double lastDist, speed; int stage;
    private int state = 0;
    private double oof;
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        stage = 0;
        mc.timer.timerSpeed = 1f;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        if(Lime.setmgr.getSettingByName("Flight").getValString().equalsIgnoreCase("VanillaCreative")){
            mc.thePlayer.capabilities.isFlying = false;
            if(!mc.thePlayer.capabilities.isCreativeMode){
                mc.thePlayer.capabilities.allowFlying = false;
            } else {
                mc.thePlayer.capabilities.allowFlying = true;
            }
        }
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eU){
        switch(Lime.setmgr.getSettingByName("Flight").getValString()) {
            case "Vanilla":
                mc.thePlayer.motionY = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown())
                    mc.thePlayer.motionY = 0.1;
                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = -0.1;
                break;
            case "VanillaCreative":
                mc.thePlayer.capabilities.isFlying = true;
                mc.thePlayer.capabilities.allowFlying = true;
                break;
            case "BRWServ":
                if (MovementUtil.isMoving())
                    MovementUtil.setSpeed(Lime.setmgr.getSettingByNameAndMod("Speed", this).getValDouble());
                break;
            case "Funcraft":
                if (mc.thePlayer.onGround){stage=1;} else {
                    mc.thePlayer.motionY = 0;
                    if(mc.thePlayer.ticksExisted % 3 == 0){
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12, mc.thePlayer.posZ);
                    } else {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-12, mc.thePlayer.posZ);
                    }
                    mc.timer.timerSpeed = 1.65f;
                }
                if (stage == 1) {
                    oof = lastDist / 159 + 1.5;
                    mc.timer.timerSpeed = 1.0F;
                    stage = 2;
                }
                if (stage == 2) {
                    if (this.mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    } else {
                        if (mc.thePlayer.motionY != 0) {
                            this.mc.thePlayer.motionY = 0F;
                        }

                        if (MovementUtil.isMoving()) {
                            mc.timer.timerSpeed = (float) 1.65F;
                            mc.gameSettings.keyBindRight.pressed = false;
                            mc.gameSettings.keyBindLeft.pressed = false;
                            mc.gameSettings.keyBindBack.pressed = false;
                        }

                        if (oof > 0.25) {
                            oof -= 0.01;
                        }

                        if (speed > 1.0) {
                            speed -= 0.01;
                        }
                        if(mc.thePlayer.ticksExisted % 3 == 0){
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12, mc.thePlayer.posZ);
                        } else {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-12, mc.thePlayer.posZ);
                        }

                        /*this.state += 1;
                        switch (this.state) {
                            case 1:
                                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-12D, this.mc.thePlayer.posZ);
                                break;
                            case 2:
                                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0E-12D, this.mc.thePlayer.posZ);
                                break;
                            case 3:
                                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-12D, this.mc.thePlayer.posZ);
                                this.state = 0;
                                break;
                            default:
                                break;
                        }*/
                    }
                    if (this.mc.thePlayer.onGround) {
                    } else {
                        if(MovementUtil.isMoving()) {
                            MovementUtil.setSpeed(oof);
                        }
                    }
                }

                break;
            case "Verus-Fast":
                if(mc.thePlayer.ticksExisted % 20 == 0){
                    mc.timer.timerSpeed = 0.5f;
                } else {
                    mc.timer.timerSpeed = 3f;
                }

                break;
            case "ZoneCraft":

        }
    }
    @EventTarget
    public void onMotion(EventMotion e){
        switch(e.getState()) {
            case PRE:
                double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                break;
            default: break;
        }
    }

    public void hClip2(double offset) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + -MathHelper.sin(MovementUtil.getDirection()) * offset, mc.thePlayer.posY, mc.thePlayer.posZ + MathHelper.cos(MovementUtil.getDirection()) * offset, false));
        mc.thePlayer.setPosition(mc.thePlayer.posX + -MathHelper.sin(MovementUtil.getDirection()) * offset, mc.thePlayer.posY, mc.thePlayer.posZ + MathHelper.cos(MovementUtil.getDirection()) * offset);
    }

    @EventTarget
    public void onBB(EventBoundingBox e){
        switch(Lime.setmgr.getSettingByNameAndMod("Flight", this).getValString()){
            case "Verus-Fast":
            case "Verus":
            case "BRWServ":
                if(e.getBlock() instanceof BlockAir && e.getBlockPos().getY() < mc.thePlayer.posY)
                    e.setBoundingBox(new AxisAlignedBB(e.getBlockPos().getX(), e.getBlockPos().getY(), e.getBlockPos().getZ(), e.getBlockPos().getX() + 1, mc.thePlayer.posY, e.getBlockPos().getZ() + 1));
                break;
        }
    }
}
