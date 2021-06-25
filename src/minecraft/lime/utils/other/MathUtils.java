package lime.utils.other;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
    public static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float random(double min, double max){
        return (float) (Math.random() * (max - min) + min);
    }
}
