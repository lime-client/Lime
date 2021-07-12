package lime.ui.proxymanager;

public enum ProxyVersion {
    SOCKSv4("SOCKS4", 4145, 1080), SOCKSv5("SOCKS5", 1080, 9050, 9051, 4145), AuthSocks("SOCKS5", 720, 1080, 4146);

    private final String ver_name;
    private final int defaultPort, extraPossiblePorts[];

    ProxyVersion(String name, int defaultPort, int... extraPossiblePorts) {
        this.ver_name = name;
        this.defaultPort = defaultPort;
        this.extraPossiblePorts = extraPossiblePorts;
    }

    public String getName() {
        return ver_name;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public int[] getExtraPossiblePorts() {
        return extraPossiblePorts;
    }
}
