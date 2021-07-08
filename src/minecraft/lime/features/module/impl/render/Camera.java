package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.setting.impl.BoolValue;

@ModuleData(name = "Camera", category = Category.RENDER)
public class Camera extends Module {
    // No Hurt Cam : EntityRenderer
    // No Fire : ItemRenderer
    // No FOV : AbstractClientPlayer#getFovModifier
    public final BoolValue noHurtCam = new BoolValue("No Hurt Cam", this, true);
    public final BoolValue noFire = new BoolValue("No Fire", this, true);
    public final BoolValue noFov = new BoolValue("No FOV", this, true);
}
