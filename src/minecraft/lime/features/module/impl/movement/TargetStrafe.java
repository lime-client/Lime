package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.ModuleData;
import lime.features.module.impl.combat.KillAura;
import lime.features.setting.impl.BoolValue;
import lime.features.setting.impl.SlideValue;
import lime.utils.combat.CombatUtils;
import lime.utils.movement.MovementUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(name = "Target Strafe", category = Category.MOVEMENT)
public class TargetStrafe extends Module {

    private final SlideValue distance = new SlideValue("Distance", this, 0.5, 6, 2.5, 0.1);
    private final BoolValue spaceOnly = new BoolValue("Space Only", this, false);

    private static int direction;
    public static Vec3 indexPos;
    public static int index, arraySize;
    private boolean set, changeDir;

    private int voidTicks;

    @Override
    public void onEnable() {
        direction = 1;
        set = false;
    }

    @EventTarget
    public void on3D(Event3D e) {
        if(canMove()) {
            RenderUtils.drawRadius(KillAura.getEntity(), distance.getCurrent());
        }
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(!canMove()) {
            set = false;
            index = 0;
            indexPos = null;
            return;
        }

        if(mc.thePlayer.isCollidedHorizontally)
            direction = -direction;

        if(mc.gameSettings.keyBindLeft.isKeyDown())
            direction = 1;
        else if(mc.gameSettings.keyBindRight.isKeyDown())
            direction = -1;

        EntityLivingBase target = (EntityLivingBase) KillAura.getEntity();
        ArrayList<Vec3> posArrayList = new ArrayList<>();
        for (float rotation = 0; rotation < (3.141592f * 2.0); rotation += 3.141592f * 2.0f / 27f) {
            final Vec3 pos = new Vec3(distance.getCurrent() * Math.cos(rotation) + target.posX, target.posY, distance.getCurrent() * Math.sin(rotation) + target.posZ);
            posArrayList.add(pos);
        }

        arraySize = posArrayList.size();
        if(!set) {
            ArrayList<Vec3> posBuffer = new ArrayList<>(posArrayList);
            posBuffer.sort(Comparator.comparingDouble(vec3 -> mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord)));
            index = posArrayList.indexOf(posBuffer.get(0));
            set = true;
        } else {
            BlockPos blockPos = new BlockPos(posArrayList.get(index).xCoord, posArrayList.get(index).yCoord, posArrayList.get(index).zCoord);
            indexPos = new Vec3(blockPos.getX() + .5, posArrayList.get(index).yCoord, blockPos.getZ());

            if (!(!inVoid(indexPos) && mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY, indexPos.zCoord)).getBlock().getCollisionBoundingBox(mc.theWorld, new BlockPos(indexPos.xCoord, mc.thePlayer.posY, indexPos.zCoord), mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY, indexPos.zCoord))) == null && mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 1, indexPos.zCoord)).getBlock().getCollisionBoundingBox(mc.theWorld, new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 1, indexPos.zCoord), mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 1, indexPos.zCoord))) == null && mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 2, indexPos.zCoord)).getBlock().getCollisionBoundingBox(mc.theWorld, new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 2, indexPos.zCoord), mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 2, indexPos.zCoord))) == null)) {
                direction = -direction;
                if (direction == 1) {
                    if (index + 1 > posArrayList.size() - 1) index = 0;
                    else index++;
                } else {
                    if (index - 1 < 0) index = posArrayList.size() - 1;
                    else index--;
                }
            } else {
                if(mc.thePlayer.isCollidedHorizontally) {
                    if(!changeDir) {
                        direction = -direction;
                        changeIndex(posArrayList);
                        changeDir = true;
                    }
                } else
                    changeDir = false;

                if(mc.gameSettings.keyBindLeft.isKeyDown()) {
                    direction = 1;
                } else if(mc.gameSettings.keyBindRight.isKeyDown()) {
                    direction = -1;
                }

                if (mc.thePlayer.getDistance(indexPos.xCoord, mc.thePlayer.posY, indexPos.zCoord) <= mc.thePlayer.getDistance(mc.thePlayer.prevPosX, mc.thePlayer.prevPosY, mc.thePlayer.prevPosZ) * 2) {
                    changeIndex(posArrayList);
                }
            }
        }
    }

    private void changeIndex(final ArrayList<Vec3> posArrayList) {
        if (direction == 1) {
            if (index + 1 > posArrayList.size() - 1) index = 0;
            else index++;
        } else {
            if (index - 1 < 0) index = posArrayList.size() - 1;
            else index--;
        }
    }

    @EventTarget
    public void onMove(EventMove e) {
        if(indexPos != null) {
            setMoveSpeed(e, MovementUtils.getSpeed());
        }
    }

    public boolean inVoid() {
        for (int i = (int) Math.ceil(mc.thePlayer.posY); i >= 0; i--) {
            if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() != Blocks.air) {
                return false;
            }
        }
        return true;
    }

    public void setMoveSpeed(EventMove event, final double speed) {
        voidTicks++;
        if (KillAura.getEntity() != null) {
            if (inVoid() && voidTicks > 4) {
                voidTicks = 0;
                direction = -direction;
            }
        }
        boolean shouldStrafe = this.isToggled()&& TargetStrafe.indexPos != null && canMove() && !(!mc.gameSettings.keyBindJump.isKeyDown() && spaceOnly.isEnabled());
        double forward = shouldStrafe ? ((Math.abs(mc.thePlayer.movementInput.moveForward) > 0 || Math.abs(mc.thePlayer.movementInput.moveStrafe) > 0) ? 1 : 0) : mc.thePlayer.movementInput.moveForward;
        double strafe = shouldStrafe ? 0 : mc.thePlayer.movementInput.moveStrafe;
        float yaw = shouldStrafe ? CombatUtils.getRotationFromPosition(TargetStrafe.indexPos.xCoord, TargetStrafe.indexPos.zCoord) : mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
            event.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
        }
    }

    public static boolean canMove() {
        return KillAura.getEntity() != null;
    }

    private boolean inVoid(Vec3 vec3) {
        if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) return false;
        for (int i = (int) Math.ceil(vec3.yCoord); i >= 0; i--) {
            if (mc.theWorld.getBlockState(new BlockPos(vec3.xCoord, i, vec3.zCoord)).getBlock() != Blocks.air) {
                return false;
            }
        }
        return true;
    }
}
