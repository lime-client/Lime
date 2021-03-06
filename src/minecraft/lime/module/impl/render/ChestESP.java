package lime.module.impl.render;

import lime.events.EventTarget;
import lime.events.impl.Event2D;
import lime.module.Module;
import lime.utils.render.UtilGL;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class ChestESP extends Module {
    public ChestESP(){
        super("ChestESP", 0, Category.RENDER);
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
    public void on2D(Event2D e){
        for(TileEntity tileEntity : mc.theWorld.loadedTileEntityList){
            if(tileEntity instanceof TileEntityChest){
                TileEntityChest tec = (TileEntityChest) tileEntity;

            }
        }
    }
}
