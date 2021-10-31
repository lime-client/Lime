package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventCrosshair;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.ColorProperty;
import lime.features.setting.impl.NumberProperty;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Crosshair extends Module {
    public Crosshair() {
        super("Crosshair", Category.VISUALS);
    }

    private final NumberProperty thickness = new NumberProperty("Thickness", this, 0.5, 4, 1, 0.5);
    private final NumberProperty length = new NumberProperty("Length", this, 0.5, 10, 3, 0.5);
    private final NumberProperty gap = new NumberProperty("Gap", this, 0.5, 4, 3, 0.5);
    private final BooleanProperty outline = new BooleanProperty("Outline", this, true);
    private final NumberProperty outlineThickness = new NumberProperty("Outline Thickness", this, 0.5, 4, 0.5, 0.5);
    private final BooleanProperty dot = new BooleanProperty("Dot", this, true);
    private final ColorProperty color = new ColorProperty("Color", this, new Color(255, 255, 255).getRGB());

    @EventTarget
    public void onCrosshairEvent(EventCrosshair e) {
        GL11.glPushMatrix();
        e.setCanceled(true);
        ScaledResolution sr = new ScaledResolution(mc);
        double thickness = this.thickness.getCurrent() / 2.0D;
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        float middleX = (float)width / 2.0F;
        float middleY = (float)height / 2.0F;
        if (dot.isEnabled()) {
            Gui.drawRect(middleX - thickness - outlineThickness.getCurrent(), middleY - thickness - outlineThickness.getCurrent(), middleX + thickness + outlineThickness.getCurrent(), middleY + thickness + outlineThickness.getCurrent(), Color.BLACK.getRGB());
            Gui.drawRect(middleX - thickness, middleY - thickness, middleX + thickness, middleY + thickness, color.getColor());
        }

        Gui.drawRect(middleX - thickness - outlineThickness.getCurrent(), middleY - gap.getCurrent() - length.getCurrent() - outlineThickness.getCurrent(), middleX + thickness + outlineThickness.getCurrent(), middleY - gap.getCurrent() + outlineThickness.getCurrent(), Color.BLACK.getRGB());
        Gui.drawRect(middleX - thickness, middleY - gap.getCurrent() - length.getCurrent(), middleX + thickness, middleY - gap.getCurrent(), color.getColor());
        Gui.drawRect(middleX - gap.getCurrent() - length.getCurrent() - outlineThickness.getCurrent(), middleY - thickness - outlineThickness.getCurrent(), middleX - gap.getCurrent() + outlineThickness.getCurrent(), middleY + thickness + outlineThickness.getCurrent(), Color.BLACK.getRGB());
        Gui.drawRect(middleX - gap.getCurrent() - length.getCurrent(), middleY - thickness, middleX - gap.getCurrent(), middleY + thickness, color.getColor());
        Gui.drawRect(middleX - thickness - outlineThickness.getCurrent(), middleY + gap.getCurrent() - outlineThickness.getCurrent(), middleX + thickness + outlineThickness.getCurrent(), middleY + gap.getCurrent() + length.getCurrent() + outlineThickness.getCurrent(), Color.BLACK.getRGB());
        Gui.drawRect(middleX - thickness, middleY + gap.getCurrent(), middleX + thickness, middleY + gap.getCurrent() + length.getCurrent(), color.getColor());
        Gui.drawRect(middleX + gap.getCurrent() - outlineThickness.getCurrent(), middleY - thickness - outlineThickness.getCurrent(), middleX + gap.getCurrent() + length.getCurrent() + outlineThickness.getCurrent(), middleY + thickness + outlineThickness.getCurrent(), Color.BLACK.getRGB());
        Gui.drawRect(middleX + gap.getCurrent(), middleY - thickness, middleX + gap.getCurrent() + length.getCurrent(), middleY + thickness, color.getColor());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GL11.glPopMatrix();
    }
}
