package net.minecraft.client.gui;

import lime.core.Lime;
import lime.ui.altmanager.Alt;
import lime.ui.altmanager.AltLoginThread;
import lime.ui.altmanager.guis.AltManagerScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GuiDisconnected extends GuiScreen
{
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private AltLoginThread altLoginThread;
    private int field_175353_i;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp)
    {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 22, "Reconnect"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 44, "Alt Manager"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 66, 100, 20, "Random Alt"));
        this.buttonList.add(new GuiButton(4, this.width / 2, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 66, 100, 20, "Random Cracked Alt"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 88, "Clipboard"));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if(button.id == 1)
        {
            mc.displayGuiScreen(new GuiConnecting(parentScreen, mc, GuiConnecting.lastKnownServerAddress.getIP(), GuiConnecting.lastKnownServerAddress.getPort()));
        }
        if(button.id == 2)
        {
            mc.displayGuiScreen(Lime.getInstance().getAltManager().getAltManagerScreen());
        }
        if(button.id == 3)
        {
            List<Alt> alts = new ArrayList<>(Lime.getInstance().getAltManager().getAlts()).stream().filter((a -> a.getName().contains("@"))).collect(Collectors.toList());
            Alt alt;
            if(alts.isEmpty()) {
                alt = Lime.getInstance().getAltManager().getRandomAlt();
            } else {
                alt = alts.get(new Random().nextInt(alts.size()));
            }

            if(alt != null) {
                altLoginThread = new AltLoginThread(alt.getMail(), alt.getPassword(), false);
                altLoginThread.start();

                new Thread(() -> {
                    while(altLoginThread.isAlive()){}
                    alt.setName(mc.session.getUsername());
                }).start();
            }
        }
        if(button.id == 4)
        {
            altLoginThread = new AltLoginThread(AltManagerScreen.generateRandomString(), "", false);
            altLoginThread.start();
        }
        if(button.id == 5) {
            String s = GuiScreen.getClipboardString();
            if(s.contains("@") && s.contains(":") && s.contains(".")) {
                altLoginThread = new AltLoginThread(s.split(":")[0], s.split(":")[1], false);
                altLoginThread.start();
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        if(altLoginThread != null) {
            this.drawCenteredString(this.fontRendererObj, this.altLoginThread.getStatus(), this.width / 2, 5, Color.GRAY.getRGB());
        }
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null)
        {
            for (String s : this.multilineMessage)
            {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.FONT_HEIGHT;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
