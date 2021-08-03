package lime.utils.other.security;

public class User {
    private final String uid;
    private final String hwid;

    public User(String uid, String hwid) {
        this.uid = uid;
        this.hwid = hwid;
    }

    public String getHwid() {
        return hwid;
    }

    public String getUid() {
        return uid;
    }
}
