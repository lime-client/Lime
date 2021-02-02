package lime.module.impl.movement;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.events.impl.EventMove;
import lime.events.impl.EventUpdate;
import lime.module.Module;
import lime.utils.movement.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Speed extends Module {
    public Speed(){
        super("Speed", Keyboard.KEY_V, Category.MOVEMENT);
        Lime.setmgr.rSetting(new Setting("Speed", this, "Vanilla", true, "Vanilla", "BRWServ", "Funcraft"));
        Lime.setmgr.rSetting(new Setting("Speed Power", this, 1, 0, 10, false));
    }
    private int level = 1;
    private double moveSpeed = 0.2873;
    private double lastDist = 0.0;
    private int timerDelay = 0;
    @Override
    public void onEnable() {
        super.onEnable();
        switch(getSettingByName("Speed").getValString()){
            case "Funcraft":
                mc.timer.timerSpeed = 1f;
                if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically) level = 1; else level = 4;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        switch(getSettingByName("Speed").getValString()){
            case "Funcraft":
                mc.timer.timerSpeed = 1f;
                moveSpeed = baseMoveSpeed();
                level = 0;
        }
    }
    @EventTarget
    public void onMotion(EventMotion e){
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @EventTarget
    public void onUpdate(EventUpdate eu){
        switch(getSettingByName("Speed").getValString()){
            case "Vanilla":
                if(MovementUtil.isMoving()) MovementUtil.setSpeed(getSettingByName("Speed Power").getValDouble());
                break;
            case "BRWServ":
                if(MovementUtil.isMoving() && mc.thePlayer.onGround) mc.thePlayer.motionY = 0.42;


                if(MovementUtil.isMoving()){
                    MovementUtil.setSpeed(0.6);
                }
                if(mc.thePlayer.ticksExisted % 3 == 0) mc.thePlayer.motionY -= 0.04;
                break;
        }
    }
    @EventTarget
    public void onMove(EventMove event){
        switch(getSettingByName("Speed").getValString()){
            case "Funcraft":
                ++this.timerDelay;
                this.timerDelay %= 5;
                if (this.timerDelay != 0) {
                    mc.timer.timerSpeed = 1.0f;
                }
                else {
                    if (MovementUtil.isMoving()) {
                        mc.timer.timerSpeed = 32767.0f;
                    }
                    if (MovementUtil.isMoving()) {
                        mc.timer.timerSpeed = 1.3f;
                        final EntityPlayerSP thePlayer = mc.thePlayer;
                        thePlayer.motionX *= 1.0199999809265137;
                        final EntityPlayerSP thePlayer2 = mc.thePlayer;
                        thePlayer2.motionZ *= 1.0199999809265137;
                    }
                }
                if (mc.thePlayer.onGround && MovementUtil.isMoving()) {
                    this.level = 2;
                }
                if (this.round(mc.thePlayer.posY - (int)mc.thePlayer.posY) == this.round(0.138)) {
                    final EntityPlayerSP thePlayer3;
                    final EntityPlayerSP thePlayer = thePlayer3 = mc.thePlayer;
                    thePlayer3.motionY -= 0.08;
                    event.y -= 0.09316090325960147;
                    final EntityPlayerSP entityPlayerSP = thePlayer;
                    entityPlayerSP.posY -= 0.09316090325960147;
                }
                if (this.level == 1 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                    this.level = 2;
                    this.moveSpeed = 1.35 * this.getBaseMoveSpeed() - 0.01;
                }
                else if (this.level == 2) {
                    this.level = 3;
                    mc.thePlayer.motionY = 0.399399995803833;
                    event.y = 0.399399995803833;
                    this.moveSpeed *= 2.149;
                }
                else if (this.level == 3) {
                    this.level = 4;
                    final double difference = 0.66 * (this.lastDist - this.getBaseMoveSpeed());
                    this.moveSpeed = this.lastDist - difference;
                }
                else {
                    if (mc.theWorld.getCollidingBoundingBoxes((Entity)mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically) {
                        this.level = 1;
                    }
                    this.moveSpeed = this.lastDist - this.lastDist / 159.0;
                }
                this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
                final MovementInput movementInput = mc.thePlayer.movementInput;
                float forward = movementInput.moveForward;
                float strafe = movementInput.moveStrafe;
                float yaw = mc.thePlayer.rotationYaw;
                if (forward == 0.0f && strafe == 0.0f) {
                    event.x = 0.0;
                    event.z = 0.0;
                }
                else if (forward != 0.0f) {
                    if (strafe >= 1.0f) {
                        yaw += ((forward > 0.0f) ? -45 : 45);
                        strafe = 0.0f;
                    }
                    else if (strafe <= -1.0f) {
                        yaw += ((forward > 0.0f) ? 45 : -45);
                        strafe = 0.0f;
                    }
                    if (forward > 0.0f) {
                        forward = 1.0f;
                    }
                    else if (forward < 0.0f) {
                        forward = -1.0f;
                    }
                }
                final double mx2 = Math.cos(Math.toRadians(yaw + 90.0f));
                final double mz2 = Math.sin(Math.toRadians(yaw + 90.0f));
                event.x = forward * this.moveSpeed * mx2 + strafe * this.moveSpeed * mz2;
                event.z = forward * this.moveSpeed * mz2 - strafe * this.moveSpeed * mx2;
                mc.thePlayer.stepHeight = 0.6f;
                if (forward == 0.0f && strafe == 0.0f) {
                    event.x = 0.0;
                    event.z = 0.0;
                }
                break;
        }
    }
    private double round(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
    private double baseMoveSpeed()
    {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed)).getAmplifier() + 1;
        return baseSpeed;
    }
    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }
}
