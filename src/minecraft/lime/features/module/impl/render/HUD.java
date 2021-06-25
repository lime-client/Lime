package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;

import lime.features.managers.FontManager;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(name = "HUD", category = Category.RENDER)
public class HUD extends Module {
    @EventTarget
    public void on2D(Event2D e) {
        for(int i = 0; i < 361; i++) {
            Gui.drawRect(5 + Math.toRadians(i), 5 + Math.toRadians(i), 5 + Math.toRadians(i) + 3, 5 + Math.toRadians(i) + 3, new Color(0, 255, 0).getRGB());
        }

        ArrayList<Module> modules = new ArrayList<>(Lime.getInstance().getModuleManager().getModules());

        modules.sort((o1, o2) -> {
            if(FontManager.ProductSans20.getFont().getStringWidth(o1.getName()) > FontManager.ProductSans20.getFont().getStringWidth(o2.getName()))
                return -1;
            else
                return 1;
        });
        int yCount = 0;
        for (Module module : modules) {
            if(module.isToggled()) {
                Gui.drawRect(e.getScaledResolution().getScaledWidth() - 1, yCount * 12, e.getScaledResolution().getScaledWidth(), yCount * 12 + 12, new Color(0, 255, 0).getRGB());
                FontManager.ProductSans20.getFont().drawStringWithShadow(module.getName(), (e.getScaledResolution().getScaledWidth() - FontManager.ProductSans20.getFont().getStringWidth(module.getName())) - 4, yCount * 12, new Color(0, 255, 0).getRGB());
                ++yCount;
            }
        }
    }
}
