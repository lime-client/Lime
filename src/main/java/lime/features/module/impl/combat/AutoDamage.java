package lime.features.module.impl.combat;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.EventMotion;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.utils.combat.CombatUtils;
import lime.utils.other.InventoryUtils;
import lime.utils.other.Timer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class AutoDamage extends Module {
    public AutoDamage() {
        super("Auto Damage", Category.COMBAT);
    }

    private final Timer timer = new Timer();
    private int ticks;

    @Override
    public void onEnable() {
        ticks = 0;
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        if(KillAura.getEntity() != null && mc.thePlayer.getDistanceToEntity(KillAura.getEntity()) < 3.5) {
            ticks++;
        } else {
            ticks = 0;
        }

        if(ticks < 3) {
            return;
        }
        AutoPot.ItemPot healthPotion = getDamagePotion();
        if(e.isPre() && !mc.thePlayer.capabilities.allowFlying) {
            if(healthPotion == null || !timer.hasReached(500)) {
                return;
            } else {
                EntityLivingBase target = KillAura.getEntity();
                if(target != null && Lime.getInstance().getModuleManager().getModuleC(KillAura.class).isToggled()) {
                    float[] rots = CombatUtils.getEntityRotations(target, false);
                    e.setYaw(rots[0]);
                    e.setPitch(rots[1]);
                }
            }
        }
        if(!e.isPre() && !mc.thePlayer.capabilities.allowFlying && KillAura.getEntity() != null && KillAura.getEntity().isEntityAlive() && Lime.getInstance().getModuleManager().getModuleC(KillAura.class).isToggled()) {
            if(healthPotion == null || !timer.hasReached(500))
                return;

            if(healthPotion.getSlot() < 36) {
                InventoryUtils.swap(healthPotion.getSlot(), 5);
                healthPotion.setSlot(41);
            }

            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(healthPotion.getSlot() - 36));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            timer.reset();
        }
    }

    public AutoPot.ItemPot getDamagePotion() {
        for(int i = 9; i < 45; ++i) {
            if(InventoryUtils.getSlot(i).getHasStack()) {
                ItemStack itemStack = InventoryUtils.getSlot(i).getStack();
                if(itemStack.getItem() instanceof ItemPotion) {
                    ItemPotion itemPotion = (ItemPotion) itemStack.getItem();

                    if(!ItemPotion.isSplash(itemStack.getItemDamage()))
                        continue;

                    for (PotionEffect effect : itemPotion.getEffects(itemStack)) {
                        if(effect.getPotionID() == Potion.harm.getId()) {
                            return new AutoPot.ItemPot(itemStack, i, false);
                        }
                    }
                }
            }
        }
        return null;
    }
}
