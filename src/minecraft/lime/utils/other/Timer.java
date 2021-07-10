package lime.utils.other;

public class Timer {

    private long lastMS = 0L;

    public long getCurrentMS(){
        return System.nanoTime() / 1000000L;
    }

    public long getTimeElapsed() {
        return getCurrentMS() - lastMS;
    }

    public boolean hasReached(long milliseconds){
        return getCurrentMS() - lastMS >= milliseconds;
    }

    public void reset(){
        lastMS = getCurrentMS();
    }

    public void reset(long offset){
        lastMS = getCurrentMS() - offset;
    }
}