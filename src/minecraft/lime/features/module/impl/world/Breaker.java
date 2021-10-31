package lime.features.module.impl.world;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventPacket;
import lime.features.commands.impl.BlackListBed;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.combat.CombatUtils;
import lime.utils.combat.Rotation;
import lime.utils.render.RenderUtils;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Breaker extends Module {

    public Breaker() {
        super("Breaker", Category.WORLD);
    }

    private final NumberProperty range = new NumberProperty("Range", this, 1, 6, 4, 0.5);
    private final BooleanProperty rotations = new BooleanProperty("Rotations", this, true);
    private final BooleanProperty noSwing = new BooleanProperty("No Swing", this, true);

    private final ArrayList<BlockPos> bedPos = new ArrayList<>();
    private BlockPos attackingBed;

    @Override
    public void onEnable() {
        bedPos.clear();
        attackingBed = null;
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S01PacketJoinGame) {
            BlackListBed.beds.clear();
        }
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        if(attackingBed != null && (attackingBed.getBlock() != Blocks.bed || mc.thePlayer.getDistance(attackingBed.getX(), attackingBed.getY(), attackingBed.getZ()) > range.intValue())) {
            attackingBed = null;
        }
        if(attackingBed == null) {
            bedPos.clear();
            for(int y = -range.intValue(); y <= range.intValue(); ++y) {
                for(int x = -range.intValue(); x <= range.intValue(); ++x) {
                    for(int z = -range.intValue(); z <= range.intValue(); ++z) {
                        BlockPos blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                        if((blockPos.getBlock() == Blocks.bed || blockPos.getBlock() == Blocks.cake) && mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) <= range.getCurrent() && !bedPos.contains(blockPos)) {
                            bedPos.add(blockPos);
                        }
                    }
                }
            }
            if(bedPos.isEmpty()) return;
            bedPos.sort(Comparator.comparingDouble(pos -> mc.thePlayer.getDistanceSq(pos)));
            attackingBed = bedPos.get(0);
        } else {
            if(e.isPre()) {
                if(!rotations.isEnabled()) return;
                float[] rots = CombatUtils.getRotations(attackingBed.getX(), attackingBed.getY(), attackingBed.getZ());
                e.setRotations(new Rotation(rots[0], rots[1] + 0.5f));
                mc.thePlayer.setRotationsTP(e);
            } else {
                if(noSwing.isEnabled())
                    mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                else
                    mc.thePlayer.swingItem();
                mc.playerController.onPlayerDamageBlock(attackingBed, getFacingDirection(attackingBed));
            }
        }
    }

    @EventTarget
    public void on3D(Event3D e) {
        if(attackingBed != null) {
            RenderUtils.drawBox(attackingBed.getX(), attackingBed.getY(), attackingBed.getZ(), 0.57, new Color(255, 0, 0, 80), true, false);
            RenderUtils.drawBox(attackingBed.getX(), attackingBed.getY(), attackingBed.getZ(), 0.57, new Color(255, 0, 0, 80), true, true);
        }
    }

    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing closestEnum = EnumFacing.UP;
        float rotations = MathHelper.wrapAngleTo180_float(CombatUtils.getRotations(pos.getX(), pos.getY(), pos.getZ())[0]);
        if(rotations >= 45 && rotations <= 135){
            closestEnum = EnumFacing.EAST;
        }else if((rotations >= 135 && rotations <= 180) ||
                (rotations <= -135 && rotations >= -180)){
            closestEnum = EnumFacing.SOUTH;
        }else if(rotations <= -45 && rotations >= -135){
            closestEnum = EnumFacing.WEST;
        }else if((rotations >= -45 && rotations <= 0) ||
                (rotations <= 45 && rotations >= 0)){
            closestEnum = EnumFacing.NORTH;
        }

        return closestEnum;
    }

}
