package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;

import lime.core.events.impl.EventScoreboard;
import lime.core.events.impl.EventUpdate;
import lime.core.events.impl.EventWorldChange;
import lime.features.setting.impl.*;
import lime.managers.FontManager;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.movement.MovementUtils;
import lime.utils.render.ColorUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleData(name = "HUD", category = Category.RENDER)
public class HUD extends Module {

    private final TextValue clientName = new TextValue("Client Name", this, "Lime");
    public final EnumValue targetHud = new EnumValue("Target HUD", this, "Lime", "None", "Lime", "Astolfo");
    public final SlideValue targetHudX = new SlideValue("TargetHUD X", this, 0, 100, 50, 1).onlyIf(targetHud.getSettingName(), "enum", "lime", "astolfo");
    public final SlideValue targetHudY = new SlideValue("TargetHUD Y", this, 0, 100, 50, 1).onlyIf(targetHud.getSettingName(), "enum", "lime", "astolfo");
    private final EnumValue sidebar = new EnumValue("Sidebar", this, "Right", "Left", "Right", "None");
    private final EnumValue color = new EnumValue("Color", this, "Lime", "Lime", "Astolfo", "Rainbow", "Fade");
    private final ColorValue fadeColor = new ColorValue("Fade Color", this, new Color(200, 0, 0).getRGB()).onlyIf(color.getSettingName(), "enum", "fade");
    private final BoolValue customFont = new BoolValue("Custom Font", this, true);
    private final BoolValue suffix = new BoolValue("Suffix", this, true);
    private final BoolValue fps = new BoolValue("FPS", this, true);

    private final Animate scoreboardAnimation = new Animate();

    @EventTarget
    public void onUpdate(EventUpdate e) {

    }

    @EventTarget
    public void on2D(Event2D e) {
        ScaledResolution sr = new ScaledResolution(mc);
        scoreboardAnimation.setEase(Easing.LINEAR).setSpeed(125).setMin(0).update();

        FontManager.ProductSans20.getFont().drawStringWithShadow("BP/S: " + MovementUtils.getBPS(), 1, 15, -1);

        if(customFont.isEnabled())
            FontManager.ProductSans20.getFont().drawStringWithShadow(clientName.getText(), 1, 1, -1);
        else
            mc.fontRendererObj.drawStringWithShadow(clientName.getText(), 1, 1, -1);

        if(fps.isEnabled()) {
            if(customFont.isEnabled()) {
                FontManager.ProductSans20.getFont().drawStringWithShadow("FPS: §f" + Minecraft.debugFPS, 1, sr.getScaledHeight() - (FontManager.ProductSans20.getFont().getFontHeight() * 2), HUD.getColor(0).getRGB());
            } else {
                mc.fontRendererObj.drawStringWithShadow("FPS: §f" + Minecraft.debugFPS, 3, sr.getScaledHeight() - (FontManager.ProductSans20.getFont().getFontHeight() * 2)+1, HUD.getColor(0).getRGB());
            }
        }

        ArrayList<Module> modules = new ArrayList<>(Lime.getInstance().getModuleManager().getModules());

        modules.sort((o1, o2) -> {
            String o1Name = o1.getName() + (suffix.isEnabled() && o1.getSuffix() != null && !o1.getSuffix().isEmpty() ? "§7 " + o1.getSuffix().replace("_", " ") + (customFont.isEnabled() ? " " : "") : "");
            String o2Name = o2.getName() + (suffix.isEnabled() && o2.getSuffix() != null && !o2.getSuffix().isEmpty() ? "§7 " + o2.getSuffix().replace("_", " ") + (customFont.isEnabled() ? " " : "") : "");
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
            if(module.hasSettings() && !((BoolValue) Lime.getInstance().getSettingsManager().getSetting("Show", module)).isEnabled()) continue;
            String moduleName = module.getName() + (suffix.isEnabled() && module.getSuffix() != null && !module.getSuffix().isEmpty() ? "§7 " + module.getSuffix().replace("_", " ") + (customFont.isEnabled() ? " " : "") : "");

            // HUD Animation
            module.hudAnimation.setEase(Easing.SINE_OUT);
            module.hudAnimation.update();
            module.hudAnimation.setMax((customFont.isEnabled() ? FontManager.ProductSans18.getFont().getStringWidth(moduleName) : mc.fontRendererObj.getStringWidth(moduleName)) + 4);
            module.hudAnimation.setReversed(!module.isToggled());

            if(module.hudAnimation.getValue() > module.hudAnimation.getMin()) {
                Color color = getColor(yCount / increment);

                if(sidebar.is("right")) {
                    Gui.drawRect(e.getScaledResolution().getScaledWidth() - 1, yCount, e.getScaledResolution().getScaledWidth(), yCount + increment, color.getRGB());
                } else if(sidebar.is("left")) {
                    Gui.drawRect(e.getScaledResolution().getScaledWidth() - module.hudAnimation.getValue() - 1, yCount, e.getScaledResolution().getScaledWidth()  - module.hudAnimation.getValue(), yCount + increment, color.getRGB());
                }
                //Gui.drawRect(e.getScaledResolution().getScaledWidth() - 1 - module.hudAnimation.getValue(), yCount, e.getScaledResolution().getScaledWidth() - 1, yCount + increment, (0xCC * 200) << 24);
                if(customFont.isEnabled())
                    FontManager.ProductSans18.getFont().drawStringWithShadow(moduleName, (e.getScaledResolution().getScaledWidth() - module.hudAnimation.getValue()), yCount, color.getRGB());
                else
                    mc.fontRendererObj.drawString(moduleName, (e.getScaledResolution().getScaledWidth() - module.hudAnimation.getValue() + (sidebar.is("right") ? 0 : 3)), yCount + 1, color.getRGB(), true);

                yCount += Math.min(increment, module.hudAnimation.getValue() + 1);
            }
        }
    }

    @EventTarget
    public void onWorldChange(EventWorldChange e) {
        scoreboardAnimation.reset();
    }

    @EventTarget
    public void onScoreboard(EventScoreboard e) {
        int size = (int) Lime.getInstance().getModuleManager().getModules().stream().filter(Module::isToggled).count();
        if(scoreboardAnimation.getValue() > size * 12) {
            scoreboardAnimation.setMin(size * 12);
            scoreboardAnimation.setReversed(true);
        } else {
            scoreboardAnimation.setReversed(false).setMax(size * 12);
        }
            e.setY(((int) scoreboardAnimation.getValue()) - (new ScaledResolution(mc).getScaledHeight() / 2) + 100);
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
