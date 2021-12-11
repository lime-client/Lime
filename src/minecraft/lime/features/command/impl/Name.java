package lime.features.command.impl;

import lime.features.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class Name extends Command {
    @Override
    public String getUsage() {
        return "Copy username";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] {"username", "name"};
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        GuiScreen.setClipboardString(Minecraft.getMinecraft().getSession().getUsername());
    }
}
