package lime.ui.proxymanager;

public enum ProxyVersion {
    SOCKSv4("SOCKS4"), SOCKSv5("SOCKS5"), AuthSocks("SOCKS5");

    private final String ver_name;

    ProxyVersion(String name) {
        this.ver_name = name;
    }

    public String getName() {
        return ver_name;
    }
}
