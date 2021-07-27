package lime.scripting.api;

import jdk.nashorn.api.scripting.AbstractJSObject;
import lime.scripting.api.events.EventMove;
import lime.utils.IUtil;
import lime.utils.movement.MovementUtils;

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
        return super.getMember(name);
    }
}
