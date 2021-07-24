package lime.ui.targethud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public abstract class TargetHUD {
    protected final Minecraft mc;
    private final int width;
    private final int height;
    private final String name;

    public TargetHUD(int width, int height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;
        mc = Minecraft.getMinecraft();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public abstract void draw(EntityLivingBase target, float x, float y, int color);
}
