package lime.core.events;

import net.minecraft.client.Minecraft;

public abstract class Event {
	protected static Minecraft mc = Minecraft.getMinecraft();
	private boolean isCanceled = false;
	private boolean canNextEvent = true;
	private EventType eventType;
	private Priority priority = Priority.NORMAL;

	public Event() {

	}

	public Event(EventType eventType) {
		this.eventType = eventType;
	}

	public void setCanceled(final boolean state) {
		this.isCanceled = state;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public void stopEventDispatch() {
		canNextEvent = false;
	}

	public boolean canNextEvent() {
		return canNextEvent;
	}

	public EventType getEventType() {
		return eventType;
	}
}
