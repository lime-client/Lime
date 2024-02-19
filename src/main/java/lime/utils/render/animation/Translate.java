package lime.utils.render.animation;

public class Translate {
    private float x, y;
    private long lastMS;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
        this.lastMS = System.currentTimeMillis();
    }

    public void interpolate(float targetX, float targetY, double speed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - lastMS;
        lastMS = currentMS;
        double deltaX = 0;
        double deltaY = 0;
        if(speed != 0){
            deltaX = (Math.abs(targetX - x) * 0.35f)/(10/speed);
            deltaY = (Math.abs(targetY - y) * 0.35f)/(10/speed);
        }
        x = calculateCompensation(targetX, x, delta, deltaX);
        y = calculateCompensation(targetY, y, delta, deltaY);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public static float calculateCompensation(float target, float current, long delta, double speed) {
        float diff = current - target;
        if (delta < 1) {
            delta = 1;
        }
        if (delta > 1000) {
            delta = 16;
        }
        if (diff > speed) {
            double xD = (speed * delta / (1000 / 60) < 0.5 ? 0.5 : speed * delta / (1000 / 60));
            current -= xD;
            if (current < target) {
                current = target;
            }
        } else if (diff < -speed) {
            double xD = (speed * delta / (1000 / 60) < 0.5 ? 0.5 : speed * delta / (1000 / 60));
            current += xD;
            if (current > target) {
                current = target;
            }
        } else {
            current = target;
        }
        return current;
    }
}
