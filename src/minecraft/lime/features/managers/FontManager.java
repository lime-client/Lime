package lime.features.managers;

import lime.utils.render.fontRenderer.GlyphPageFontRenderer;

public enum FontManager {
    ProductSans18("/assets/minecraft/lime/fonts/productsans.ttf", 18),
    ProductSans20("/assets/minecraft/lime/fonts/productsans.ttf", 20);

    private GlyphPageFontRenderer font;
    FontManager(String path, int size, boolean bold, boolean italic, boolean boldItalic) {
        this.font = GlyphPageFontRenderer.create(path, size, bold, italic, boldItalic);
    }

    FontManager(String path, int size) {
        this.font = GlyphPageFontRenderer.create(path, size, false, false, false);
    }

    public GlyphPageFontRenderer getFont() {
        return font;
    }
}
