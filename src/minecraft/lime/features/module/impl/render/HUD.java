package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;

import lime.core.events.impl.EventScoreboard;
import lime.features.managers.FontManager;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.render.ColorUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(name = "HUD", category = Category.RENDER)
public class HUD extends Module {
    @EventTarget
    public void on2D(Event2D e) {
        for(int i = 0; i < 361; i++) {
            Color color = ColorUtils.blend2colors(new Color(0, 255, 0), new Color(0, 255, 0).darker(), (System.nanoTime() + (0 * 100000000L * 2)) / 1.0E09F % 2.0F);
            Gui.drawRect(5 + Math.toRadians(i), 5 + Math.toRadians(i), 5 + Math.toRadians(i) + 3, 5 + Math.toRadians(i) + 3, color.getRGB());
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
                Color color = ColorUtils.blend2colors(new Color(0, 255, 0), new Color(0, 255, 0).darker(), (System.nanoTime() + (yCount + yCount * 100000000L * 2)) / 1.0E09F % 2.0F);

                Gui.drawRect(e.getScaledResolution().getScaledWidth() - 1, yCount * 12, e.getScaledResolution().getScaledWidth(), yCount * 12 + 12, color.getRGB());
                //FontManager.ProductSans20.getFont().drawStringWithShadow(module.getName(), (e.getScaledResolution().getScaledWidth() - FontManager.ProductSans20.getFont().getStringWidth(module.getName())) - 4, yCount * 12, new Color(0, 255, 0).getRGB());
                FontManager.ProductSans20.getFont().drawStringWithShadow(module.getName(), (e.getScaledResolution().getScaledWidth() - FontManager.ProductSans20.getFont().getStringWidth(module.getName())) - 4, yCount * 12, color.getRGB());
                ++yCount;
            }
        }
    }

    @EventTarget
    public void onScoreboard(EventScoreboard e) {
        int size = (int) Lime.getInstance().getModuleManager().getModules().stream().filter(Module::isToggled).count();
        e.setY((size * 12) - (new ScaledResolution(mc).getScaledHeight() / 2) + 100);
    }
}
