package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.SlideValue;
import lime.features.setting.impl.TextValue;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.Random;

public class Spammer extends Module {
    public Spammer() {
        super("Spammer", Category.PLAYER);
    }

    private final TextValue text = new TextValue("Text", this, "Lime best clarinet");
    private final SlideValue delay = new SlideValue("Delay", this, 0.5, 5, 1.5, 0.5);
    private final BoolValue randomChars = new BoolValue("Random Chars", this, true);

    private final Timer timer = new Timer();

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(timer.hasReached(((long) delay.getCurrent() * 1000))) {
            mc.getNetHandler().addToSendQueue(new C01PacketChatMessage(text.getText() + (randomChars.isEnabled() ? " - " + generateRandomChars() : "")));
            timer.reset();
        }
    }

    public String generateRandomChars() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

        int size = 10;
        String s = "";

        while(s.length() < size) {
            s += chars[new Random().nextInt(chars.length)];
        }

        return s;
    }
}
