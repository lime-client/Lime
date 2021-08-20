package lime.bot.mc.protocol;

import lime.bot.mc.auth.data.GameProfile;
import lime.bot.mc.auth.exception.request.RequestException;
import lime.bot.mc.auth.service.AuthenticationService;
import lime.bot.mc.protocol.data.SubProtocol;
import lime.bot.mc.protocol.packet.handshake.client.HandshakePacket;
import lime.bot.mc.protocol.packet.ingame.client.ClientChatPacket;
import lime.bot.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import lime.bot.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import lime.bot.mc.protocol.packet.ingame.client.ClientRequestPacket;
import lime.bot.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import lime.bot.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import lime.bot.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerAbilitiesPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import lime.bot.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import lime.bot.mc.protocol.packet.ingame.client.window.ClientAdvancementTabPacket;
import lime.bot.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import lime.bot.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import lime.bot.mc.protocol.packet.ingame.client.window.ClientCraftingBookDataPacket;
import lime.bot.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import lime.bot.mc.protocol.packet.ingame.client.window.ClientEnchantItemPacket;
import lime.bot.mc.protocol.packet.ingame.client.window.ClientPrepareCraftingGridPacket;
import lime.bot.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import lime.bot.mc.protocol.packet.ingame.client.world.ClientSpectatePacket;
import lime.bot.mc.protocol.packet.ingame.client.world.ClientSteerBoatPacket;
import lime.bot.mc.protocol.packet.ingame.client.world.ClientSteerVehiclePacket;
import lime.bot.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import lime.bot.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import lime.bot.mc.protocol.packet.ingame.client.world.ClientVehicleMovePacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerAdvancementTabPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerAdvancementsPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerChatPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerCombatPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerDifficultyPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerResourcePackSendPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerRespawnPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerSetCooldownPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerStatisticsPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerSwitchCameraPacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerTabCompletePacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerTitlePacket;
import lime.bot.mc.protocol.packet.ingame.server.ServerUnlockRecipesPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityAnimationPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityCollectItemPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityRemoveEffectPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.ServerVehicleMovePacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.player.ServerPlayerSetExperiencePacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.player.ServerPlayerUseBedPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnGlobalEntityPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import lime.bot.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import lime.bot.mc.protocol.packet.ingame.server.scoreboard.ServerDisplayScoreboardPacket;
import lime.bot.mc.protocol.packet.ingame.server.scoreboard.ServerScoreboardObjectivePacket;
import lime.bot.mc.protocol.packet.ingame.server.scoreboard.ServerTeamPacket;
import lime.bot.mc.protocol.packet.ingame.server.scoreboard.ServerUpdateScorePacket;
import lime.bot.mc.protocol.packet.ingame.server.window.ServerCloseWindowPacket;
import lime.bot.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket;
import lime.bot.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import lime.bot.mc.protocol.packet.ingame.server.window.ServerPreparedCraftingGridPacket;
import lime.bot.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import lime.bot.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import lime.bot.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerBlockBreakAnimPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerBlockValuePacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerNotifyClientPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerOpenTileEntityEditorPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerPlayBuiltinSoundPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerPlayEffectPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerPlaySoundPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import lime.bot.mc.protocol.packet.ingame.server.world.ServerWorldBorderPacket;
import lime.bot.mc.protocol.packet.login.client.EncryptionResponsePacket;
import lime.bot.mc.protocol.packet.login.client.LoginStartPacket;
import lime.bot.mc.protocol.packet.login.server.EncryptionRequestPacket;
import lime.bot.mc.protocol.packet.login.server.LoginDisconnectPacket;
import lime.bot.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import lime.bot.mc.protocol.packet.login.server.LoginSuccessPacket;
import lime.bot.mc.protocol.packet.status.client.StatusPingPacket;
import lime.bot.mc.protocol.packet.status.client.StatusQueryPacket;
import lime.bot.mc.protocol.packet.status.server.StatusPongPacket;
import lime.bot.mc.protocol.packet.status.server.StatusResponsePacket;
import lime.bot.packetlib.Client;
import lime.bot.packetlib.Server;
import lime.bot.packetlib.Session;
import lime.bot.packetlib.crypt.AESEncryption;
import lime.bot.packetlib.crypt.PacketEncryption;
import lime.bot.packetlib.packet.DefaultPacketHeader;
import lime.bot.packetlib.packet.PacketHeader;
import lime.bot.packetlib.packet.PacketProtocol;

