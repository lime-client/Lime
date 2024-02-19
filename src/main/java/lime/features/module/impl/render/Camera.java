package lime.features.module.impl.render;

import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;

public class Camera extends Module {

    public Camera() {
        super("Camera", Category.VISUALS);
    }

    // No Hurt Cam : EntityRenderer
    // No Fire : ItemRenderer
    // No FOV : AbstractClientPlayer#getFovModifier
    public final BooleanProperty noHurtCam = new BooleanProperty("No Hurt Cam", this, true);
    public final BooleanProperty noFire = new BooleanProperty("No Fire", this, true);
    public final BooleanProperty noFov = new BooleanProperty("No FOV", this, true);
}
