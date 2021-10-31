package lime.ui.altmanager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import lime.utils.other.WebUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class AltLoginThread extends Thread {

    private final String username, password;
    private final boolean cracked, microsoft;
    private String status;

    public AltLoginThread(String username, String password, boolean microsoft) {
        this.username = username;
        this.password = password;
        this.status = "§7Waiting";
        this.microsoft = microsoft;
        this.cracked = password.isEmpty();
    }

    public String getStatus() {
        return status;
    }

    public static Session createSession(String username, String password) {
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

    public static Session createMicrosoftSession(String username, String password) {
        try {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithCredentials(username, password);
            return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
        } catch (Exception e) {
            e.printStackTrace();
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
            Session session = microsoft ? createMicrosoftSession(this.username, this.password) : createSession(username, password);
            if(session == null) {
                this.status = "§4Invalid mail or password!";
                return;
            }
            this.status = "§aLogged in as " + session.getUsername() + " (Premium)";
            mc.session = session;
        }
        super.run();
    }
}
