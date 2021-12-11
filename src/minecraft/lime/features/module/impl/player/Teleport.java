package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.ui.gui.ProcessBar;
import lime.utils.movement.pathfinder.CustomVec;
import lime.utils.movement.pathfinder.utils.PathComputer;
import lime.utils.other.PlayerUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.LinkedList;
import java.util.Queue;

public class Teleport extends Module {
    public Teleport() {
        super("Teleport", Category.PLAYER);
    }

    private final BooleanProperty latestVerus = new BooleanProperty("Latest Verus", this, false);

    private Queue<CustomVec> vecs = new LinkedList<>();
    private ProcessBar processBar;
    private BlockPos targetBlock;
    private boolean damaged, rng;

    @Override
    public void onEnable() {
        vecs = null;
        targetBlock = null;
        ScaledResolution sr = new ScaledResolution(mc);
        processBar = new ProcessBar((sr.getScaledWidth() / 2) - 25, (sr.getScaledHeight() / 2) + 20, 1500);
        processBar.getTimer().reset();
        damaged = false;
        rng = false;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(!processBar.getTimer().hasReached(1500)) {
            e.setCanceled(true);
        }
        if(processBar.getTimer().hasReached(1500) && !damaged) {
            PlayerUtils.verusDamage(!latestVerus.isEnabled());
            damaged = true;
        }
        if(e.isPre() && vecs != null) {
            if(vecs.size() > 0) {
                e.setCanceled(true);
                CustomVec vec = vecs.poll();
                rng = !rng;
                mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec.getX(), vec.getY()+1-(rng ? 0.0784000015258789 : 0), vec.getZ(), false));
            }

            if(vecs.size() == 0) {
                mc.thePlayer.setPosition(targetBlock.getX(), targetBlock.getY()+1, targetBlock.getZ());
                e.setCanceled(true);
                toggle();
            }
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(!processBar.getTimer().hasReached(1500) || (damaged && vecs == null) || (vecs != null && vecs.size() > 0)) {
            e.setCanceled(true);
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        processBar.draw();
    }

    private MovingObjectPosition getBlinkBlock() {
        Vec3 eyeEight = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        Vec3 lookVec = mc.thePlayer.getLook(mc.timer.renderPartialTicks);
        Vec3 vec = eyeEight.addVector(lookVec.xCoord * 70, lookVec.yCoord * 70, lookVec.zCoord * 70);
        return mc.thePlayer.worldObj.rayTraceBlocks(eyeEight, vec, false, false, true);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
            BlockPos blockPos = getBlinkBlock().getBlockPos();
            targetBlock = blockPos;

            vecs = new LinkedList<>(PathComputer.computePath(new CustomVec(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new CustomVec(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 1000, 4, 7));
        }
    }
}
