package lime.ui.realaltmanager.guis;

import lime.managers.FontManager;
import lime.ui.realaltmanager.Alt;
import lime.ui.realaltmanager.AltManager;
import lime.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;

public class AltManagerScreen extends GuiScreen {
    private final AltManager altManager;

    private long initTime;

    public AltManagerScreen(AltManager altManager) {
        this.altManager = altManager;
    }

    @Override
    public void initGui() {
        initTime = System.currentTimeMillis();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        Minecraft.getMinecraft().getShader().useShader(this.width + 935, this.height + 500, mouseX, mouseY, (System.currentTimeMillis() - initTime) / 1000F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int i = 0;
        ScaledResolution sr = new ScaledResolution(this.mc);
        for (Alt alt : altManager.getAlts()) {
            Gui.drawRect(3, 3 + (i * 56), (sr.getScaledWidth() / 2F) + 100F, 3 + (i * 56) + 52, new Color(41, 41, 41).getRGB());
            RenderUtils.drawHollowBox(3, 3 + (i * 56), (sr.getScaledWidth() / 2F) + 100F, 3 + (i * 56) + 52, 1, new Color(25, 25, 25).getRGB());
            RenderUtils.drawFace(4, 4 + (i * 56), 51, 51, new ResourceLocation("textures/entity/steve.png"));
            FontManager.ProductSans24.getFont().drawStringWithShadow(alt.getName().isEmpty() ? alt.getMail() : alt.getName(), 58,  4 + (i * 56), -1);
            FontManager.ProductSans20.getFont().drawStringWithShadow(alt.getName().isEmpty() ? alt.getMail() : alt.getName(), 58,  4 + (i * 56) + (FontManager.ProductSans20.getFont().getFontHeight() * 2), new Color(75, 75, 75).getRGB());
            FontManager.ProductSans20.getFont().drawStringWithShadow(replaceToPassword(alt.getPassword()), 58,  4 + (i * 56) + (FontManager.ProductSans20.getFont().getFontHeight() * 3), new Color(75, 75, 75).getRGB());
            ++i;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String replaceToPassword(String s) {
        String string = "";
        while(s.length() > string.length()) {
            string += "*";
        }
        return string;
    }
}
