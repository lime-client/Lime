package lime.ui.realaltmanager;

public class Alt {
    private final String mail, password;
    private String name;

    private long lastTimeTried, lastTimeConnected;

    public Alt(String mail, String password, String name) {
        this.mail = mail;
        this.password = password;
        this.name = name == null || name.isEmpty() ? "" : name;
    }

    public Alt(String mail, String password) {
        this(mail, password, "");
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCracked() {
        return password == null || password.isEmpty();
    }

    public long getLastTimeConnected() {
        return lastTimeConnected;
    }

    public long getLastTimeTried() {
        return lastTimeTried;
    }

    public void setLastTimeConnected(long lastTimeConnected) {
        this.lastTimeConnected = lastTimeConnected;
    }

    public void setLastTimeTried(long lastTimeTried) {
        this.lastTimeTried = lastTimeTried;
    }
}
