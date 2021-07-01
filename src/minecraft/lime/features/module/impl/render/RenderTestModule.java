package lime.features.module.impl.render;

import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.utils.render.animation.easings.Animate;
import lime.utils.render.animation.easings.Easing;
import lime.utils.time.DeltaTime;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

@ModuleData(name = "Render Test", category = Category.RENDER)
public class RenderTestModule extends Module {

    Animate animate;

    @Override
    public void onEnable() {
        animate = new Animate();
        animate.setEase(Easing.CUBIC_IN_OUT);
        animate.setMin(10);
        animate.setMax(new ScaledResolution(this.mc).getScaledWidth());
        animate.setSpeed(350);
        animate.setReversed(true);
    }

    @EventTarget
    public void onRender(Event2D e) {
        animate.update();
        Gui.drawRect(animate.getValue(), 5, animate.getValue() + 10, 10, -1);
        if(Mouse.isButtonDown(1))
            animate.reset();
    }
}
