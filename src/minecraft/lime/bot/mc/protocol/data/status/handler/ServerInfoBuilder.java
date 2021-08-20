package lime.bot.mc.protocol.data.status.handler;

import lime.bot.mc.protocol.data.status.ServerStatusInfo;
import lime.bot.packetlib.Session;

public interface ServerInfoBuilder {
    public ServerStatusInfo buildInfo(Session session);
}
