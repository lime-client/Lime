package lime.features.module.impl.player;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.other.ChatUtils;
import lime.utils.other.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public class MCF extends Module {
    public MCF() {
        super("MCF", Category.PLAYER);
    }

    private final Timer timer = new Timer();

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(timer.hasReached(500) && Mouse.isButtonDown(2) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            Entity entity = mc.objectMouseOver.entityHit;
            if(Lime.getInstance().getFriendManager().isFriend(entity)) {
                Lime.getInstance().getFriendManager().removeFriend(entity.getName());
                ChatUtils.sendMessage("Removed §a" + entity.getName() + " §7 as friend");
            } else {
                Lime.getInstance().getFriendManager().addFriend(entity.getName());
                ChatUtils.sendMessage("Added §a" + entity.getName() + "§7 as friend");
            }
            timer.reset();
        }
    }
}
