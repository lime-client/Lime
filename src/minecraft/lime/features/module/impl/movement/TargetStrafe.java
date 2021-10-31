package lime.features.module.impl.movement;

import lime.core.Lime;
import lime.core.events.EventTarget;
import lime.core.events.impl.Event3D;
import lime.core.events.impl.EventEntityAction;
import lime.core.events.impl.EventMotion;
import lime.core.events.impl.EventMove;
import lime.features.module.Category;
import lime.features.module.Module;
import lime.features.module.impl.combat.KillAura;
import lime.features.module.impl.render.HUD;
import lime.features.setting.impl.BooleanProperty;
import lime.features.setting.impl.NumberProperty;
import lime.utils.combat.CombatUtils;
import lime.utils.movement.MovementUtils;
import lime.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;

public class TargetStrafe extends Module {

    public TargetStrafe() {
        super("Target Strafe", Category.MOVE);
    }

    private final NumberProperty distance = new NumberProperty("Distance", this, 0.5, 6, 2.5, 0.1);
    private final NumberProperty sides = new NumberProperty("Sides", this, 1, 20, 8, 1);
    private final BooleanProperty speedOnly = new BooleanProperty("Speed Only", this, true);
    private final BooleanProperty playersOnly = new BooleanProperty("Players Only", this, true);
    public final BooleanProperty spaceOnly = new BooleanProperty("Space Only", this, false);
    public final BooleanProperty stopJump = new BooleanProperty("Stop Jump", this, true).onlyIf(spaceOnly.getSettingName(), "bool", "true");
    private final BooleanProperty thirdPerson = new BooleanProperty("Third Person", this, false);

    private static int direction;
    public static Vec3 indexPos;
    public static int index, arraySize;
    private boolean set, changeDir;

    private int voidTicks;

    private int thirdPersonView;
    private boolean flag;

    @Override
    public void onEnable() {
        direction = 1;
        thirdPersonView = 0;
        set = false;
        flag = false;
    }

    @EventTarget
    public void on3D(Event3D e) {
        if(canMove()) {
            if(!flag && thirdPerson.isEnabled()) {
                flag = true;
                thirdPersonView = mc.gameSettings.thirdPersonView;
                mc.gameSettings.thirdPersonView = 1;
            }
            glPushMatrix();
            glDisable(GL_TEXTURE_2D);
            RenderUtils.startSmooth();
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
            glLineWidth(6.0f);
            glBegin(GL_LINE_STRIP);

            EntityLivingBase entity = mc.thePlayer;
            if (canMove()) {
                entity = KillAura.getEntity();
            }

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.elapsedPartialTicks - mc.getRenderManager().viewerPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.elapsedPartialTicks - mc.getRenderManager().viewerPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.elapsedPartialTicks - mc.getRenderManager().viewerPosZ;


            int color1 = HUD.getColor(0).getRGB();
            int sides = this.sides.intValue();
            double pix2 = Math.PI * 2.0D;
            for (int i = 0; i <= 90; ++i) {
                RenderUtils.glColor(color1);
                glVertex3d(x + distance.getCurrent() * Math.cos(i * pix2 / sides), y, z + distance.getCurrent() * Math.sin(i * pix2 / sides));
            }

            glEnd();
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            RenderUtils.endSmooth();
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();

            glPushMatrix();
            glDisable(GL_TEXTURE_2D);
            RenderUtils.startSmooth();
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
            glLineWidth(2.0f);
            glBegin(GL_LINE_STRIP);

            float r1 = ((float) 1 / 255) * Color.black.getRed();
            float g1 = ((float) 1 / 255) * Color.black.getGreen();
            float b1 = ((float) 1 / 255) * Color.black.getBlue();

            for (int i = 0; i <= 90; ++i) {
                glColor3f(r1, g1, b1);
                glVertex3d(x + (distance.getCurrent() + 0.01) * Math.cos(i * pix2 / sides), y, z + (distance.getCurrent() + 0.01) * Math.sin(i * pix2 / sides));
            }

            glEnd();
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            RenderUtils.endSmooth();
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();

            glPushMatrix();
            glDisable(GL_TEXTURE_2D);
            RenderUtils.startSmooth();
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
            glLineWidth(2.0f);
            glBegin(GL_LINE_STRIP);


            for (int i = 0; i <= 90; ++i) {
                glColor3f(r1, g1, b1);
                glVertex3d(x + (distance.getCurrent() - 0.01) * Math.cos(i * pix2 / sides), y, z + (distance.getCurrent() - 0.01) * Math.sin(i * pix2 / sides));
            }

            glEnd();
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
            RenderUtils.endSmooth();
        } else {
            if(flag) {
                flag = false;
                mc.gameSettings.thirdPersonView = thirdPersonView;
            }
        }
    }

