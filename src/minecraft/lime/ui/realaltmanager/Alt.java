package lime.ui.realaltmanager;

public class Alt {
    private final String mail, password;
    private String name;

    public Alt(String mail, String password, String name) {
        this.mail = mail;
        this.password = password;
        this.name = "";
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
}
