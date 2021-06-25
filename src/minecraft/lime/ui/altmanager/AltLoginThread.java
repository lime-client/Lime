package lime.ui.altmanager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.net.Proxy;

public class AltLoginThread extends Thread {

    private final String username, password;
    private final boolean cracked;
    private String status;

    public AltLoginThread(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = "§7Waiting";
        this.cracked = password.isEmpty();
    }

    public String getStatus() {
        return status;
    }

    public Session createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException e) {
            System.out.println("Wasn't able to connect to the account, check email or password!");
        }
        return null;
    }

    @Override
    public void run() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (cracked) {
            mc.session = new Session(this.username, "", "", "mojang");
            this.status = "§aLogged in as " + this.username + " (Cracked)";
        } else {
            Session session = this.createSession(this.username, this.password);
            if(session == null) {
                this.status = "§4Invalid mail or passord!";
                return;
            }
            this.status = "§aLogged in as " + session.getUsername() + " (Premium)";
            mc.session = session;
        }
        super.run();
    }
}
