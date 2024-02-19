package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMove;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.KillAura;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.combat.CombatUtils;
import lime.utils.movement.MovementUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TargetStrafe extends Module {
    public TargetStrafe() {
        super("Target Strafe", Category.MOVE);
    }

    private final NumberProperty distance = new NumberProperty("Distance", this, 0.5, 5, 1.5, 0.1);
    private final BooleanProperty holdSpace = new BooleanProperty("Hold Space", this, true);
    private final BooleanProperty fallCheck = new BooleanProperty("Fall Check", this, true);
    private final BooleanProperty wallCheck = new BooleanProperty("Wall Check", this, true);
    private final BooleanProperty speedOnly = new BooleanProperty("Speed Only", this, true);
    private static int direction = -1; // -1 = Right, 1 = Left
    private static int index = 0, ticks = 0;

    @Override
    public void onEnable() {
        index = 0;
        direction = -1;
        ticks = 0;
    }

    @EventTarget
    public void onMotion(EventMove e) {
        setSpeed(e, MovementUtils.getSpeed());
    }

    public static void setSpeed(EventMove e, double speed) {

        Minecraft mc = Minecraft.getMinecraft();
        TargetStrafe ts = Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
        if(KillAura.getEntity() == null) {
            return;
        }
        if((ts.holdSpace.isEnabled() && !mc.gameSettings.keyBindJump.isKeyDown()) || (!(Lime.getInstance().getModuleManager().getModuleC(Speed.class).isToggled() || Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) && ts.speedOnly.isEnabled())) {
            if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled() || Lime.getInstance().getModuleManager().getModuleC(Speed.class).isToggled()) {
                MovementUtils.setSpeed(e, speed, mc.thePlayer.rotationYaw, mc.thePlayer.moveStrafing, mc.thePlayer.movementInput.moveForward);
            }
            return;
        }
        List<Point> points = ts.getPoints(KillAura.getEntity());
        index = ts.getIndex(points);

        if((!points.get(index).isValid() || mc.thePlayer.isCollidedHorizontally) && ticks > 5) {
            direction = -direction;
            ticks = 0;
        }

        ++ticks;

        ts.changeIndex();

        index = Math.max(index, 0);
        Point point = points.get(index);
        float yaw = CombatUtils.getRotationFromPosition(point.getX(), point.getZ());
        MovementUtils.setSpeed(e, speed, yaw, -direction, mc.thePlayer.getDistanceToEntity(KillAura.getEntity()) > ts.distance.getCurrent() ? 1 : 0);
    }

    public int getIndex(List<Point> points) {
        List<Point> points1 = new ArrayList<>(points);
        points1.sort(Comparator.comparingDouble(p -> mc.thePlayer.getDistance(p.getX(), p.getY(), p.getZ())));
        return points.indexOf(points1.get(0));
    }

    public void changeIndex() {
        if(direction == 1) {
            index -= 5;
            if(index < 0) {
                index = 360 + index;
            }
        } else if(direction == -1) {
            index += 5;
            if(index > 360) {
                index = index - 360;
            }
        }
    }

    public List<Point> getPoints(EntityLivingBase entity) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            Point point = new Point((entity.posX + (entity.posX - entity.lastTickPosX)) + -Math.sin(Math.toRadians(i)) * distance.getCurrent(), entity.posY, (entity.posZ + (entity.posZ - entity.lastTickPosZ)) + Math.cos(Math.toRadians(i)) * distance.getCurrent(), true);

            Block block = new BlockPos(point.getX(), point.getY(), point.getZ()).getBlock();
            if((fallCheck.isEnabled() && !couldFall(point)) || (wallCheck.isEnabled() && block.getMaterial().isSolid())) {
                point.setValid(false);
            }

            points.add(point);
        }

        return points;
    }

    public boolean couldFall(Point point) {
        if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) {
            return true;
        }
        for (int i = (int)point.getY(); i > point.getY() - 3; i--) {
            if(!(new BlockPos(point.getX(), i, point.getZ()).getBlock() instanceof BlockAir)) {
                return true;
            }
        }

        return false;
    }

    public static class Point {
        private double x, y, z;
        private boolean valid;

        public Point(double x, double y, double z, boolean valid) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.valid = valid;
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

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setZ(double z) {
            this.z = z;
        }
    }
}
