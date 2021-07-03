package lime.utils.other.security;

public class User {

    private final String name;
    private final String hwid;

    public User(String name, String hwid) {
        this.name = name;
        this.hwid = hwid;
    }

    public String getName() {
        return name;
    }

    public String getHwid() {
        return hwid;
    }
}
