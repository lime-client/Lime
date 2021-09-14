package lime.utils.other;

import lime.utils.IUtil;
import net.minecraft.block.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

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

        EnumFacing[] enumFacings = EnumFacing.VALUES;
        int radius = 6;
        for(int r = 1; r < radius; ++r) {
            for (EnumFacing enumFacing : enumFacings) {
                BlockPos pos1 = pos.offset(enumFacing);
                if (isPosSolid(pos1.add(0, -r, 0))) {
                    return new BlockData(pos1.add(0, -r, 0), EnumFacing.UP);
                }
                if (isPosSolid(pos1.add(-r, 0, 0))) {
                    return new BlockData(pos1.add(-r, 0, 0), EnumFacing.EAST);
                }
                if (isPosSolid(pos1.add(r, 0, 0))) {
                    return new BlockData(pos1.add(r, 0, 0), EnumFacing.WEST);
                }
                if (isPosSolid(pos1.add(0, 0, r))) {
                    return new BlockData(pos1.add(0, 0, r), EnumFacing.NORTH);
                }
                if (isPosSolid(pos1.add(0, 0, -r))) {
                    return new BlockData(pos1.add(0, 0, -r), EnumFacing.SOUTH);
                }
            }
        }

        return null;
    }

    private static boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isBlockSolid(mc.theWorld, pos, EnumFacing.DOWN) || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }
}
