package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.*;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.exploit.Disabler;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.EnumProperty;
import lime.features.setting.impl.NumberProperty;
import lime.ui.gui.ProcessBar;
import lime.utils.movement.MovementUtils;
import lime.utils.other.PlayerUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class HighJump extends Module {
    public HighJump() {
        super("High Jump", Category.MOVE);
    }

    private final EnumProperty mode = new EnumProperty("Mode", this, "Vanilla", "Vanilla", "Verus");
    private final NumberProperty motionY = new NumberProperty("Motion Y", this, 0.4, 3, 1, 0.1);
    private final BooleanProperty latestVerus = new BooleanProperty("Latest Verus", this, false).onlyIf(mode.getSettingName(), "enum", "Verus");
    private ProcessBar processBar;
    private boolean waiting, received;

    @Override
    public void onEnable() {
        ScaledResolution sr = new ScaledResolution(mc);
        processBar = new ProcessBar((sr.getScaledWidth() / 2) - 25, (sr.getScaledHeight() / 2) + 20, 1500);
        waiting = received = false;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        setSuffix(mode.getSelected());
        if(mode.is("verus")) {
            if(!processBar.getTimer().hasReached(1500)) {
                e.setCanceled(true);
            } else {
                if(!waiting) {
                    PlayerUtils.verusDamage(!latestVerus.isEnabled());
                    MovementUtils.vClip(.42);
                    waiting = true;
                }

                if(received && e.isPre()) {
                    boolean b = Lime.getInstance().getModuleManager().getModuleC(Disabler.class).isToggled() && ((Disabler) Lime.getInstance().getModuleManager().getModuleC(Disabler.class)).mode.is("Verus Transaction");
                    mc.thePlayer.motionY = b ? 5 : 0.84;
                    this.toggle();
                }
            }
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(mode.is("verus")) {
            if((waiting && !received) || !processBar.getTimer().hasReached(1500)) {
                e.setCanceled(true);
            }
        }
        if(mode.is("vanilla")) {
            if(MovementUtils.isOnGround(0.01) && mc.gameSettings.keyBindJump.isKeyDown()) {
                e.setY(mc.thePlayer.motionY = motionY.getCurrent());
            }
        }
    }

    @EventTarget
    public void onEntityAction(EventEntityAction e) {
        e.setShouldJump(false);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
            received = true;
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        if(mode.is("verus")) {
            processBar.draw();
        }
    }
}
