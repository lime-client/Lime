package lime.ui.realaltmanager.guis;

import lime.core.Lime;
import lime.management.FontManager;
import lime.ui.fields.ButtonField;
import lime.ui.realaltmanager.Alt;
import lime.ui.realaltmanager.AltLoginThread;
import lime.ui.realaltmanager.AltManager;
import lime.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class AltManagerScreen extends GuiScreen {
    private final AltManager altManager;
    private AltLoginThread lastThread;
    private Alt alt;
    private long initTime, index;

    public AltManagerScreen(AltManager altManager) {
        this.altManager = altManager;
    }

    @Override
    public void initGui() {
        initTime = System.currentTimeMillis();
        index = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        float width = sr.getScaledWidth() - ((sr.getScaledWidth() / 2F) + 202);
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Direct Login", (sr.getScaledWidth() / 2F) + 200, sr.getScaledHeight() - 190, width, 20, new Color(25, 25, 25), false, () -> {
            mc.displayGuiScreen(new DirectLoginScreen(this));
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Clear Alts", (sr.getScaledWidth() / 2F) + 200, sr.getScaledHeight() - 168, width, 20, new Color(25, 25, 25), false, () -> {
            this.altManager.getAlts().clear();
            this.altManager.saveAlts();
        }));
        this.customButtonList.add(new ButtonField(FontManager.ProductSans20.getFont(), "Random Alt", (sr.getScaledWidth() / 2F) + 200, sr.getScaledHeight() - 146, width, 20, new Color(25, 25, 25), false, () -> {
            Alt alt = Lime.getInstance().getAltManager().getRandomAlt();
            if(alt != null) {
                this.alt = alt;
                lastThread = new AltLoginThread(alt.getMail(), alt.getPassword());
                lastThread.start();
            }
        }));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(lastThread != null && !lastThread.isAlive() && lastThread.getStatus().contains("Premium") && alt.getName().equals(alt.getMail())) {
            for (Alt altManagerAlt : altManager.getAlts()) {
                if(alt.getMail().equals(altManagerAlt.getMail()) && alt.getName().equals(altManagerAlt.getName())) {
                    altManagerAlt.setName(mc.getSession().getUsername());
                }
            }
            altManager.saveAlts();
        }
        ScaledResolution sr = new ScaledResolution(this.mc);

        if(Mouse.hasWheel() && (altManager.getAlts().size() - 1) * 56D > sr.getScaledHeight())
        {
            int wheel = Mouse.getDWheel();
            if(wheel > 0) {
                index -= 30;
            } else if(wheel < 0) {
                index += 30;
            }
        }
        index = (int) Math.max(index, 0);
        index = Math.min((altManager.getAlts().size() - 1) * 56L, index);
        GuiScreen.drawRect(0, 0, mc.displayWidth, mc.displayHeight, new Color(21, 21, 21).getRGB());
        int i = 0;
        for (Alt alt : altManager.getAlts()) {
            Gui.drawRect(3, 3 + (i * 56D) - index, (sr.getScaledWidth() / 2F) + 100F, 3 + (i * 56D) + 52 - index, alt.isSelected() ? new Color(25, 25, 25).getRGB() : new Color(41, 41, 41).getRGB());
            RenderUtils.drawHollowBox(3, 3 + (i * 56L) - index, (sr.getScaledWidth() / 2F) + 100F, 3 + (i * 56L) + 52 - index, 1, new Color(25, 25, 25).getRGB());
            RenderUtils.drawFace(4, (int) (4 + (i * 56) - index), 51, 51, new ResourceLocation("textures/entity/steve.png"));
            FontManager.ProductSans24.getFont().drawStringWithShadow(alt.getName(), 58,  4 + (i * 56L) - index, -1);
            FontManager.ProductSans20.getFont().drawStringWithShadow(alt.getMail(), 58,  4 + (i * 56) + (FontManager.ProductSans20.getFont().getFontHeight() * 2F) - index, new Color(75, 75, 75).getRGB());
            FontManager.ProductSans20.getFont().drawStringWithShadow(replaceToPassword(alt.getPassword()), 58,  4 + (i * 56) + (FontManager.ProductSans20.getFont().getFontHeight() * 3F) - index, new Color(75, 75, 75).getRGB());
            ++i;
        }
        RenderUtils.drawRoundedRect((sr.getScaledWidth() / 2F) + 200, 3, sr.getScaledWidth() - ((sr.getScaledWidth() / 2F) + 202), sr.getScaledHeight() - 200, 5, new Color(41, 41, 41, 175).getRGB());
        float width = sr.getScaledWidth() - ((sr.getScaledWidth() / 2F) + 202);
        float x = (sr.getScaledWidth() / 2F) + 200;
        FontManager.ProductSans20.getFont().drawStringWithShadow("§7Status: " + (lastThread == null ? "Unknown" : lastThread.getStatus()), x + (width / 2F) - (FontManager.ProductSans20.getFont().getStringWidth("Status: " + (lastThread == null ? "Unknown" : lastThread.getStatus())) / 2F), 5, -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow("§7Current Username: §f" + mc.getSession().getUsername(), x + (width / 2F) - (FontManager.ProductSans20.getFont().getStringWidth("Current Username: " + mc.getSession().getUsername()) / 2F), 5 + FontManager.ProductSans20.getFont().getFontHeight(), -1);
        FontManager.ProductSans20.getFont().drawStringWithShadow("Empty nigger", x + (width / 2F) - (FontManager.ProductSans20.getFont().getStringWidth("Empty nigger") / 2F), 50 + FontManager.ProductSans20.getFont().getFontHeight(), -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int i = 0;
        final ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        for (Alt alt : altManager.getAlts()) {
            if(GuiScreen.hover(3, 3 + (i * 56) - (int) index, mouseX, mouseY, (scaledResolution.getScaledWidth() / 2) + 100, 52) && mouseButton == 0) {
                if(alt.isSelected()) {
                    this.alt = alt;
                    lastThread = connectToAlt(alt.getMail(), alt.getPassword());
                } else {
                    for (Alt altManagerAlt : altManager.getAlts()) {
                        altManagerAlt.setSelected(false);
                    }
                    alt.setSelected(true);
                }
            }
            ++i;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public AltLoginThread connectToAlt(String mail, String password) {
        AltLoginThread altLoginThread = new AltLoginThread(mail, password);
        altLoginThread.start();
        return altLoginThread;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) {
            mc.displayGuiScreen(altManager.getLastScreen());
            return;
        }
        if((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && keyCode == Keyboard.KEY_V) {
            if(GuiScreen.getClipboardString().contains("\n")) {
                for (String content : GuiScreen.getClipboardString().split("\n")) {
                    if(content.split(":").length == 2) {
                        altManager.addAlt(new Alt(content.split(":")[0], content.split(":")[1]));
                    }
                    if(content.split(":").length == 3) {
                        altManager.addAlt(new Alt(content.split(":")[0], content.split(":")[1], content.split(":")[2]));
                    }
                }
            } else {
                String content = GuiScreen.getClipboardString();
                if(content.split(":").length == 2) {
                    altManager.addAlt(new Alt(content.split(":")[0], content.split(":")[1], ""));
                }
                if(content.split(":").length == 3) {
                    altManager.addAlt(new Alt(content.split(":")[0], content.split(":")[1], content.split(":")[2]));
                }
            }
            altManager.saveAlts();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    private String replaceToPassword(String s) {
        String string = "";
        while(s.length() > string.length()) {
            string += "*";
        }
        return string;
    }
}
