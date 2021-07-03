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
