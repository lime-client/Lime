package lime.scripting.api;

import jdk.nashorn.api.scripting.AbstractJSObject;
import lime.scripting.api.events.EventMove;
import lime.utils.IUtil;
import lime.utils.movement.MovementUtils;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PlayerWrapper extends AbstractJSObject implements IUtil {
    @Override
    public Object getMember(String name) {
        if(name.equals("motionY")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.motionY;
                }
            };
        }
        if(name.equals("setMotionY")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    mc.thePlayer.motionY = Double.parseDouble(args[0] + "");
                    return null;
                }
            };
        }
        if(name.equals("setPosition")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    mc.thePlayer.setPosition(Double.parseDouble(args[0] + ""), Double.parseDouble(args[1] + ""), Double.parseDouble(args[2] + ""));
                    return null;
                }
            };
        }
        if(name.equals("posX")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.posX;
                }
            };
        }
        if(name.equals("posY")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.posY;
                }
            };
        }
        if(name.equals("posZ")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.posZ;
                }
            };
        }
        if(name.equals("prevPosX")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.prevPosX;
                }
            };
        }
        if(name.equals("prevPosY")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.prevPosY;
                }
            };
        }
        if(name.equals("prevPosZ")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.prevPosZ;
                }
            };
        }
        if(name.equals("setJumpMovementFactor")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    mc.thePlayer.jumpMovementFactor = Float.parseFloat(args[0] + "");
                    return null;
                }
            };
        }
        if(name.equals("onGround")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.onGround;
                }
            };
        }
        if(name.equals("isOnGround")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return MovementUtils.isOnGround(Double.parseDouble(args[0] + ""));
                }
            };
        }
        if(name.equals("setSpeed")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    if(args[0] instanceof EventMove) {
                        MovementUtils.setSpeed(((EventMove) args[0]).getEventMove(), Double.parseDouble(args[1] + ""));
                    } else {
                        MovementUtils.setSpeed(Double.parseDouble(args[0] + ""));
                    }
                    return null;
                }
            };
        }
        if(name.equals("isMoving")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.isMoving();
                }
            };
        }
        if(name.equals("jump")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    mc.thePlayer.jump();
                    return null;
                }
            };
        }
        if(name.equals("getBaseMoveSpeed")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return MovementUtils.getBaseMoveSpeed();
                }
            };
        }
        if(name.equals("ticksExisted")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.ticksExisted;
                }
            };
        }
        if(name.equals("setTimer")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    mc.timer.timerSpeed = Float.parseFloat(args[0] + "");
                    return null;
                }
            };
        }
        if(name.equals("sendMessage")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    mc.thePlayer.sendChatMessage((String) args[0]);
                    return null;
                }
            };
        }
        if(name.equals("hurtTime")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    return mc.thePlayer.hurtTime;
                }
            };
        }
        if(name.equals("sendPacket")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    int packetId = (Integer) args[0];
                    switch(packetId) {
                        case 0x01:
                            mc.getNetHandler().sendPacketNoEvent(new C01PacketChatMessage((String) args[1]));
                            break;
                        case 0x03:
                            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer((Boolean) args[1]));
                            break;
                        case 0x04:
                            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Double.parseDouble(args[1] + ""), Double.parseDouble(args[2] + ""), Double.parseDouble(args[3] + ""), (Boolean) args[4]));
                            break;
                        case 0x05:
                            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(Float.parseFloat(args[1] + ""), Float.parseFloat(args[2] + ""), (Boolean) args[3]));
                            break;
                        case 0x06:
                            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(Double.parseDouble(args[1] + ""), Double.parseDouble(args[2] + ""), Double.parseDouble(args[3] + ""), Float.parseFloat(args[4] + ""), Float.parseFloat(args[5] + ""), (Boolean) args[6]));
                            break;
                    }
                    return null;
                }
            };
        }
        return super.getMember(name);
    }
}
