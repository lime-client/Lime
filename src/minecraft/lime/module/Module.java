package lime.module;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.cgui.settings.Value;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class Module {
    public enum Category{
        COMBAT,MOVEMENT,PLAYER,RENDER,MISC
    }
    protected Minecraft mc = Minecraft.getMinecraft();
    public String name, displayName;
    public int key;
    public float anim = -1;
    public boolean binding = false;
    Category cat;
    public boolean toggled;
    public void disable(){
        if(isToggled()) toggle();
    }
    public float getAnim() {
        return anim;
    }

    public void setAnim(float anim) {
        this.anim = anim;
    }

    public Module(String name, int key, Category category){
        this.name = name;
        this.key = key;
        this.cat = category;
    }
    public void onEnable(){
        Lime.eventManager.register(this);
    }
    public void onDisable(){
        Lime.eventManager.unregister(this);
    }
    public void toggle(){
        toggled = !toggled;
        if (toggled)
            onEnable();
        else
            onDisable();
    }

    public String getName() {
        return name;
    }

    public Category getCat() {
        return cat;
    }

    public boolean isToggled() {
        return toggled;
    }

    public int getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setCat(Category cat) {
        this.cat = cat;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean hasSettings(){
        return Lime.setmgr.getSettingsByMod(this) != null;
    }
    public Setting getSettingByName(String n){
        return Lime.setmgr.getSettingByNameAndMod(n, this);
    }
}
