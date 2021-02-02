package lime.managers;

import lime.fonts.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    public CFontRenderer comfortaa_hud = new CFontRenderer(getFont("comfortaa.ttf", 18), true, 8);
    public CFontRenderer comfortaa_slider = new CFontRenderer(getFont("comfortaa.ttf", 12), true, 8);
    public static Font getFont(String fontName, int size) {
        Font font = null;
        try {
            InputStream ex = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("lime/fonts/" + fontName)).getInputStream();
            font = Font.createFont(0, ex);
            font = font.deriveFont(0, (float) size);
        } catch (Exception var3) {
            var3.printStackTrace();
            System.err.println("Font couldn't be found. Using default font now");
            font = new Font("default", 0, size);
        }
        return font;
    }
}
