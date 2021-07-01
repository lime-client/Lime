package lime.utils.render.animation.easings;

import lime.utils.time.DeltaTime;

public class Animate {
    private float value, min, max, speed, time;
    private boolean reversed;
    private Easing ease;

    public Animate() {
        this.ease = Easing.LINEAR;
        this.value = 0;
        this.min = 0;
        this.max = 1;
        this.speed = 50;
        this.reversed = false;
    }

    public void reset() { time = min; }

    public void update() {
        if (reversed) {
            if (time > min) { time -= (DeltaTime.getDeltaTime() * .001F * speed); }
            if(time < min) { time = min; }
        } else {
            if (time < max) { time += (DeltaTime.getDeltaTime() * .001F * speed); }
            if(time > max) { time = max; }
        }
        this.value = getEase().ease(time, min, max, max);
    }

    //Getter
    public float getValue() { return value; }
    public float getMin() { return min; }
    public float getMax() { return max; }
    public float getSpeed() { return speed; }
    public boolean isReversed() { return reversed; }
    public Easing getEase() { return ease; }

    //Setter
    public void setValue(float value) { this.value = value; }
    public void setMin(float min) { this.min = min; }
    public void setMax(float max) { this.max = max; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setReversed(boolean reversed) { this.reversed = reversed; }
    public void setEase(Easing ease) { this.ease = ease; }
}
