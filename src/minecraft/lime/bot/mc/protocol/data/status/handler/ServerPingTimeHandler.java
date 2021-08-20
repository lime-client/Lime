package lime.bot.mc.protocol.data.status.handler;

import lime.bot.packetlib.Session;

public interface ServerPingTimeHandler {
    public void handle(Session session, long pingTime);
}
