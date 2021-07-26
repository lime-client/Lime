package lime.scripting.api;

import jdk.nashorn.api.scripting.AbstractJSObject;
import lime.utils.other.ChatUtils;

import java.util.Arrays;

public class ClientWrapper extends AbstractJSObject {
    @Override
    public Object getMember(String name) {
        if(name.equalsIgnoreCase("print")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    Arrays.stream(args).forEach(s -> System.out.println((String) s));
                    return null;
                }
            };
        }
        if(name.equalsIgnoreCase("printChat")) {
            return new AbstractJSObject() {
                @Override
                public Object call(Object thiz, Object... args) {
                    Arrays.stream(args).forEach(s -> ChatUtils.sendMessage((String) s));
                    return null;
                }
            };
        }
        return super.getMember(name);
    }
}
