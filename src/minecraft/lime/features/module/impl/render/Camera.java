package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;

public class Camera extends Module {

    public Camera() {
        super("Camera", Category.RENDER);
    }

    // No Hurt Cam : EntityRenderer
    // No Fire : ItemRenderer
    // No FOV : AbstractClientPlayer#getFovModifier
    public final BoolValue noHurtCam = new BoolValue("No Hurt Cam", this, true);
    public final BoolValue noFire = new BoolValue("No Fire", this, true);
    public final BoolValue noFov = new BoolValue("No FOV", this, true);
}
