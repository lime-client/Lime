package lime.bot.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lime.bot.mc.auth.exception.request.RequestException;
import lime.bot.mc.protocol.MinecraftProtocol;
import lime.bot.mc.protocol.data.message.Message;
import lime.bot.mc.protocol.packet.MinecraftPacket;
import lime.bot.mc.protocol.packet.ingame.client.ClientChatPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerChatPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import lime.bot.packetlib.Client;
import lime.bot.packetlib.event.session.DisconnectedEvent;
import lime.bot.packetlib.event.session.PacketReceivedEvent;
import lime.bot.packetlib.event.session.SessionAdapter;
import lime.bot.packetlib.tcp.TcpSessionFactory;
import lime.core.Lime;
import lime.ui.notifications.Notification;
import lime.utils.other.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.net.Proxy;

public class Bot {
    private Position position;
    private final JsonParser jsonParser = new JsonParser();
    private int playerId;
    private String userName;
    private Client client;

    private final String mail;
    private final String password;

    public Bot(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    private Client createClient() {
        MinecraftProtocol protocol;
        String[] userInfo = new String[] {mail, password};
        try {
            protocol = new MinecraftProtocol(userInfo[0], userInfo[1], false);
        }
        catch (RequestException e) {
            e.printStackTrace();
            return null;
        }
        userName = protocol.getProfile().getName();
        final Client client = new Client("tiddies.club", 25565, protocol, new TcpSessionFactory());
        client.getSession().setFlag("auth-proxy", Proxy.NO_PROXY);
        client.getSession().addListener(new SessionAdapter(){
            @Override
            public void packetReceived(PacketReceivedEvent event) {
                MinecraftPacket packet;
                if (event.getPacket() instanceof ServerChatPacket) {
                    Message message = ((ServerChatPacket)event.getPacket()).getMessage();
                    JsonElement element = message.toJson();
                    try {
                        if (ChatUtils.removeColors(message.getFullText()).toLowerCase().contains("join> " + userName.toLowerCase())) {
                            Lime.getInstance().getNotificationManager().addNotification(new Notification("Disabler", "Bot joined.", Notification.Type.SUCCESS));
                            client.getSession().send(new ClientChatPacket("/spec"));
                        }
                        if (element instanceof JsonObject) {
                            client.getSession().send(new ClientChatPacket(jsonParser.parse(element.getAsJsonObject().getAsJsonObject().get("text").getAsString()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("extra").getAsJsonArray().get(1).getAsJsonObject().get("clickEvent").getAsJsonObject().get("value").getAsString()));
                        }
                    }
                    catch (Exception ignored) {
                    }
                }
                if (event.getPacket() instanceof ServerPlayerPositionRotationPacket) {
                    ServerPlayerPositionRotationPacket positionRotationPacket = event.getPacket();
                    position = new Position(positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ());
                    client.getSession().send(new ClientPlayerPositionRotationPacket(true, positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ(), 0, 0));
                    client.getSession().send(new ClientPlayerPositionRotationPacket(true, positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ(), 0, 0));
                }
                if (event.getPacket() instanceof ServerEntityPositionPacket && ((ServerEntityMovementPacket)(packet = event.getPacket())).getEntityId() == playerId) {
                    position = new Position(((ServerEntityMovementPacket)packet).getMovementX(), ((ServerEntityMovementPacket)packet).getMovementY(), ((ServerEntityMovementPacket)packet).getMovementZ());
                }
                if (event.getPacket() instanceof ServerJoinGamePacket) {
                    ServerJoinGamePacket joinGamePacket1 = event.getPacket();
                    playerId = joinGamePacket1.getEntityId();
                    new Thread(() -> Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C01PacketChatMessage("/party " + userName))).start();
                }
                if (event.getPacket() instanceof ServerSpawnPlayerPacket) {
                    packet = event.getPacket();
                    if (((ServerSpawnPlayerPacket)packet).getEntityId() == playerId) {
                        position = new Position(((ServerSpawnPlayerPacket)packet).getX(), ((ServerSpawnPlayerPacket)packet).getY(), ((ServerSpawnPlayerPacket)packet).getZ());
                    }
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                System.out.println("Bot Kicked: " + Message.fromString(event.getReason()).getText());
                if (event.getCause() != null) {
                    event.getCause().printStackTrace();
                }
            }
        });
        return client;
    }

    public void startBot() {
        client = createClient();
        assert (client != null);
        client.getSession().connect();
    }

    public Client getClient() {
        return client;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getUserName() {
        return userName;
    }

    public static class Position {
        private final double x, y, z;
        
        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
 