import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.UUID;

public class MinecraftProtocol extends PacketProtocol {
    private SubProtocol subProtocol = SubProtocol.HANDSHAKE;
    private PacketHeader header = new DefaultPacketHeader();
    private AESEncryption encrypt;

    private GameProfile profile;
    private String accessToken = "";

    @SuppressWarnings("unused")
    private MinecraftProtocol() {
    }

    public MinecraftProtocol(SubProtocol subProtocol) {
        if(subProtocol != SubProtocol.LOGIN && subProtocol != SubProtocol.STATUS) {
            throw new IllegalArgumentException("Only login and status modes are permitted.");
        }

        this.subProtocol = subProtocol;
        if(subProtocol == SubProtocol.LOGIN) {
            this.profile = new GameProfile((UUID) null, "Player");
        }
    }

    public MinecraftProtocol(String username) {
        this(SubProtocol.LOGIN);
        this.profile = new GameProfile((UUID) null, username);
    }

    public MinecraftProtocol(String username, String password) throws RequestException {
        this(username, password, false);
    }

    public MinecraftProtocol(String username, String using, boolean token) throws RequestException {
        this(username, using, token, Proxy.NO_PROXY);
    }

    public MinecraftProtocol(String username, String using, boolean token, Proxy authProxy) throws RequestException {
        this(SubProtocol.LOGIN);
        String clientToken = UUID.randomUUID().toString();
        AuthenticationService auth = new AuthenticationService(clientToken, authProxy);
        auth.setUsername(username);
        if(token) {
            auth.setAccessToken(using);
        } else {
            auth.setPassword(using);
        }

        auth.login();
        this.profile = auth.getSelectedProfile();
        this.accessToken = auth.getAccessToken();
    }

