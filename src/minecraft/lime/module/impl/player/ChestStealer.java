package lime.module.impl.player;

import lime.Lime;
import lime.settings.Setting;
import lime.settings.impl.BooleanValue;
import lime.settings.impl.ComboBooleanValue;
import lime.events.EventTarget;
import lime.events.impl.EventDeath;
import lime.events.impl.EventMotion;
import lime.events.impl.EventWorldChange;
import lime.module.Module;
import lime.utils.Timer;
import net.minecraft.inventory.ContainerChest;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Random;

public class ChestStealer extends Module {
    ComboBooleanValue disableon = new ComboBooleanValue("Disable on", this);
    BooleanValue onDeath = new BooleanValue("Death", this, true, disableon.getSet());
    BooleanValue onWorldChange = new BooleanValue("Changing World", this, true, disableon.getSet());
    public ChestStealer(){
        super("ChestSteal", Keyboard.KEY_I, Category.PLAYER);
        Lime.setmgr.rSetting(new Setting("Delay", this, 150, 10, 1000, true));
        Lime.setmgr.rSetting(new Setting("Random Delay", this, true));
        Lime.setmgr.rSetting(new Setting("Randomize", this, true));
    }



    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onWorldChange(EventWorldChange e){
        if(onWorldChange.getValue())
            this.disable();
    }

    @EventTarget
    public void onDeath(EventDeath e){
        if(onDeath.getValue())
            this.disable();
    }

    Timer timer = new Timer();

    @EventTarget
    public void onMotion(EventMotion eventMotion){
        setSuffix(getSettingByName("Delay").getValDouble() + "");
        if(mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest){
            ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
            String name = chest.getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase();
            String[] list = new String[] {"menu", "selector", "game", "server", "inventory", "cancel", "buy", "trade", "compass", "profile", "friends", "select", "map", "armor", "user", "teleporter", "upgrade", "lobby",
                    "vault", "utility", "potions", "anticheat", "travel", "settings", "preference", "warp", "sure", "tool", "team", "play", "accept", "soul", "book", "recipe", "skywars", "cakewars", "bedwars", "wars",
                    "profile", "lang", "english", "jeux", "équipe", "boutique", "amis", "paramètres", "team", "paramètre", "pvp", "duel", "skyblock", "cubelets", "préférences", "lottery", "echanges", "échanges"};
            for(String str : list){
                if(name.toLowerCase().contains(str.toLowerCase())) return;
            }
            int delay = (int) Lime.setmgr.getSettingByName("Delay").getValDouble() + (Lime.setmgr.getSettingByName("Random Delay").getValBoolean() ? (RandomUtils.nextInt(0, 1) == 0 ? RandomUtils.nextInt(0, 125) : -RandomUtils.nextInt(0, 125)) : 0);
            if(isChestEmpty(chest))
                mc.thePlayer.closeScreen();
            if(!getSettingByName("Randomize").getValBoolean()){
                for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++){
                    if(chest.getLowerChestInventory().getStackInSlot(i) != null){
                        if(chest.getLowerChestInventory().getStackInSlot(i) != null)
                            if(timer.hasReached(delay)){
                                mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                                timer.reset();
                            }
                    }
                }
            } else {
                ArrayList<Integer> ischest = new ArrayList<>();
                for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++){
                    if(chest.getLowerChestInventory().getStackInSlot(i) != null)
                        ischest.add(i);
                }
                Random rand = new Random();
                for(int inter = 0; inter < ischest.size(); inter++){
                    Integer is = ischest.get(rand.nextInt(ischest.size()));
                    if(chest.getLowerChestInventory().getStackInSlot(is) != null)
                        if(timer.hasReached(delay)) {
                            mc.playerController.windowClick(chest.windowId, is, 0, 1, mc.thePlayer);
                            try { ischest.remove(is); } catch (Exception ignored) { ; }
                            timer.reset();
                        }
                }
            }
        }
    }
    public boolean isChestEmpty(ContainerChest chest) {
        for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); ++i) {
            if (chest.getLowerChestInventory().getStackInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }
}
