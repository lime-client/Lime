package lime.utils.movement.pathfinder;

import net.minecraft.util.Vec3;

public class CustomVec {
    private final double x, y, z;

    public CustomVec(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public CustomVec addVector(double x, double y, double z) {
        return new CustomVec(this.x + x, this.y + y, this.z + z);
    }

    public CustomVec floor() {
        return new CustomVec(Math.floor(this.x), Math.floor(y), Math.floor(z));
    }

    public double squareDistanceTo(CustomVec v) {
        return Math.pow(v.x - this.x, 2) + Math.pow(v.y - this.y, 2) + Math.pow(v.z - this.z, 2);
    }

    public CustomVec add(CustomVec v) {
        return addVector(v.getX(), v.getY(), v.getZ());
    }

    public Vec3 mc() {
        return new Vec3(this.x, this.y, this.z);
    }
}
