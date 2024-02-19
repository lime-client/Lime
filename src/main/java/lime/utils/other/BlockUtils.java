package lime.utils.other;

import lime.core.Lime;
import lime.features.module.impl.world.Scaffold;
import lime.utils.IUtil;
import net.minecraft.block.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

import java.util.*;

public class BlockUtils implements IUtil {

    public static class BlockData
    {
        private final BlockPos blockPos;
        private final EnumFacing enumFacing;

        public BlockData(BlockPos blockPos, EnumFacing enumFacing)
        {
            this.blockPos = blockPos;
            this.enumFacing = enumFacing;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public EnumFacing getEnumFacing() {
            return enumFacing;
        }
    }

    public static BlockData getBlockData(BlockPos pos) {
        if(isPosSolid(pos.add(0, 1, 0))) {
            return new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        Scaffold scaffold = Lime.getInstance().getModuleManager().getModuleC(Scaffold.class);
        if(scaffold.search.is("basic")) {
            return null;
        }
        List<BlockData> blockDataList = new ArrayList<>();
        int radius = 10;
        for(int r = 0; r < radius; ++r) {
            for (EnumFacing enumFacing : EnumFacing.VALUES) {
                BlockPos pos1 = pos.offset(enumFacing);
                if(pos1.getY() == (int) mc.thePlayer.posY) {
                    pos1 = pos1.add(0, -1, 0);
                }
                int radiusY = Math.min(Math.max(r, 1), 4);
                if (isPosSolid(pos1.add(0, -radiusY, 0))) {
                    if(isPlayerPos(pos1.add(0, -radiusY, 0))) continue;
                    blockDataList.add(new BlockData(pos1.add(0, -radiusY, 0), EnumFacing.UP));
                }
                if (isPosSolid(pos1.add(-r, 0, 0))) {
                    if(isPlayerPos(pos1.add(-r, 0, 0))) continue;
                    blockDataList.add(new BlockData(pos1.add(-r, 0, 0), EnumFacing.EAST));
                }
                if (isPosSolid(pos1.add(r, 0, 0))) {
                    if(isPlayerPos(pos1.add(r, 0, 0))) continue;
                    blockDataList.add(new BlockData(pos1.add(r, 0, 0), EnumFacing.WEST));
                }
                if (isPosSolid(pos1.add(0, 0, r))) {
                    if(isPlayerPos(pos1.add(0,0,r))) continue;
                    blockDataList.add(new BlockData(pos1.add(0, 0, r), EnumFacing.NORTH));
                }
                if (isPosSolid(pos1.add(0, 0, -r))) {
                    if(isPlayerPos(pos1.add(0,0,-r))) continue;
                    blockDataList.add(new BlockData(pos1.add(0, 0, -r), EnumFacing.SOUTH));
                }
                if (isPosSolid(pos1.add(-r, 0, -r))) {
                    if(isPlayerPos(pos1.add(-r,0,-r))) continue;
                    blockDataList.add(new BlockData(pos1.add(-r, 0, -r), EnumFacing.SOUTH));
                }
                if (isPosSolid(pos1.add(r, 0, -r))) {
                    if(isPlayerPos(pos1.add(0,0,-r))) continue;
                    blockDataList.add(new BlockData(pos1.add(r, 0, -r), EnumFacing.SOUTH));
                }
                if (isPosSolid(pos1.add(-r, 0, r))) {
                    if(isPlayerPos(pos1.add(-r,0,r))) continue;
                    blockDataList.add(new BlockData(pos1.add(-r, 0, r), EnumFacing.NORTH));
                }
                if (isPosSolid(pos1.add(r, 0, r))) {
                    if(isPlayerPos(pos1.add(r,0,r))) continue;
                    blockDataList.add(new BlockData(pos1.add(r, 0, r), EnumFacing.NORTH));
                }
            }
        }
        if(!blockDataList.isEmpty()) {
            blockDataList.sort(Comparator.comparingDouble(b -> mc.thePlayer.getDistance(b.getBlockPos().getX(), b.getBlockPos().getY(), b.getBlockPos().getZ())));
            return blockDataList.get(0);
        }
        return null;
    }

    private static boolean isPlayerPos(BlockPos blockPos) {
        return blockPos.getX() == (int) mc.thePlayer.posX && blockPos.getY() == (int) mc.thePlayer.posY && blockPos.getZ() == (int) mc.thePlayer.posZ;
    }

    private static boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isBlockSolid(mc.theWorld, pos, EnumFacing.DOWN) || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }
}
