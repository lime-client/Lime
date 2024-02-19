package lime.core.events;

import java.io.PrintStream;
import java.lang.reflect.Method;

public class Handler {
	private final Object objInstance;
	private final Method method;
	private final Priority priority;

	public Handler(final Object objInstance, final Method method, final Priority priority) {
		this.method = method;
		this.objInstance = objInstance;
		this.priority = priority;
	}

	public void call(final Event event) {
		try {
			if (EventBus.enablePriorities) event.setPriority(priority);
			method.invoke(objInstance,event);
		} catch (Exception e) {
			PrintStream stream = EventBus.INSTANCE.getPrintStream();
			stream.println("Error when calling "+event.getClass().getName()+" on "+objInstance.getClass().getName()+" in method "+method.getName()+"@"+method.getReturnType().getName());
			e.printStackTrace(stream);
		}
	}

	@Override
	public boolean equals(Object o) {
		Handler handler = (Handler) o;
		return handler.getMethod() == this.getMethod() && handler.getObjInstance() == this.getObjInstance();
	}

	public Object getObjInstance() {
		return objInstance;
	}

	public Priority getPriority() {
		return priority;
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return "Handler{Object="+objInstance.getClass().getName()+",Method={"+method+"}}";
	}
}
