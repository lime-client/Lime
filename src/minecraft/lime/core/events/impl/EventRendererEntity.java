package lime.core.events.impl;

import lime.core.events.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;

public class EventRendererEntity extends Event {
    public enum State {
        PRE, POST
    }

    private final State state;
    private final ModelBase model;
    private final EntityLivingBase entity;
    private final float x, y, z, x2, y2, z2;

    public EventRendererEntity(State state, ModelBase model, EntityLivingBase entity, float x, float y, float z, float x2, float y2, float z2) {
        this.state = state;
        this.model = model;
        this.x = x;
        this.y = y;
        this.z = z;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public State getState() {
        return state;
    }

    public ModelBase getModel() {
        return model;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public float getZ2() {
        return z2;
    }

    public boolean isPre() {
        return state.name().equalsIgnoreCase("pre");
    }
}
