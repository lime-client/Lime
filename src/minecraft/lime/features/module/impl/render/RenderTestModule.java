package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.managers.FontManager;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.ColorValue;

import java.awt.*;

@ModuleData(name = "Render Test", category = Category.RENDER)
public class RenderTestModule extends Module {

    private final ColorValue colorPicker = new ColorValue("Color Picker", this, Color.GREEN.getRGB());

    @EventTarget
    public void on2D(Event2D e) {
        Lime.getInstance().getNotificationManager().getNotifications().clear();
        FontManager.ProductSans20.getFont().drawStringWithShadow("Salutation le khey", 3, 30, colorPicker.getColor());
    }
}
