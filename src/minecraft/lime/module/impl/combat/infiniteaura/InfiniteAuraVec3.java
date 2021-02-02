package lime.module.impl.combat.infiniteaura;

public class InfiniteAuraVec3 {
    private double x, y, z;

    public InfiniteAuraVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public InfiniteAuraVec3 addVector(double x, double y, double z) {
        return new InfiniteAuraVec3(this.x + x, this.y + y, this.z + z);
    }

    public InfiniteAuraVec3 floor() {
        return new InfiniteAuraVec3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }

    public double squareDistanceTo(InfiniteAuraVec3 v) {
        return Math.pow(v.x - this.x, 2) + Math.pow(v.y - this.y, 2) + Math.pow(v.z - this.z, 2);
    }

    public InfiniteAuraVec3 add(InfiniteAuraVec3 v) {
        return addVector(v.getX(), v.getY(), v.getZ());
    }

    public net.minecraft.util.Vec3 mc() {
        return new net.minecraft.util.Vec3(x, y, z);
    }

    @Override
    public String toString() {
        return "[" + x + ";" + y + ";" + z + "]";
    }
}