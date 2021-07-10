package lime.managers;

import lime.utils.render.fontRenderer.GlyphPageFontRenderer;

import java.awt.*;
import java.io.IOException;

public enum FontManager {
    ProductSans18("/assets/minecraft/lime/fonts/productsans.ttf", 18),
    ProductSans20("/assets/minecraft/lime/fonts/productsans.ttf", 20),
    ProductSans24("/assets/minecraft/lime/fonts/productsans.ttf", 24),
    ProductSans76("/assets/minecraft/lime/fonts/productsans.ttf", 76);

    private GlyphPageFontRenderer font;
    FontManager(String path, int size, boolean bold, boolean italic, boolean boldItalic) {
        try {
            this.font = GlyphPageFontRenderer.create(this.getClass().getResourceAsStream(path), size, bold, italic, boldItalic);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
    }

    FontManager(String path, int size) {
        try {
            this.font = GlyphPageFontRenderer.create(this.getClass().getResourceAsStream(path), size, false, false, false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
    }

    public GlyphPageFontRenderer getFont() {
        return font;
    }
}
