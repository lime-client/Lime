package lime.module.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import lime.Lime;
import lime.cgui.settings.Setting;
import lime.events.EventTarget;
import lime.events.impl.EventStep;
import lime.module.Module;
import lime.utils.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Step extends Module {
    public final static String MODE = "MODE";
    public final static String STEP = "HEIGHT";
    public final static String NCPHEIGHT = "HEIGHT2";
    public final static String TIMER = "TIMER";
    public final static String DELAY= "DELAY";
    boolean resetTimer;
    Timer time = new Timer();
    public static Timer lastStep = new Timer();
    public Step() {
        super("Step", 0, Category.MOVEMENT);
        Lime.setmgr.rSetting(new Setting("Mode", this, "NCP", "Vanilla", "NCP"));
        Lime.setmgr.rSetting(new Setting("Height", this, 2.5, 0.5, 5, false));
        Lime.setmgr.rSetting(new Setting("Timer", this, 0.2, 0.01, 1, false));
        Lime.setmgr.rSetting(new Setting("Delay", this, 0.1, 0, 2, false));
    }

    @Override
    public void onEnable(){
        resetTimer = false;
        super.onEnable();
    }
    @Override
    public void onDisable(){
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }
    @EventTarget
    public void onStep(EventStep es){
        double stepValue = 1.5D;
        final float timer = (float) getSettingByName("Timer").getValDouble();
        final float delay = (float) getSettingByName("Delay").getValDouble() * 1000;

        if(getSettingByName("Mode").getValString().equalsIgnoreCase("Vanilla"))
            stepValue = getSettingByName("Height").getValDouble();
        if(getSettingByName("Mode").getValString().equalsIgnoreCase("NCP"))
            stepValue = getSettingByName("Height").getValDouble();

        if(resetTimer){
            resetTimer = !resetTimer;
            mc.timer.timerSpeed = 1;
        }
        Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock();
        if(!(block instanceof BlockLiquid))
            if (es.isPre()) {
                if(mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.isKeyDown() && time.hasReached((long)delay)){
                    es.setStepHeight(stepValue);
                    es.setActive(true);
                }

            }else{

                double rheight = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
                boolean canStep = rheight >= 0.625;
                if(canStep){
                    lastStep.reset();
                    time.reset();
                }
                switch(getSettingByName("Mode").getValString()){
                    case "NCP":
                        if(canStep){
                            mc.timer.timerSpeed = timer - (rheight >= 1 ? Math.abs(1-(float)rheight)*((float)timer*0.55f) : 0);
                            if(mc.timer.timerSpeed <= 0.05f){
                                mc.timer.timerSpeed = 0.05f;
                            }
                            resetTimer = true;
                            ncpStep(rheight);
                        }
                        break;
                    case "Vanilla":

                        return;
                }


            }
    }
    void ncpStep(double height){
        List<Double>offset = Arrays.asList(0.42,0.333,0.248,0.083,-0.078);
        double posX = mc.thePlayer.posX; double posZ = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;
        if(height < 1.1){
            double first = 0.42;
            double second = 0.75;
            if(height != 1){
                first *= height;
                second *= height;
                if(first > 0.425){
                    first = 0.425;
                }
                if(second > 0.78){
                    second = 0.78;
                }
                if(second < 0.49){
                    second = 0.49;
                }
            }
            if(first == 0.42)
                first = 0.41999998688698;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
            if(y+second < y + height)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
            return;
        }else if(height <1.6){
            for(int i = 0; i < offset.size(); i++){
                double off = offset.get(i);
                y += off;
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
            }
        }else if(height < 2.1){
            double[] heights = {0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869};
            for(double off : heights){
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        }else{
            double[] heights = {0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869,2.019,1.907};
            for(double off : heights){
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        }

    }

}
