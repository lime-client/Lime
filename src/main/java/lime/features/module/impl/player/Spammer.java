package lime.features.module.impl.player;

import lime.core.events.EventTarget;
import lime.core.events.impl.EventUpdate;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.NumberProperty;
import lime.features.setting.impl.TextProperty;
import lime.utils.other.Timer;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.Random;

public class Spammer extends Module {
    public Spammer() {
        super("Spammer", Category.PLAYER);
    }

    private final TextProperty text = new TextProperty("Text", this, "Lime best clarinet");
    private final NumberProperty delay = new NumberProperty("Delay", this, 0.5, 5, 1.5, 0.5);
    private final NumberProperty randomCharsLength = new NumberProperty("Random Chars Length", this, 1, 25, 10, 1);
    private final BooleanProperty randomChars = new BooleanProperty("Random Chars", this, true);

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

        int size = 25;
        String s = "";

        while(s.length() < size) {
            s += chars[new Random().nextInt(chars.length)];
        }

        return s;
    }
}
