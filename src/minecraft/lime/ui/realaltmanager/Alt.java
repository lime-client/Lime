package lime.ui.realaltmanager;

public class Alt {
    private final String mail, password;
    private String name;
    private boolean selected;

    private long lastTimeTried, lastTimeConnected;

    public Alt(String mail, String password, String name) {
        this.mail = mail;
        this.password = password;
        this.selected = false;
        this.name = name == null || name.isEmpty() ? mail : name;
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

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isCracked() {
        return password == null || password.isEmpty();
    }
}
