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

    public static double snapToStep(double value, double valueStep) {
        return valueStep == 0 ? value : Math.round((valueStep * (double) Math.round(value / valueStep)) * 100000D) / 100000D;
    }

    public static float random(double min, double max){
        return (float) (Math.random() * (max - min) + min);
    }

    public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }
}
