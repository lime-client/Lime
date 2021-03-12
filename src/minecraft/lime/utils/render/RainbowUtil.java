package lime.utils.render;

import net.minecraft.util.MathHelper;

import java.awt.*;

public class RainbowUtil {
    public static Color rainbow(long d, float brightness, float speed) {
        float hue = (float) (System.nanoTime() + (d * speed)) / 1.0E09F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, brightness, 1F)).intValue()), 16);
        Color c = new Color((int) color);
        return new Color(c.getRed()/255.0F, c.getGreen()/255.0F, c.getBlue()/255.0F, c.getAlpha()/255.0F);
    }
    public static Color darker(Color color, float factor) {
        //in case of keks
        factor = MathHelper.clamp_float(factor, 0.001f, 0.999f);

        return new Color(Math.max((int) (color.getRed() * factor), 0), Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0), color.getAlpha());
    }
    public static int rainbow2(int speed, int offset, int alpha) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        Color c = Color.getHSBColor(hue, 1f, 1f);
        int a = alpha;
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        a = (a << 24) & 0xFF000000;
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        return 0x00000000 | a | r | g | b;
    }

    public static int rainbow3(int speed, int offset, int alpha, int min, int max) {
        if (min > max) {
            int min2 = min;
            min = max;
            max = min2;
        }
        if (max > 255) {
            max = 255;
        }
        if (min < 0) {
            min = 0;
        }
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        Color c = Color.getHSBColor(hue, 1f, 1f);
        int a = alpha;
        int r = (int) (min + (((float)c.getRed()) / 255f) * (max - min));
        int g = (int) (min + (((float)c.getGreen()) / 255f)* (max - min));
        int b = (int) (min + (((float)c.getBlue()) / 255f)* (max - min));
        a = (a << 24) & 0xFF000000;
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        return 0x00000000 | a | r | g | b;
    }

    public static Color blend2colors(Color color1, Color color2, double offset) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }
        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        int alphaPart = (int) (color1.getAlpha() * inverse_percent + color2.getAlpha() * offset);
        return new Color(redPart, greenPart, bluePart, alphaPart);
    }
    public static Color getGradient(double offset, Color color1, Color color2, Color color3) {
        Color color11 = color1;
        Color color22 = color2;
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;

        }

        offset = offset * 2;

        if(offset >= 1) {
            offset -= 1;
            color11 = color2;
            color22 = color3;

        }

        double inverse_percent = 1 - offset;
        int redPart = (int) (color11.getRed() * inverse_percent + color22.getRed() * offset);
        int greenPart = (int) (color11.getGreen() * inverse_percent + color22.getGreen() * offset);
        int bluePart = (int) (color11.getBlue() * inverse_percent + color22.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }
}
