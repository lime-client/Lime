package lime.module.impl.player;

import lime.events.EventTarget;
import lime.events.impl.EventUpdate;
import lime.module.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InvMove extends Module {
    public InvMove(){
        super("InvMove", 0, Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    @EventTarget
    public void onUpdate(EventUpdate e){
        if (mc.currentScreen instanceof GuiChat) { return; }
        if (mc.currentScreen != null) {
            KeyBinding[] moveKeys = new KeyBinding[]{
                    mc.gameSettings.keyBindForward,
                    mc.gameSettings.keyBindBack,
                    mc.gameSettings.keyBindLeft,
                    mc.gameSettings.keyBindRight,
                    mc.gameSettings.keyBindJump,
                    mc.gameSettings.keyBindSneak,
                    mc.gameSettings.keyBindSprint
            };
            for (KeyBinding bind : moveKeys) {
                KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
            }
        }
    }
}
