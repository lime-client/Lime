package lime.module.impl.misc;

import lime.Lime;
import lime.events.EventTarget;
import lime.events.impl.EventMotion;
import lime.module.Module;
import lime.utils.ChatUtils;
import lime.utils.Timer;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public class Friends extends Module {
    public Friends(){
        super("Friends", 0, Category.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    Timer timer = new Timer();

    @EventTarget
    public void onMotion(EventMotion e){
        if(Mouse.isButtonDown(2) && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit != null){
            if(timer.hasReached(500)){
                if(!Lime.friendManager.isIn(mc.objectMouseOver.entityHit.getName())){
                    Lime.friendManager.addFriend(mc.objectMouseOver.entityHit.getName());
                    ChatUtils.sendMsg("Added " + mc.objectMouseOver.entityHit.getName() + " as friend.");
                } else {
                    Lime.friendManager.deleteFriend(mc.objectMouseOver.entityHit.getName());
                    ChatUtils.sendMsg("Removed " + mc.objectMouseOver.entityHit.getName() + " friend.");
                }
                timer.reset();
            }

        }
    }
}
