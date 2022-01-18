package lime.utils.other;

public class Timer {
    private long lastMS = 0;

    public long getCurrentMS(){
        return System.nanoTime() / 1000000;
    }

    public long getTimeElapsed() {
        return getCurrentMS() - lastMS;
    }

    public boolean hasReached(long milliseconds){
        return getCurrentMS() - lastMS >= milliseconds;
    }

    public boolean hasReached(long ms, boolean reset) {
        if(reset && getCurrentMS() - lastMS >= ms) {
            reset();
        }

        return getCurrentMS() - lastMS >= ms;
    }

    public long hasTimeLeft(final long MS) {
        return (MS + lastMS) - System.currentTimeMillis();
    }

    public void reset(){
        lastMS = getCurrentMS();
    }

    public void reset(long offset){
        lastMS = getCurrentMS() - offset;
    }
}