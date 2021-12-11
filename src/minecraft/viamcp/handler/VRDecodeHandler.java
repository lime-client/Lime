package viamcp.handler;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelDecoderException;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lime.core.events.EventBus;
import lime.core.events.impl.EventPacket;
import lime.utils.other.SPacketPlayerPosLook;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@ChannelHandler.Sharable
public class VRDecodeHandler extends MessageToMessageDecoder<ByteBuf>
{
    private final UserConnection info;
    private boolean handledCompression;
    private boolean skipDoubleTransform;

    public VRDecodeHandler(UserConnection info) {
        this.info = info;
    }

    public UserConnection getInfo() {
        return info;
    }

    // https://github.com/ViaVersion/ViaVersion/blob/master/velocity/src/main/java/us/myles/ViaVersion/velocity/handlers/VelocityDecodeHandler.java
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception
    {
        if (skipDoubleTransform)
        {
            skipDoubleTransform = false;
            out.add(bytebuf.retain());
            return;
        }

        if (!info.checkIncomingPacket())
        {
            throw CancelDecoderException.generate(null);
        }

        if (!info.shouldTransformPacket())
        {
            out.add(bytebuf.retain());
            return;
        }

        ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);

        try
        {
            boolean needsCompress = handleCompressionOrder(ctx, transformedBuf);

            try {
                PacketBuffer packetBuffer = new PacketBuffer(transformedBuf.copy());
                int packetID = packetBuffer.readVarIntFromBuffer();

                if(packetID == 0x38) {
                    double x = packetBuffer.readDouble();
                    double y = packetBuffer.readDouble();
                    double z = packetBuffer.readDouble();
                    float yaw = packetBuffer.readFloat();
                    float pitch = packetBuffer.readFloat();
                    S08PacketPlayerPosLook.EnumFlags.func_180053_a(packetBuffer.readUnsignedByte());
                    int teleportId = packetBuffer.readVarIntFromBuffer();
                    EventBus.INSTANCE.call(new EventPacket(new SPacketPlayerPosLook(x, y, z, yaw, pitch, teleportId), EventPacket.Mode.RECEIVE));
                }
            } catch (Exception ignored){}

            info.transformIncoming(transformedBuf, CancelDecoderException::generate);

            if (needsCompress)
            {
                CommonTransformer.compress(ctx, transformedBuf);
                skipDoubleTransform = true;
            }

            out.add(transformedBuf.retain());
        }
        finally
        {
            transformedBuf.release();
        }
    }

    private boolean handleCompressionOrder(ChannelHandlerContext ctx, ByteBuf buf) throws InvocationTargetException
    {
        if (handledCompression)
        {
            return false;
        }

        int decoderIndex = ctx.pipeline().names().indexOf("decompress");

        if (decoderIndex == -1)
        {
            return false;
        }

        handledCompression = true;

        if (decoderIndex > ctx.pipeline().names().indexOf("via-decoder"))
        {
            // Need to decompress this packet due to bad order
            CommonTransformer.decompress(ctx, buf);
            ChannelHandler encoder = ctx.pipeline().get("via-encoder");
            ChannelHandler decoder = ctx.pipeline().get("via-decoder");
            ctx.pipeline().remove(encoder);
            ctx.pipeline().remove(decoder);
            ctx.pipeline().addAfter("compress", "via-encoder", encoder);
            ctx.pipeline().addAfter("decompress", "via-decoder", decoder);
            return true;
        }

        return false;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        if (PipelineUtil.containsCause(cause, CancelCodecException.class))
        {
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}