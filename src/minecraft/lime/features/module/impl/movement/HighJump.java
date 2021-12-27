package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event2D;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.core.events.impl.EventPacket;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;

public class HighJump extends Module {
    public HighJump() {
        super("High Jump", Category.MOVE);
    }

    private final NumberProperty height = new NumberProperty("Height", this, 0.1, 8, 3, 0.1);
    private final EnumProperty mode = new EnumProperty("Mode", this, "Verus", "Verus", "Verus NoDamage");
    private final BooleanProperty latestVerus = new BooleanProperty("Latest Verus", this, false).onlyIf(mode.getSettingName(), "enum", "Verus");
    private ProcessBar processBar;
    private int ticks;
    private boolean waiting, received;

    @Override
    public void onEnable() {
        if (mode.is("verus")) {
            ScaledResolution sr = new ScaledResolution(mc);
            processBar = new ProcessBar((sr.getScaledWidth() / 2) - 25, (sr.getScaledHeight() / 2) + 20, 1500);
            waiting = received = false;
            ticks = 0;
        }
        if (mode.is("verus nodamage")) {
            mc.thePlayer.motionY = height.getCurrent();
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.motionY = 0;
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        setSuffix(mode.getSelected());
        if (mode.is("verus nodamage")) {
            mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0, 0.5f, 0));
            mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
        }
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
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if(e.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
            received = true;
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        processBar.draw();
    }
}
