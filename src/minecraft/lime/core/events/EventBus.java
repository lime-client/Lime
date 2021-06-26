package lime.core.events;

import lime.core.events.impl.*;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;

public class EventBus {

	public static final EventBus INSTANCE = new EventBus();
	private PrintStream defaultStream = System.out;
	public final static boolean enablePriorities = true;// if activated, register Objects can take more times
	private final HashMap<Class<? extends Event>, LightList<Handler>> HANDLERS = new HashMap<>();

	private EventBus() {
		registerEvents(Event2D.class, EventUpdate.class, EventKey.class, EventPacket.class, EventMotion.class, EventBoundingBox.class, EventScoreboard.class,
				Event3D.class, EventMove.class, EventSlow.class, EventRendererEntity.class);
	}

	@SafeVarargs
	private final void registerEvents(Class<? extends Event>... events) {
		for (Class<? extends Event> e : events) {
			HANDLERS.put(e,new LightList<>());
		}
	}

	public LightList<Handler> getHandlerList(Event event) {
		return HANDLERS.get(event.getClass());
	}

	public void call(Event event) {
		try {
			for (Handler h : HANDLERS.get(event.getClass())) {
				if (!event.canNextEvent()) return;
				h.call(event);
			}
		} catch (NullPointerException e) {
			getPrintStream().println("Attempt to call "+event.getClass().getName()+" but was not defined!");
		} catch (Exception e) {
			getPrintStream().println("Attempt to call "+event.getClass().getName()+" unknown error");
			e.printStackTrace();
		}
	}

	public void register(final Object object) {
		try {
			for (Method method : object.getClass().getDeclaredMethods()) {
				EventTarget eventTargetAnnotation = method.getAnnotation(EventTarget.class);
				if (eventTargetAnnotation == null) continue;

				Class<? extends Event> event = findParameterType(method);
				if (event == null) continue;
				if (!enablePriorities)  {
					HANDLERS.get(event).add(new Handler(object,method,eventTargetAnnotation.priority()));
					continue;
				}
				sortHandlerListByPriority(
						HANDLERS.get(event).addElement(new Handler(object,method,eventTargetAnnotation.priority()))
				);
			}
			//Sort Events
		} catch (Exception e) {
			e.printStackTrace(getPrintStream());
		}
	}

	public void unregister(final Object object) {
		try {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if (method.getAnnotation(EventTarget.class) == null) continue;

				Class<? extends Event> event = findParameterType(method);
				if (event == null) continue;
				HANDLERS.get(event).removeIf(handler -> handler.getObjInstance().equals(object) && handler.getMethod().equals(method));
			}
		} catch (Exception e) {
			e.printStackTrace(getPrintStream());
		}
	}

	private void sortHandlerListByPriority(LightList<Handler> handlerList) {
		handlerList.sort((handler, t1) -> t1.getPriority().compareTo(handler.getPriority()));
	}

	private Class<? extends Event> findParameterType(Method method) {
		for (Class<? extends Event> e : HANDLERS.keySet()) {
			if (method.getParameterTypes().length < 1) continue;
			if (!method.getParameterTypes()[0].equals(e)) continue;
			return e;
		}
		getPrintStream().println("[EventBus] found invalid method "+method.getName());
		return null;
	}

	public void setPrintStream(PrintStream printStream) {
		this.defaultStream = printStream;
	}

	public PrintStream getPrintStream() {
		return defaultStream;
	}
}
