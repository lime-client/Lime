package lime.ui.gui;

import lime.core.Information;
import lime.core.Lime;
import lime.managers.FontManager;
import lime.ui.altmanager.AltLoginScreen;
import lime.ui.fields.ButtonField;
import lime.ui.proxymanager.ProxyManagerScreen;
import lime.utils.render.GLSLSandboxShader;
import lime.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.lang.reflect.Field;

public class MainScreen extends GuiScreen {
    private final long initTime;

    public MainScreen() {
        this.initTime = System.currentTimeMillis();
    }

    @Override
    public void initGui() {

        final Color color = new Color(90, 24, 184, 255);

        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Singleplayer", width / 2F - 75, height / 2F - 22 - 22, 150, 20, color, () -> {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Multiplayer", width / 2F - 75, height / 2F - 22, 150, 20, color, () -> {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Alt Manager", width / 2F - 75, height / 2F, 150, 20, color, () -> {
            mc.displayGuiScreen(new AltLoginScreen(this));
        }));

        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Proxy Manager", width / 2F - 75, height / 2F + 22, 150, 20, color, () -> {
            mc.displayGuiScreen(new ProxyManagerScreen(this));
        }));

        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Options", width / 2F - 75, height / 2F + 22 + 22, 150, 20, color, () -> {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Exit", width / 2F - 75, height / 2F + 44 + 22, 150, 20, color, () -> {
            mc.shutdown();
        }));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(Lime.getInstance().getUserCheckThread() == null || !Lime.getInstance().getUserCheckThread().isAlive() || !Lime.getInstance().getUser().getHwid().equalsIgnoreCase(Minecraft.getHardwareID()) || Lime.getInstance().getUserCheckThread().getLastTime() + /* interval */ Lime.getInstance().getInterval() + /* timeout */ Lime.getInstance().getTimeout() < System.currentTimeMillis() / 1000) {
            System.out.println("Please contact Wykt#0001 with the error code \"9M\"");
            Minecraft.getMinecraft().shutdown();
            Lime.getInstance().setUserCheckThread(null);
            Lime.getInstance().setUser(null);
            try {
                Field field = Lime.class.getDeclaredField("instance");
                field.setAccessible(true);
                field.set(Lime.getInstance(), null);
            } catch (Exception ignored) {}
        }


        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        Lime.getInstance().getShader().useShader(this.width + 935, this.height + 500, mouseX, mouseY, (System.currentTimeMillis() - initTime) / 1000F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        FontManager.ProductSans20.getFont().drawStringWithShadow("Made by " + Information.getAuthor(), width - (FontManager.ProductSans20.getFont().getStringWidth("Made by" + Information.getAuthor())) - 6, height - FontManager.ProductSans20.getFont().getFontHeight(), -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow("Lime " + Information.getVersion() + " | Build: " + Information.getBuild(), 1, height - (FontManager.ProductSans20.getFont().getFontHeight()), -1);

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        RenderUtils.prepareScissorBox((width / 2F - (FontManager.ProductSans76.getFont().getStringWidth("Lime")  / 2F)), this.height / 2F - 132, (width / 2F - (FontManager.ProductSans76.getFont().getStringWidth("Lime")  / 2F)) + (FontManager.ProductSans76.getFont().getStringWidth("Lime")), this.height / 2F - 132 + FontManager.ProductSans76.getFont().getFontHeight() - 5);
        FontManager.ProductSans76.getFont().drawStringWithShadow(EnumChatFormatting.DARK_PURPLE + "L§fime", (width / 2F - (FontManager.ProductSans76.getFont().getStringWidth("Lime")  / 2F)), this.height / 2F - 132, -1);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
