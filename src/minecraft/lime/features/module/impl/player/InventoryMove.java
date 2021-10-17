package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InventoryMove extends Module {
    public InventoryMove() {
        super("Inventory Move", Category.PLAYER);
    }
    private final BoolValue sneak = new BoolValue("Sneak", this, false);

    private final KeyBinding[] keyBindings = new KeyBinding[] {
            mc.gameSettings.keyBindJump,
            null,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindSprint
    };

    @Override
    public void onDisable() {
        if(mc.currentScreen != null) {
            for (KeyBinding keyBinding : keyBindings) {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        keyBindings[1] = sneak.isEnabled() ? mc.gameSettings.keyBindSneak : null;
        if(mc.currentScreen == null || (mc.currentScreen instanceof GuiChat)) return;
        for (KeyBinding keyBinding : keyBindings) {
            if(keyBinding != null) {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), Keyboard.isKeyDown(keyBinding.getKeyCode()));
            }
        }
    }
}
