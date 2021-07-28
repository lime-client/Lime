package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;

import lime.core.events.impl.EventScoreboard;
import lime.core.events.impl.EventUpdate;
import lime.features.setting.impl.*;
import lime.managers.FontManager;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.render.ColorUtils;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleData(name = "HUD", category = Category.RENDER)
public class HUD extends Module {

    private enum ColorMode {
        Lime, Astolfo, Rainbow, Fade
    }

    private enum TargetHUD {
        NONE, LIME
    }

    private final TextValue clientName = new TextValue("Client Name", this, "Lime");
    public final EnumValue targetHud = new EnumValue("Target HUD", this, "Lime", "None", "Lime");
    public final SlideValue targetHudX = new SlideValue("TargetHUD X", this, 0, 100, 50, 1).onlyIf(targetHud.getSettingName(), "enum", "lime");
    public final SlideValue targetHudY = new SlideValue("TargetHUD Y", this, 0, 100, 50, 1).onlyIf(targetHud.getSettingName(), "enum", "lime");
    private final EnumValue color = new EnumValue("Color", this, "Lime", "Lime", "Astolfo", "Rainbow", "Fade");
    private final ColorValue fadeColor = new ColorValue("Fade Color", this, new Color(200, 0, 0).getRGB()).onlyIf(color.getSettingName(), "enum", "fade");
    private final BoolValue customFont = new BoolValue("Custom Font", this, true);
    private final BoolValue suffix = new BoolValue("Suffix", this, true);

    @EventTarget
    public void onUpdate(EventUpdate e) {

    }

    @EventTarget
    public void on2D(Event2D e) {
        /*for(int i = 0; i < 361; i++) {
            Color color = getColor(0);
            Gui.drawRect(5 + Math.toRadians(i), 5 + Math.toRadians(i), 5 + Math.toRadians(i) + 3, 5 + Math.toRadians(i) + 3, color.getRGB());
        }*/

        if(customFont.isEnabled())
            FontManager.ProductSans20.getFont().drawStringWithShadow(clientName.getText(), 3, 3, -1);
        else
            mc.fontRendererObj.drawStringWithShadow(clientName.getText(), 3, 3, -1);

        ArrayList<Module> modules = new ArrayList<>(Lime.getInstance().getModuleManager().getModules());

        modules.sort((o1, o2) -> {
            String o1Name = o1.getName() + (suffix.isEnabled() && o1.getSuffix() != null && !o1.getSuffix().isEmpty() ? "§7 " + o1.getSuffix() + (customFont.isEnabled() ? " " : "") : "");
            String o2Name = o2.getName() + (suffix.isEnabled() && o2.getSuffix() != null && !o2.getSuffix().isEmpty() ? "§7 " + o2.getSuffix() + (customFont.isEnabled() ? " " : "") : "");
            if(customFont.isEnabled()) {
                if(FontManager.ProductSans18.getFont().getStringWidth(o1Name)  > FontManager.ProductSans18.getFont().getStringWidth(o2Name))
                    return -1;
                else
                    return 1;
            } else {
                if(mc.fontRendererObj.getStringWidth(o1Name) > mc.fontRendererObj.getStringWidth(o2Name))
                    return -1;
                else
                    return 1;
            }
        });
        int increment = customFont.isEnabled() ? FontManager.ProductSans18.getFont().getFontHeight() : mc.fontRendererObj.FONT_HEIGHT;
        int yCount = 0;
        for (Module module : modules) {
            String moduleName = module.getName() + (suffix.isEnabled() && module.getSuffix() != null && !module.getSuffix().isEmpty() ? "§7 " + module.getSuffix() + (customFont.isEnabled() ? " " : "") : "");

            // HUD Animation
            module.hudAnimation.setEase(Easing.SINE_OUT);
            module.hudAnimation.update();
            module.hudAnimation.setMax((customFont.isEnabled() ? FontManager.ProductSans18.getFont().getStringWidth(moduleName) : mc.fontRendererObj.getStringWidth(moduleName)) + 4);
            module.hudAnimation.setReversed(!module.isToggled());

            if(module.hudAnimation.getValue() > module.hudAnimation.getMin()) {
                Color color = getColor(yCount / increment);

                Gui.drawRect(e.getScaledResolution().getScaledWidth() - 1, yCount, e.getScaledResolution().getScaledWidth(), yCount + increment, color.getRGB());
                if(customFont.isEnabled())
                    FontManager.ProductSans18.getFont().drawStringWithShadow(moduleName, (e.getScaledResolution().getScaledWidth() - module.hudAnimation.getValue()), yCount, color.getRGB());
                else
                    mc.fontRendererObj.drawString(moduleName, (e.getScaledResolution().getScaledWidth() - module.hudAnimation.getValue()), yCount + 2, color.getRGB(), true);

                yCount += Math.min(increment, module.hudAnimation.getValue() + 1);
            }
        }
    }

    @EventTarget
    public void onScoreboard(EventScoreboard e) {
        int size = (int) Lime.getInstance().getModuleManager().getModules().stream().filter(Module::isToggled).count();
        e.setY((size * 12) - (new ScaledResolution(mc).getScaledHeight() / 2) + 100);
    }

    public static Color getColor(int index) {
        HUD hud = (HUD) Lime.getInstance().getModuleManager().getModuleC(HUD.class);
        if(hud.color.is("lime")) {
            return ColorUtils.blend2colors(new Color(75, 75, 75), new Color(200, 200, 200).darker(), (System.nanoTime() + (index + index * 100000000L * 2)) / 1.0E09F % 2.0F);
        }
        if(hud.color.is("astolfo")) {
            return new Color(ColorUtils.getAstolfo(3000, index * (17 * 4)));
        }

        if(hud.color.is("rainbow")) {
            return ColorUtils.rainbow(index + index * 70000000L, 0.7F, 1);
        }

        if(hud.color.is("fade"))
        {
            AtomicInteger count = new AtomicInteger();
            Lime.getInstance().getModuleManager().getModules().forEach(module ->
            {
                if(module.hudAnimation.getValue() > 0)
                    count.incrementAndGet();
            });
            return ColorUtils.fade(new Color(hud.fadeColor.getColor()), index, count.get());
        }

        // wtf ?
        return Color.BLACK;
    }
}
