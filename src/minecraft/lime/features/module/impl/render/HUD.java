package lime.features.module.impl.render;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventScoreboard;
import lime.core.events.impl.EventWorldChange;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.*;
import lime.utils.movement.MovementUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.render.ColorUtils;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import lime.utils.render.font2.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class HUD extends Module {

    public HUD() {
        super("HUD", Category.VISUALS);
    }

    private final TextValue clientName = new TextValue("Client Name", this, "Lime");
    public final EnumValue targetHud = new EnumValue("Target HUD", this, "Lime", "None", "Lime", "Lime2", "Astolfo");
    public final SlideValue targetHudX = new SlideValue("TargetHUD X", this, 0, 100, 50, 1).onlyIf(targetHud.getSettingName(), "enum", "lime", "lime2", "astolfo");
    public final SlideValue targetHudY = new SlideValue("TargetHUD Y", this, 0, 100, 50, 1).onlyIf(targetHud.getSettingName(), "enum", "lime", "lime2", "astolfo");
    private final EnumValue sidebar = new EnumValue("Sidebar", this, "Right", "Left", "Right", "None");
    private final EnumValue color = new EnumValue("Color", this, "Lime", "Lime", "Astolfo", "Rainbow", "Fade");
    private final ColorValue fadeColor = new ColorValue("Fade Color", this, new Color(200, 0, 0).getRGB()).onlyIf(color.getSettingName(), "enum", "fade");
    private final BoolValue customFont = new BoolValue("Custom Font", this, true);
    private final BoolValue suffix = new BoolValue("Suffix", this, true);
    private final BoolValue armorHud = new BoolValue("Armor HUD", this, true);
    private final BoolValue uid = new BoolValue("UID", this, true);
    private final BoolValue bps = new BoolValue("BP/S", this, true);
    private final BoolValue fps = new BoolValue("FPS", this, true);

    private final Animate scoreboardAnimation = new Animate();

    @EventTarget
    public void on2D(Event2D e) {
        ScaledResolution sr = new ScaledResolution(mc);
        scoreboardAnimation.setEase(Easing.LINEAR).setSpeed(125).setMin(0).update();

        if(customFont.isEnabled())
            FontManager.SfUiArray.drawStringWithShadow(clientName.getText(), 1, 1, -1);
        else
            mc.fontRendererObj.drawStringWithShadow(clientName.getText(), 1, 1, -1);

        if(fps.isEnabled()) {
            if(customFont.isEnabled()) {
                FontManager.SfUiArray.drawStringWithShadow("FPS: §f" + Minecraft.debugFPS, 1, sr.getScaledHeight() - (lime.management.FontManager.ProductSans20.getFont().getFontHeight() * (mc.currentScreen instanceof GuiChat ? 2 : 1))+2, HUD.getColor(0).getRGB());
            } else {
                mc.fontRendererObj.drawStringWithShadow("FPS: §f" + Minecraft.debugFPS, 3, sr.getScaledHeight() - (lime.management.FontManager.ProductSans20.getFont().getFontHeight() * (mc.currentScreen instanceof GuiChat ? 2 : 1))+1, HUD.getColor(0).getRGB());
            }
        }
        if(bps.isEnabled()) {
            if(customFont.isEnabled()) {
                FontManager.SfUiArray.drawStringWithShadow("BP/S: §f" + (Math.round(MovementUtils.getBPS() * 1000D) / 1000F), 1 + FontManager.SfUiArray.getStringWidth("FPS: " + Minecraft.debugFPS + " "), sr.getScaledHeight() - (lime.management.FontManager.ProductSans20.getFont().getFontHeight() * (mc.currentScreen instanceof GuiChat ? 2 : 1))+2, HUD.getColor(0).getRGB());
            } else {
                mc.fontRendererObj.drawStringWithShadow("BP/S: §f" + (Math.round(MovementUtils.getBPS() * 1000D) / 1000F), 3 + mc.fontRendererObj.getStringWidth("FPS: " + Minecraft.debugFPS + " "), sr.getScaledHeight() - (lime.management.FontManager.ProductSans20.getFont().getFontHeight() * (mc.currentScreen instanceof GuiChat ? 2 : 1))+1, HUD.getColor(0).getRGB());
            }
        }

        if(uid.isEnabled()) {
            if(customFont.isEnabled()) {
                FontManager.SfUiArray.drawStringWithShadow("User ID: §f" + Lime.getInstance().getUser().getUid(), sr.getScaledWidth() - 1 - FontManager.SfUiArray.getStringWidth("User ID: " + Lime.getInstance().getUser().getUid()), sr.getScaledHeight() - (lime.management.FontManager.ProductSans20.getFont().getFontHeight() * (mc.currentScreen instanceof GuiChat ? 2 : 1))+2, HUD.getColor(0).getRGB());
            } else {
                mc.fontRendererObj.drawStringWithShadow("User ID: §f" + Lime.getInstance().getUser().getUid(), sr.getScaledWidth() - 3 - mc.fontRendererObj.getStringWidth("User ID: " + Lime.getInstance().getUser().getUid()), sr.getScaledHeight() - (lime.management.FontManager.ProductSans20.getFont().getFontHeight() * (mc.currentScreen instanceof GuiChat ? 2 : 1))+1, HUD.getColor(0).getRGB());
            }
        }
        if(armorHud.isEnabled()) {
            int index = 0;
            int test = sr.getScaledWidth() / 2 + 75;
            for (int i = 8; i >= 5; i--) {
                if(InventoryUtils.getSlot(i).getHasStack()) {
                    ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                    RenderHelper.enableStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, test - (index * 16), sr.getScaledHeight() - 55);
                    index++;
                }
            }
        }

        ArrayList<Module> modules = new ArrayList<>(Lime.getInstance().getModuleManager().getModules());

        modules.sort((o1, o2) -> {
            String o1Name = o1.getName() + (suffix.isEnabled() && o1.getSuffix() != null && !o1.getSuffix().isEmpty() ? "§7 " + o1.getSuffix().replace("_", " ") : "");
            String o2Name = o2.getName() + (suffix.isEnabled() && o2.getSuffix() != null && !o2.getSuffix().isEmpty() ? "§7 " + o2.getSuffix().replace("_", " ") : "");
            if(customFont.isEnabled()) {
                if(FontManager.SfUiArray.getStringWidth(o1Name)  > FontManager.SfUiArray.getStringWidth(o2Name))
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
        int increment = customFont.isEnabled() ? FontManager.SfUiArray.getHeight() : mc.fontRendererObj.FONT_HEIGHT;
        int yCount = 0;
        for (Module module : modules) {
            if(module.hasSettings() && !((BoolValue) Lime.getInstance().getSettingsManager().getSetting("Show", module)).isEnabled()) continue;
            String moduleName = module.getName() + (suffix.isEnabled() && module.getSuffix() != null && !module.getSuffix().isEmpty() ? "§7 " + module.getSuffix().replace("_", " ") : "");

            // HUD Animation
            module.hudAnimation.setEase(Easing.SINE_OUT);
            module.hudAnimation.update();
            module.hudAnimation.setMax((customFont.isEnabled() ? FontManager.SfUiArray.getStringWidth(moduleName) : mc.fontRendererObj.getStringWidth(moduleName)) + 4);
            module.hudAnimation.setReversed(!module.isToggled());

            if(module.hudAnimation.getValue() > module.hudAnimation.getMin()) {
                Color color = getColor(yCount / increment);

                if(sidebar.is("right")) {
                    Gui.drawRect(e.getScaledResolution().getScaledWidth() - 1, yCount, e.getScaledResolution().getScaledWidth(), yCount + increment, color.getRGB());
                } else if(sidebar.is("left")) {
                    Gui.drawRect(e.getScaledResolution().getScaledWidth() - module.hudAnimation.getValue() - 1, yCount, e.getScaledResolution().getScaledWidth()  - module.hudAnimation.getValue(), yCount + increment, color.getRGB());
                }
                if(customFont.isEnabled())
                    FontManager.SfUiArray.drawStringWithShadow(moduleName, (e.getScaledResolution().getScaledWidth() - module.hudAnimation.getValue()) + 2, 2 + yCount, color.getRGB());
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
        int size = (int) Lime.getInstance().getModuleManager().getModules().stream().filter(module -> module.isToggled() && ((BoolValue) Lime.getInstance().getSettingsManager().getSetting("Show", module)).isEnabled()).count();
        if(scoreboardAnimation.getValue() > size * FontManager.SfUiArray.getHeight()) {
            scoreboardAnimation.setMin(size * FontManager.SfUiArray.getHeight());
            scoreboardAnimation.setReversed(true);
        } else {
            scoreboardAnimation.setReversed(false).setMax(size * FontManager.SfUiArray.getHeight());
        }
            e.setY(Math.max(((int) scoreboardAnimation.getValue()) - (new ScaledResolution(mc).getScaledHeight() / 2) + 100, e.getY()));
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
