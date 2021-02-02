
package lime.module.impl.combat.infiniteaura;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InfAuraPathFinder {
    private InfiniteAuraVec3 startInfiniteAuraVec3;
    private InfiniteAuraVec3 endInfiniteAuraVec3;
    private ArrayList<InfiniteAuraVec3> path = new ArrayList<InfiniteAuraVec3>();
    private ArrayList<Hub> hubs = new ArrayList<Hub>();
    private ArrayList<Hub> hubsToWork = new ArrayList<Hub>();
    private double minDistanceSquared = 9;
    private boolean nearest = true;

    private static InfiniteAuraVec3[] flatCardinalDirections = {
            new InfiniteAuraVec3(1, 0, 0),
            new InfiniteAuraVec3(-1, 0, 0),
            new InfiniteAuraVec3(0, 0, 1),
            new InfiniteAuraVec3(0, 0, -1)
    };

    public InfAuraPathFinder(InfiniteAuraVec3 startInfiniteAuraVec3, InfiniteAuraVec3 endInfiniteAuraVec3) {
        this.startInfiniteAuraVec3 = startInfiniteAuraVec3.addVector(0, 0, 0).floor();
        this.endInfiniteAuraVec3 = endInfiniteAuraVec3.addVector(0, 0, 0).floor();
    }

    public ArrayList<InfiniteAuraVec3> getPath() {
        return path;
    }

    public void compute() {
        compute(1000, 4);
    }

    public void compute(int loops, int depth) {
        path.clear();
        hubsToWork.clear();
        ArrayList<InfiniteAuraVec3> initPath = new ArrayList<InfiniteAuraVec3>();
        initPath.add(startInfiniteAuraVec3);
        hubsToWork.add(new Hub(startInfiniteAuraVec3, null, initPath, startInfiniteAuraVec3.squareDistanceTo(endInfiniteAuraVec3), 0, 0));
        search:
        for (int i = 0; i < loops; i++) {
            Collections.sort(hubsToWork, new CompareHub());
            int j = 0;
            if (hubsToWork.size() == 0) {
                break;
            }
            for (Hub hub : new ArrayList<Hub>(hubsToWork)) {
                j++;
                if (j > depth) {
                    break;
                } else {
                    hubsToWork.remove(hub);
                    hubs.add(hub);

                    for (InfiniteAuraVec3 direction : flatCardinalDirections) {
                        InfiniteAuraVec3 loc = hub.getLoc().add(direction).floor();
                        if (checkPositionValidity(loc, false)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    InfiniteAuraVec3 loc1 = hub.getLoc().addVector(0, 1, 0).floor();
                    if (checkPositionValidity(loc1, false)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    InfiniteAuraVec3 loc2 = hub.getLoc().addVector(0, -1, 0).floor();
                    if (checkPositionValidity(loc2, false)) {
                        if (addHub(hub, loc2, 0)) {
                            break search;
                        }
                    }
                }
            }
        }
        if (nearest) {
            Collections.sort(hubs, new CompareHub());
            path = hubs.get(0).getPath();
        }
    }

    public static boolean checkPositionValidity(InfiniteAuraVec3 loc, boolean checkGround) {
        return checkPositionValidity((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), checkGround);
    }

    public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
    }
    public BlockPos getBlock(int x, int y, int z){
        return new BlockPos(x, y, z);
    }

    private static boolean isBlockSolid(BlockPos block) {

        return block.getBlock().isFullBlock() ||
                (block.getBlock() instanceof BlockSlab) ||
                (block.getBlock() instanceof BlockStairs)||
                (block.getBlock() instanceof BlockCactus)||
                (block.getBlock() instanceof BlockChest)||
                (block.getBlock() instanceof BlockEnderChest)||
                (block.getBlock() instanceof BlockSkull)||
                (block.getBlock() instanceof BlockPane)||
                (block.getBlock() instanceof BlockFence)||
                (block.getBlock() instanceof BlockWall)||
                (block.getBlock() instanceof BlockGlass)||
                (block.getBlock() instanceof BlockPistonBase)||
                (block.getBlock() instanceof BlockPistonExtension)||
                (block.getBlock() instanceof BlockPistonMoving)||
                (block.getBlock() instanceof BlockStainedGlass)||
                (block.getBlock() instanceof BlockTrapDoor);
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        return !(block.getBlock() instanceof BlockFence) &&
                !(block.getBlock() instanceof BlockWall);
    }

    public Hub isHubExisting(InfiniteAuraVec3 loc) {
        for (Hub hub : hubs) {
            if (hub.getLoc().getX() == loc.getX() && hub.getLoc().getY() == loc.getY() && hub.getLoc().getZ() == loc.getZ()) {
                return hub;
            }
        }
        for (Hub hub : hubsToWork) {
            if (hub.getLoc().getX() == loc.getX() && hub.getLoc().getY() == loc.getY() && hub.getLoc().getZ() == loc.getZ()) {
                return hub;
            }
        }
        return null;
    }

    public boolean addHub(Hub parent, InfiniteAuraVec3 loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if ((loc.getX() == endInfiniteAuraVec3.getX() && loc.getY() == endInfiniteAuraVec3.getY() && loc.getZ() == endInfiniteAuraVec3.getZ()) || (minDistanceSquared != 0 && loc.squareDistanceTo(endInfiniteAuraVec3) <= minDistanceSquared)) {
                path.clear();
                path = parent.getPath();
                path.add(loc);
                return true;
            } else {
                ArrayList<InfiniteAuraVec3> path = new ArrayList<InfiniteAuraVec3>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(endInfiniteAuraVec3), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<InfiniteAuraVec3> path = new ArrayList<InfiniteAuraVec3>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endInfiniteAuraVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    private class Hub {
        private InfiniteAuraVec3 loc = null;
        private Hub parent = null;
        private ArrayList<InfiniteAuraVec3> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(InfiniteAuraVec3 loc, Hub parent, ArrayList<InfiniteAuraVec3> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public InfiniteAuraVec3 getLoc() {
            return loc;
        }

        public Hub getParent() {
            return parent;
        }

        public ArrayList<InfiniteAuraVec3> getPath() {
            return path;
        }

        public double getSquareDistanceToFromTarget() {
            return squareDistanceToFromTarget;
        }

        public double getCost() {
            return cost;
        }

        public void setLoc(InfiniteAuraVec3 loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(ArrayList<InfiniteAuraVec3> path) {
            this.path = path;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }

    public class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int) (
                    (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()) - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost())
            );
        }
    }
}