package lime.core.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) 
public @interface EventTarget {

	Priority priority() default Priority.NORMAL;
}
