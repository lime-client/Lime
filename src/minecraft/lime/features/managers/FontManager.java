package lime.features.managers;

import lime.utils.render.fontRenderer.GlyphPageFontRenderer;

import java.awt.*;
import java.io.IOException;

public enum FontManager {
    ProductSans18("/assets/minecraft/lime/fonts/productsans.ttf", 18),
    ProductSans20("/assets/minecraft/lime/fonts/productsans.ttf", 20),
    Montserrat("/assets/minecraft/lime/fonts/montserrat.ttf", 20),
    SF("/assets/minecraft/lime/fonts/SFREGULAR.ttf", 20);

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
