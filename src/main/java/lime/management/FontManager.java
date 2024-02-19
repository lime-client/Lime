package lime.management;

import lime.utils.render.fontRenderer.GlyphPageFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public enum FontManager {
    ProductSans18("lime/fonts/productsans.ttf", 18),
    ProductSans20("lime/fonts/productsans.ttf", 20),
    ProductSans24("lime/fonts/productsans.ttf", 24),
    ProductSans76("lime/fonts/productsans.ttf", 76);

    private GlyphPageFontRenderer font;
    FontManager(String path, int size, boolean bold, boolean italic, boolean boldItalic) {
        try {
            this.font = GlyphPageFontRenderer.create(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(path)).getInputStream(), size, bold, italic, boldItalic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    FontManager(String path, int size) {
        try {
            this.font = GlyphPageFontRenderer.create(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(path)).getInputStream(), size, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GlyphPageFontRenderer getFont() {
        return font;
    }
}