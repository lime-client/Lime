package lime.utils.movement.pathfinder;

import net.minecraft.block.*;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PathFinder {

    private final CustomVec startVec;
    private final CustomVec endVec;
    private ArrayList<CustomVec> path = new ArrayList<>();
    private final ArrayList<Hub> hubs = new ArrayList<>();
    private final ArrayList<Hub> hubsToWork = new ArrayList<>();
    private double minDistanceSquared = 9;
    private boolean nearest = true;

    private static CustomVec[] flatCardinalDirections = {
            new CustomVec(1, 0, 0),
            new CustomVec(-1, 0, 0),
            new CustomVec(0, 0, 1),
            new CustomVec(0, 0, -1)
    };

    public PathFinder(CustomVec startVec, CustomVec endVec) {
        this.startVec = startVec.addVector(0, 0, 0).floor();
        this.endVec = endVec.addVector(0, 0, 0).floor();
    }

    public ArrayList<CustomVec> getPath() {
        return path;
    }

    public void compute() {
        compute(1000, 4);
    }

    public void compute(int loops, int depth) {
        path.clear();
        hubsToWork.clear();
        ArrayList<CustomVec> initPath = new ArrayList<>();
        initPath.add(startVec);
        hubsToWork.add(new Hub(startVec, null, initPath, startVec.squareDistanceTo(endVec), 0, 0));
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

                    for (CustomVec direction : flatCardinalDirections) {
                        CustomVec loc = hub.getLoc().add(direction).floor();
                        if (checkPositionValidity(loc, false)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    CustomVec loc1 = hub.getLoc().addVector(0, 1, 0).floor();
                    if (checkPositionValidity(loc1, false)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    CustomVec loc2 = hub.getLoc().addVector(0, -1, 0).floor();
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

    public boolean checkPositionValidity(CustomVec loc, boolean checkGround) {
        return checkPositionValidity((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), checkGround);
    }

    public boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
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

    public Hub isHubExisting(CustomVec loc) {
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

    public boolean addHub(Hub parent, CustomVec loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if ((loc.getX() == endVec.getX() && loc.getY() == endVec.getY() && loc.getZ() == endVec.getZ()) || (minDistanceSquared != 0 && loc.squareDistanceTo(endVec) <= minDistanceSquared)) {
                path.clear();
                path = parent.getPath();
                path.add(loc);
                return true;
            } else {
                ArrayList<CustomVec> path = new ArrayList<>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(endVec), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<CustomVec> path = new ArrayList<>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endVec));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }


    private class Hub {
        private CustomVec loc = null;
        private Hub parent = null;
        private ArrayList<CustomVec> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(CustomVec loc, Hub parent, ArrayList<CustomVec> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public CustomVec getLoc() {
            return loc;
        }

        public Hub getParent() {
            return parent;
        }

        public ArrayList<CustomVec> getPath() {
            return path;
        }

        public double getSquareDistanceToFromTarget() {
            return squareDistanceToFromTarget;
        }

        public double getCost() {
            return cost;
        }

        public void setLoc(CustomVec loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(ArrayList<CustomVec> path) {
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
