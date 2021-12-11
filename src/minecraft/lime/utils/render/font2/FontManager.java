package lime.utils.render.font2;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public abstract class FontManager {
    public static CFontRenderer SfUiArray = new CFontRenderer(FontManager.getPS(), true, true);

    private static Font getPS() {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("lime/fonts/productsans.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, 20);
        } catch (Exception ex) {
            ex.printStackTrace();
            font = new Font("default", Font.PLAIN, 20);
        }
        return font;
    }
}
