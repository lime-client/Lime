package lime.ui.proxymanager;

import lime.core.Lime;
import net.minecraft.util.EnumChatFormatting;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxyCheckThread extends Thread {

    private final String username, password;
    private String status;
    private final ProxyVersion proxyVersion;

    public ProxyCheckThread(String username, String password, ProxyVersion proxyVersion) {
        this.username = username;
        this.password = password;
        this.proxyVersion = proxyVersion;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    private Proxy createSession(String ip, int port, ProxyVersion version) {
        Authenticator.setDefault(null);

        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(ip, port));
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void run() {
        try {
            this.status = "\u00A7a Success! IP: " + this.username + ", Port: " + this.password;
            Lime.getInstance().setProxy(this.createSession(username, Integer.parseInt(this.password), this.proxyVersion));
        } catch (Exception e) {
            this.status = "Invalid port.";
        }
        super.run();
    }
}