    public MinecraftProtocol(GameProfile profile, String accessToken) {
        this(SubProtocol.LOGIN);
        this.profile = profile;
        this.accessToken = accessToken;
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public String getSRVRecordPrefix() {
        return "_minecraft";
    }

    @Override
    public PacketHeader getPacketHeader() {
        return this.header;
    }

    @Override
    public PacketEncryption getEncryption() {
        return this.encrypt;
    }

    @Override
    public void newClientSession(Client client, Session session) {
        if(this.profile != null) {
            session.setFlag(MinecraftConstants.PROFILE_KEY, this.profile);
            session.setFlag(MinecraftConstants.ACCESS_TOKEN_KEY, this.accessToken);
        }

        this.setSubProtocol(this.subProtocol, true, session);
        session.addListener(new ClientListener());
    }

    @Override
    public void newServerSession(Server server, Session session) {
        this.setSubProtocol(SubProtocol.HANDSHAKE, false, session);
        session.addListener(new ServerListener());
    }

    protected void enableEncryption(Key key) {
        try {
            this.encrypt = new AESEncryption(key);
        } catch(GeneralSecurityException e) {
            throw new Error("Failed to enable protocol encryption.", e);
        }
    }

    public SubProtocol getSubProtocol() {
        return this.subProtocol;
    }

    protected void setSubProtocol(SubProtocol subProtocol, boolean client, Session session) {
        this.clearPackets();
        switch(subProtocol) {
            case HANDSHAKE:
                if(client) {
                    this.initClientHandshake(session);
                } else {
                    this.initServerHandshake(session);
                }

                break;
            case LOGIN:
                if(client) {
                    this.initClientLogin(session);
                } else {
                    this.initServerLogin(session);
                }

                break;
            case GAME:
                if(client) {
                    this.initClientGame(session);
                } else {
                    this.initServerGame(session);
                }

                break;
            case STATUS:
                if(client) {
                    this.initClientStatus(session);
                } else {
                    this.initServerStatus(session);
                }

                break;
        }

        this.subProtocol = subProtocol;
    }

    private void initClientHandshake(Session session) {
        this.registerOutgoing(0, HandshakePacket.class);
    }

    private void initServerHandshake(Session session) {
        this.registerIncoming(0, HandshakePacket.class);
    }

    private void initClientLogin(Session session) {
        this.registerIncoming(0x00, LoginDisconnectPacket.class);
        this.registerIncoming(0x01, EncryptionRequestPacket.class);
        this.registerIncoming(0x02, LoginSuccessPacket.class);
        this.registerIncoming(0x03, LoginSetCompressionPacket.class);

        this.registerOutgoing(0x00, LoginStartPacket.class);
        this.registerOutgoing(0x01, EncryptionResponsePacket.class);
    }

    private void initServerLogin(Session session) {
        this.registerIncoming(0x00, LoginStartPacket.class);
        this.registerIncoming(0x01, EncryptionResponsePacket.class);

        this.registerOutgoing(0x00, LoginDisconnectPacket.class);
        this.registerOutgoing(0x01, EncryptionRequestPacket.class);
        this.registerOutgoing(0x02, LoginSuccessPacket.class);
        this.registerOutgoing(0x03, LoginSetCompressionPacket.class);
    }

    private void initClientGame(Session session) {
        this.registerIncoming(0x00, ServerSpawnObjectPacket.class);
        this.registerIncoming(0x01, ServerSpawnExpOrbPacket.class);
        this.registerIncoming(0x02, ServerSpawnGlobalEntityPacket.class);
        this.registerIncoming(0x03, ServerSpawnMobPacket.class);
        this.registerIncoming(0x04, ServerSpawnPaintingPacket.class);
        this.registerIncoming(0x05, ServerSpawnPlayerPacket.class);
        this.registerIncoming(0x06, ServerEntityAnimationPacket.class);
        this.registerIncoming(0x07, ServerStatisticsPacket.class);
        this.registerIncoming(0x08, ServerBlockBreakAnimPacket.class);
        this.registerIncoming(0x09, ServerUpdateTileEntityPacket.class);
        this.registerIncoming(0x0A, ServerBlockValuePacket.class);
        this.registerIncoming(0x0B, ServerBlockChangePacket.class);
        this.registerIncoming(0x0C, ServerBossBarPacket.class);
        this.registerIncoming(0x0D, ServerDifficultyPacket.class);
        this.registerIncoming(0x0E, ServerTabCompletePacket.class);
        this.registerIncoming(0x0F, ServerChatPacket.class);
        this.registerIncoming(0x10, ServerMultiBlockChangePacket.class);
        this.registerIncoming(0x11, ServerConfirmTransactionPacket.class);
        this.registerIncoming(0x12, ServerCloseWindowPacket.class);
        this.registerIncoming(0x13, ServerOpenWindowPacket.class);
        this.registerIncoming(0x14, ServerWindowItemsPacket.class);
        this.registerIncoming(0x15, ServerWindowPropertyPacket.class);
        this.registerIncoming(0x16, ServerSetSlotPacket.class);
        this.registerIncoming(0x17, ServerSetCooldownPacket.class);
        this.registerIncoming(0x18, ServerPluginMessagePacket.class);
        this.registerIncoming(0x19, ServerPlaySoundPacket.class);
        this.registerIncoming(0x1A, ServerDisconnectPacket.class);
        this.registerIncoming(0x1B, ServerEntityStatusPacket.class);
        this.registerIncoming(0x1C, ServerExplosionPacket.class);
        this.registerIncoming(0x1D, ServerUnloadChunkPacket.class);
        this.registerIncoming(0x1E, ServerNotifyClientPacket.class);
        this.registerIncoming(0x1F, ServerKeepAlivePacket.class);
        this.registerIncoming(0x20, ServerChunkDataPacket.class);
        this.registerIncoming(0x21, ServerPlayEffectPacket.class);
        this.registerIncoming(0x22, ServerSpawnParticlePacket.class);
        this.registerIncoming(0x23, ServerJoinGamePacket.class);
        this.registerIncoming(0x24, ServerMapDataPacket.class);
        this.registerIncoming(0x25, ServerEntityMovementPacket.class);
        this.registerIncoming(0x26, ServerEntityPositionPacket.class);
        this.registerIncoming(0x27, ServerEntityPositionRotationPacket.class);
        this.registerIncoming(0x28, ServerEntityRotationPacket.class);
        this.registerIncoming(0x29, ServerVehicleMovePacket.class);
        this.registerIncoming(0x2A, ServerOpenTileEntityEditorPacket.class);
        this.registerIncoming(0x2B, ServerPreparedCraftingGridPacket.class);
        this.registerIncoming(0x2C, ServerPlayerAbilitiesPacket.class);
        this.registerIncoming(0x2D, ServerCombatPacket.class);
        this.registerIncoming(0x2E, ServerPlayerListEntryPacket.class);
        this.registerIncoming(0x2F, ServerPlayerPositionRotationPacket.class);
        this.registerIncoming(0x30, ServerPlayerUseBedPacket.class);
        this.registerIncoming(0x31, ServerUnlockRecipesPacket.class);
        this.registerIncoming(0x32, ServerEntityDestroyPacket.class);
        this.registerIncoming(0x33, ServerEntityRemoveEffectPacket.class);
        this.registerIncoming(0x34, ServerResourcePackSendPacket.class);
        this.registerIncoming(0x35, ServerRespawnPacket.class);
        this.registerIncoming(0x36, ServerEntityHeadLookPacket.class);
        this.registerIncoming(0x37, ServerAdvancementTabPacket.class);
        this.registerIncoming(0x38, ServerWorldBorderPacket.class);
        this.registerIncoming(0x39, ServerSwitchCameraPacket.class);
        this.registerIncoming(0x3A, ServerPlayerChangeHeldItemPacket.class);
        this.registerIncoming(0x3B, ServerDisplayScoreboardPacket.class);
        this.registerIncoming(0x3C, ServerEntityMetadataPacket.class);
        this.registerIncoming(0x3D, ServerEntityAttachPacket.class);
        this.registerIncoming(0x3E, ServerEntityVelocityPacket.class);
        this.registerIncoming(0x3F, ServerEntityEquipmentPacket.class);
        this.registerIncoming(0x40, ServerPlayerSetExperiencePacket.class);
        this.registerIncoming(0x41, ServerPlayerHealthPacket.class);
        this.registerIncoming(0x42, ServerScoreboardObjectivePacket.class);
        this.registerIncoming(0x43, ServerEntitySetPassengersPacket.class);
        this.registerIncoming(0x44, ServerTeamPacket.class);
        this.registerIncoming(0x45, ServerUpdateScorePacket.class);
        this.registerIncoming(0x46, ServerSpawnPositionPacket.class);
        this.registerIncoming(0x47, ServerUpdateTimePacket.class);
        this.registerIncoming(0x48, ServerTitlePacket.class);
        this.registerIncoming(0x49, ServerPlayBuiltinSoundPacket.class);
        this.registerIncoming(0x4A, ServerPlayerListDataPacket.class);
        this.registerIncoming(0x4B, ServerEntityCollectItemPacket.class);
        this.registerIncoming(0x4C, ServerEntityTeleportPacket.class);
        this.registerIncoming(0x4D, ServerAdvancementsPacket.class);
        this.registerIncoming(0x4E, ServerEntityPropertiesPacket.class);
        this.registerIncoming(0x4F, ServerEntityEffectPacket.class);

        this.registerOutgoing(0x00, ClientTeleportConfirmPacket.class);
        this.registerOutgoing(0x01, ClientTabCompletePacket.class);
        this.registerOutgoing(0x02, ClientChatPacket.class);
        this.registerOutgoing(0x03, ClientRequestPacket.class);
        this.registerOutgoing(0x04, ClientSettingsPacket.class);
        this.registerOutgoing(0x05, ClientConfirmTransactionPacket.class);
        this.registerOutgoing(0x06, ClientEnchantItemPacket.class);
        this.registerOutgoing(0x07, ClientWindowActionPacket.class);
        this.registerOutgoing(0x08, ClientCloseWindowPacket.class);
        this.registerOutgoing(0x09, ClientPluginMessagePacket.class);
        this.registerOutgoing(0x0A, ClientPlayerInteractEntityPacket.class);
        this.registerOutgoing(0x0B, ClientKeepAlivePacket.class);
        this.registerOutgoing(0x0C, ClientPlayerMovementPacket.class);
        this.registerOutgoing(0x0D, ClientPlayerPositionPacket.class);
        this.registerOutgoing(0x0E, ClientPlayerPositionRotationPacket.class);
        this.registerOutgoing(0x0F, ClientPlayerRotationPacket.class);
        this.registerOutgoing(0x10, ClientVehicleMovePacket.class);
        this.registerOutgoing(0x11, ClientSteerBoatPacket.class);
        this.registerOutgoing(0x12, ClientPrepareCraftingGridPacket.class);
        this.registerOutgoing(0x13, ClientPlayerAbilitiesPacket.class);
        this.registerOutgoing(0x14, ClientPlayerActionPacket.class);
        this.registerOutgoing(0x15, ClientPlayerStatePacket.class);
        this.registerOutgoing(0x16, ClientSteerVehiclePacket.class);
        this.registerOutgoing(0x17, ClientCraftingBookDataPacket.class);
        this.registerOutgoing(0x18, ClientResourcePackStatusPacket.class);
        this.registerOutgoing(0x19, ClientAdvancementTabPacket.class);
        this.registerOutgoing(0x1A, ClientPlayerChangeHeldItemPacket.class);
        this.registerOutgoing(0x1B, ClientCreativeInventoryActionPacket.class);
        this.registerOutgoing(0x1C, ClientUpdateSignPacket.class);
        this.registerOutgoing(0x1D, ClientPlayerSwingArmPacket.class);
        this.registerOutgoing(0x1E, ClientSpectatePacket.class);
        this.registerOutgoing(0x1F, ClientPlayerPlaceBlockPacket.class);
        this.registerOutgoing(0x20, ClientPlayerUseItemPacket.class);
    }

    private void initServerGame(Session session) {
        this.registerIncoming(0x00, ClientTeleportConfirmPacket.class);
        this.registerIncoming(0x01, ClientTabCompletePacket.class);
        this.registerIncoming(0x02, ClientChatPacket.class);
        this.registerIncoming(0x03, ClientRequestPacket.class);
        this.registerIncoming(0x04, ClientSettingsPacket.class);
        this.registerIncoming(0x05, ClientConfirmTransactionPacket.class);
        this.registerIncoming(0x06, ClientEnchantItemPacket.class);
        this.registerIncoming(0x07, ClientWindowActionPacket.class);
        this.registerIncoming(0x08, ClientCloseWindowPacket.class);
        this.registerIncoming(0x09, ClientPluginMessagePacket.class);
        this.registerIncoming(0x0A, ClientPlayerInteractEntityPacket.class);
        this.registerIncoming(0x0B, ClientKeepAlivePacket.class);
        this.registerIncoming(0x0C, ClientPlayerMovementPacket.class);
        this.registerIncoming(0x0D, ClientPlayerPositionPacket.class);
        this.registerIncoming(0x0E, ClientPlayerPositionRotationPacket.class);
        this.registerIncoming(0x0F, ClientPlayerRotationPacket.class);
        this.registerIncoming(0x10, ClientVehicleMovePacket.class);
        this.registerIncoming(0x11, ClientSteerBoatPacket.class);
        this.registerIncoming(0x12, ClientPrepareCraftingGridPacket.class);
        this.registerIncoming(0x13, ClientPlayerAbilitiesPacket.class);
        this.registerIncoming(0x14, ClientPlayerActionPacket.class);
        this.registerIncoming(0x15, ClientPlayerStatePacket.class);
        this.registerIncoming(0x16, ClientSteerVehiclePacket.class);
        this.registerIncoming(0x17, ClientCraftingBookDataPacket.class);
        this.registerIncoming(0x18, ClientResourcePackStatusPacket.class);
        this.registerIncoming(0x19, ClientAdvancementTabPacket.class);
        this.registerIncoming(0x1A, ClientPlayerChangeHeldItemPacket.class);
        this.registerIncoming(0x1B, ClientCreativeInventoryActionPacket.class);
        this.registerIncoming(0x1C, ClientUpdateSignPacket.class);
        this.registerIncoming(0x1D, ClientPlayerSwingArmPacket.class);
        this.registerIncoming(0x1E, ClientSpectatePacket.class);
        this.registerIncoming(0x1F, ClientPlayerPlaceBlockPacket.class);
        this.registerIncoming(0x20, ClientPlayerUseItemPacket.class);

        this.registerOutgoing(0x00, ServerSpawnObjectPacket.class);
        this.registerOutgoing(0x01, ServerSpawnExpOrbPacket.class);
        this.registerOutgoing(0x02, ServerSpawnGlobalEntityPacket.class);
        this.registerOutgoing(0x03, ServerSpawnMobPacket.class);
        this.registerOutgoing(0x04, ServerSpawnPaintingPacket.class);
        this.registerOutgoing(0x05, ServerSpawnPlayerPacket.class);
        this.registerOutgoing(0x06, ServerEntityAnimationPacket.class);
        this.registerOutgoing(0x07, ServerStatisticsPacket.class);
        this.registerOutgoing(0x08, ServerBlockBreakAnimPacket.class);
        this.registerOutgoing(0x09, ServerUpdateTileEntityPacket.class);
        this.registerOutgoing(0x0A, ServerBlockValuePacket.class);
        this.registerOutgoing(0x0B, ServerBlockChangePacket.class);
        this.registerOutgoing(0x0C, ServerBossBarPacket.class);
        this.registerOutgoing(0x0D, ServerDifficultyPacket.class);
        this.registerOutgoing(0x0E, ServerTabCompletePacket.class);
        this.registerOutgoing(0x0F, ServerChatPacket.class);
        this.registerOutgoing(0x10, ServerMultiBlockChangePacket.class);
        this.registerOutgoing(0x11, ServerConfirmTransactionPacket.class);
        this.registerOutgoing(0x12, ServerCloseWindowPacket.class);
        this.registerOutgoing(0x13, ServerOpenWindowPacket.class);
        this.registerOutgoing(0x14, ServerWindowItemsPacket.class);
        this.registerOutgoing(0x15, ServerWindowPropertyPacket.class);
        this.registerOutgoing(0x16, ServerSetSlotPacket.class);
        this.registerOutgoing(0x17, ServerSetCooldownPacket.class);
        this.registerOutgoing(0x18, ServerPluginMessagePacket.class);
        this.registerOutgoing(0x19, ServerPlaySoundPacket.class);
        this.registerOutgoing(0x1A, ServerDisconnectPacket.class);
        this.registerOutgoing(0x1B, ServerEntityStatusPacket.class);
        this.registerOutgoing(0x1C, ServerExplosionPacket.class);
        this.registerOutgoing(0x1D, ServerUnloadChunkPacket.class);
        this.registerOutgoing(0x1E, ServerNotifyClientPacket.class);
        this.registerOutgoing(0x1F, ServerKeepAlivePacket.class);
        this.registerOutgoing(0x20, ServerChunkDataPacket.class);
        this.registerOutgoing(0x21, ServerPlayEffectPacket.class);
        this.registerOutgoing(0x22, ServerSpawnParticlePacket.class);
        this.registerOutgoing(0x23, ServerJoinGamePacket.class);
        this.registerOutgoing(0x24, ServerMapDataPacket.class);
        this.registerOutgoing(0x25, ServerEntityMovementPacket.class);
        this.registerOutgoing(0x26, ServerEntityPositionPacket.class);
        this.registerOutgoing(0x27, ServerEntityPositionRotationPacket.class);
        this.registerOutgoing(0x28, ServerEntityRotationPacket.class);
        this.registerOutgoing(0x29, ServerVehicleMovePacket.class);
        this.registerOutgoing(0x2A, ServerOpenTileEntityEditorPacket.class);
        this.registerOutgoing(0x2B, ServerPreparedCraftingGridPacket.class);
        this.registerOutgoing(0x2C, ServerPlayerAbilitiesPacket.class);
        this.registerOutgoing(0x2D, ServerCombatPacket.class);
        this.registerOutgoing(0x2E, ServerPlayerListEntryPacket.class);
        this.registerOutgoing(0x2F, ServerPlayerPositionRotationPacket.class);
        this.registerOutgoing(0x30, ServerPlayerUseBedPacket.class);
        this.registerOutgoing(0x31, ServerUnlockRecipesPacket.class);
        this.registerOutgoing(0x32, ServerEntityDestroyPacket.class);
        this.registerOutgoing(0x33, ServerEntityRemoveEffectPacket.class);
        this.registerOutgoing(0x34, ServerResourcePackSendPacket.class);
        this.registerOutgoing(0x35, ServerRespawnPacket.class);
        this.registerOutgoing(0x36, ServerEntityHeadLookPacket.class);
        this.registerOutgoing(0x37, ServerAdvancementTabPacket.class);
        this.registerOutgoing(0x38, ServerWorldBorderPacket.class);
        this.registerOutgoing(0x39, ServerSwitchCameraPacket.class);
        this.registerOutgoing(0x3A, ServerPlayerChangeHeldItemPacket.class);
        this.registerOutgoing(0x3B, ServerDisplayScoreboardPacket.class);
        this.registerOutgoing(0x3C, ServerEntityMetadataPacket.class);
        this.registerOutgoing(0x3D, ServerEntityAttachPacket.class);
        this.registerOutgoing(0x3E, ServerEntityVelocityPacket.class);
        this.registerOutgoing(0x3F, ServerEntityEquipmentPacket.class);
        this.registerOutgoing(0x40, ServerPlayerSetExperiencePacket.class);
        this.registerOutgoing(0x41, ServerPlayerHealthPacket.class);
        this.registerOutgoing(0x42, ServerScoreboardObjectivePacket.class);
        this.registerOutgoing(0x43, ServerEntitySetPassengersPacket.class);
        this.registerOutgoing(0x44, ServerTeamPacket.class);
        this.registerOutgoing(0x45, ServerUpdateScorePacket.class);
        this.registerOutgoing(0x46, ServerSpawnPositionPacket.class);
        this.registerOutgoing(0x47, ServerUpdateTimePacket.class);
        this.registerOutgoing(0x48, ServerTitlePacket.class);
        this.registerOutgoing(0x49, ServerPlayBuiltinSoundPacket.class);
        this.registerOutgoing(0x4A, ServerPlayerListDataPacket.class);
        this.registerOutgoing(0x4B, ServerEntityCollectItemPacket.class);
        this.registerOutgoing(0x4C, ServerEntityTeleportPacket.class);
        this.registerOutgoing(0x4D, ServerAdvancementsPacket.class);
        this.registerOutgoing(0x4E, ServerEntityPropertiesPacket.class);
        this.registerOutgoing(0x4F, ServerEntityEffectPacket.class);
    }

    private void initClientStatus(Session session) {
        this.registerIncoming(0x00, StatusResponsePacket.class);
        this.registerIncoming(0x01, StatusPongPacket.class);

        this.registerOutgoing(0x00, StatusQueryPacket.class);
        this.registerOutgoing(0x01, StatusPingPacket.class);
    }

    private void initServerStatus(Session session) {
        this.registerIncoming(0x00, StatusQueryPacket.class);
        this.registerIncoming(0x01, StatusPingPacket.class);

        this.registerOutgoing(0x00, StatusResponsePacket.class);
        this.registerOutgoing(0x01, StatusPongPacket.class);
    }
}
