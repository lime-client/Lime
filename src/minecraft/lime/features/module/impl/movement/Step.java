package lime.features.module.impl.movement;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventStep;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.other.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;

public class Step extends Module {

    public Step() {
        super("Step", Category.MOVE);
    }

    private boolean resetTimer;
    Timer time = new Timer();
    public static Timer lastStep = new Timer();

    private final EnumProperty mode = new EnumProperty("Mode", this, "NCP", "Vanilla", "NCP", "Verus");
    private final NumberProperty height = new NumberProperty("Height", this, 0.6, 5, 2.5, 0.1);
    private final NumberProperty timer = new NumberProperty("Timer", this, .01, 1, 0.4, 0.01);
    private final NumberProperty delay = new NumberProperty("Delay", this, 0, 2, 0.1, 0.1);

    @Override
    public void onEnable(){
        setSuffix(mode.getSelected());
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
        setSuffix(mode.getSelected());
        double stepValue;
        final float timer = (float) this.timer.getCurrent();
        final float delay = (float) this.delay.getCurrent() * 1000;

        stepValue = height.getCurrent();

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
                switch(mode.getSelected()){
                    case "NCP":
                    case "Verus":
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
        List<Double> offset = Arrays.asList(0.42,0.333,0.248,0.083,-0.078);
        double posX = mc.thePlayer.posX; double posZ = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;
        if(height < 1.1){
            if (mode.is("ncp")) {
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
            } else if(mode.is("verus")) {
                double[] heights = {0.41999998688697815, 0.33319999363422426, 0.24813599859093927, 0.1647732818260721};
                for(double off : heights){
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y += off, posZ, false));
                }
            }
        }else if(height <1.6){
            if(mode.is("ncp")) {
                for (double off : offset) {
                    y += off;
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
                }
            } else if(mode.is("verus")) {
                double[] heights = {0.41999998688697815, 0.33319999363422426, 0.24813599859093927, 0.1647732818260721};
                for(double off : heights){
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y += off, posZ, false));
                }
            }
        }else if(height < 2.1){
            if(mode.is("ncp")) {
                double[] heights = {0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869};
                for(double off : heights){
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
                }
            } else if(mode.is("verus")) {
                double[] heights = {0.41999998688697815, 0.33319999363422426, 0.24813599859093927, 0.1647732818260721, 0.33319999363422426, 0.24813599859093927, 0.1647732818260721};
                for(double off : heights){
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y += off, posZ, false));
                }
                for(double off : heights){
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y += off, posZ, false));
                }
            }
        }else{
            double[] heights = {0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869,2.019,1.907};
            for(double off : heights){
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        }
    }
}
