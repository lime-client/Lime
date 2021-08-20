package lime.bot.mc.protocol;

import lime.bot.packetlib.Session;

public interface ServerLoginHandler {
    public void loggedIn(Session session);
}
