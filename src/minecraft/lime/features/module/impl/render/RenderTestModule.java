package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.managers.FontManager;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.ColorValue;
import lime.utils.other.PlayerUtils;

import java.awt.*;

@ModuleData(name = "Render Test", category = Category.RENDER)
public class RenderTestModule extends Module {

    @Override
    public void onEnable() {
        PlayerUtils.verusDamage();
        mc.thePlayer.jump();
    }
}
