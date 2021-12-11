package lime.management;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class TargetManager {
    private final List<String> entitiesName;

    public TargetManager() {
        this.entitiesName = new ArrayList<>();
    }

    public List<String> getEntitiesName() {
        return entitiesName;
    }

    public void addTarget(String s) {
        entitiesName.add(s);
    }

    public boolean shouldTarget(EntityLivingBase e) {
        return e instanceof EntityPlayer && entitiesName.contains(e.getName());
    }
}
