package lime.utils.render;

import lime.utils.render.fontRenderer.GlyphPageFontRenderer;

import java.awt.*;

public class ColorUtils {
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

    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];

        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + (float)index / (float)(count + 1) * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;

        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public static int getAstolfo(int delay, float offset) {
        float speed = 3000f;
        float hue = (float) (System.currentTimeMillis() % delay) + (offset);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.HSBtoRGB(hue, 0.5F, 1F);
    }

    public static Color rainbow(long d, float brightness, float speed) {
        float hue = (float) (System.nanoTime() + (d * speed)) / 1.0E09F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, brightness, 1F)), 16);
        Color c = new Color((int) color);
        return new Color(c.getRed()/255.0F, c.getGreen()/255.0F, c.getBlue()/255.0F, c.getAlpha()/255.0F);
    }

    public static Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static void drawChromaString(GlyphPageFontRenderer fontRenderer, String text, float x, float y, boolean shadow) {
        float tmpX = x;
        for (char currentChar : text.toCharArray()) {
            long l = System.currentTimeMillis() - ((long) tmpX * 10 - (long) y * 10);
            int currentColor = Color.HSBtoRGB(l % (int) 2000.0F / 2000.0F, 0.8F, 0.8F);
            fontRenderer.drawString(String.valueOf(currentChar), tmpX, y, currentColor, shadow);
            tmpX += fontRenderer.getStringWidth(String.valueOf(currentChar));
        }
    }
}