    @EventTarget
    public void onEntityAction(EventEntityAction e) {
        if(spaceOnly.isEnabled() && stopJump.isEnabled() && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && KillAura.getEntity() != null && (Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled() || Lime.getInstance().getModuleManager().getModuleC(Speed.class).isToggled())) {
            e.setShouldJump(false);
        }
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if(!canMove() || KillAura.getEntity() == null) {
            set = false;
            index = 0;
            indexPos = null;
            return;
        }

        if(mc.thePlayer.isCollidedHorizontally)
            direction = -direction;

        if(mc.gameSettings.keyBindLeft.isPressed())
            direction = 1;
        else if(mc.gameSettings.keyBindRight.isPressed())
            direction = -1;

        EntityLivingBase target = KillAura.getEntity();
        ArrayList<Vec3> posArrayList = new ArrayList<>();
        for (float rotation = 0; rotation < (Math.PI * 2); rotation += Math.PI * 2 / sides.intValue()) {
            final Vec3 pos = new Vec3(distance.getCurrent() * Math.cos(rotation) + target.posX, target.posY, distance.getCurrent() * Math.sin(rotation) + target.posZ);
            posArrayList.add(pos);
        }

        arraySize = posArrayList.size();
        index = Math.min(index, arraySize - 1);
        if(!set) {
            ArrayList<Vec3> posBuffer = new ArrayList<>(posArrayList);
            posBuffer.sort(Comparator.comparingDouble(vec3 -> mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord)));
            index = posArrayList.indexOf(posBuffer.get(0));
            set = true;
        } else {
            BlockPos blockPos = new BlockPos(posArrayList.get(index).xCoord, posArrayList.get(index).yCoord, posArrayList.get(index).zCoord);
            indexPos = new Vec3(blockPos.getX()+.5, posArrayList.get(index).yCoord, blockPos.getZ()+.5);

            if (!(!inVoid(indexPos) && mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY, indexPos.zCoord)).getBlock().getCollisionBoundingBox(mc.theWorld, new BlockPos(indexPos.xCoord, mc.thePlayer.posY, indexPos.zCoord), mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY, indexPos.zCoord))) == null && mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 1, indexPos.zCoord)).getBlock().getCollisionBoundingBox(mc.theWorld, new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 1, indexPos.zCoord), mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 1, indexPos.zCoord))) == null && mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 2, indexPos.zCoord)).getBlock().getCollisionBoundingBox(mc.theWorld, new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 2, indexPos.zCoord), mc.theWorld.getBlockState(new BlockPos(indexPos.xCoord, mc.thePlayer.posY + 2, indexPos.zCoord))) == null)) {
                direction = -direction;
                changeIndex(posArrayList);
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
        if(Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) return false;
        for (int i = (int) Math.ceil(mc.thePlayer.posY); i >= 0; --i) {
            if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() != Blocks.air) {
                return false;
            }
        }
        return true;
    }

    public void setMoveSpeed(EventMove event, final double speed) {
        voidTicks++;
        if (KillAura.getEntity() != null) {
            if (inVoid() && voidTicks >= 5) {
                voidTicks = 0;
                direction = -direction;
            } else if(!inVoid()) {
                voidTicks = 0;
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
        TargetStrafe targetStrafe = Lime.getInstance().getModuleManager().getModuleC(TargetStrafe.class);
        return KillAura.getEntity() != null && (!targetStrafe.playersOnly.isEnabled() || KillAura.getEntity() instanceof EntityPlayer) && ((Lime.getInstance().getModuleManager().getModuleC(Speed.class).isToggled() || Lime.getInstance().getModuleManager().getModuleC(Flight.class).isToggled()) || !targetStrafe.speedOnly.isEnabled()) && (!targetStrafe.spaceOnly.isEnabled() || Keyboard.isKeyDown(Keyboard.KEY_SPACE)) && Minecraft.getMinecraft().thePlayer.canEntityBeSeen(KillAura.getEntity());
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
