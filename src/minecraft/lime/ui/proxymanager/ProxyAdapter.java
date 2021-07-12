package lime.ui.proxymanager;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.Socket;

public class ProxyAdapter implements ChannelFactory<OioSocketChannel> {

    private final Proxy proxy;

    public ProxyAdapter(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public OioSocketChannel newChannel() {
        Socket socks = new Socket(this.proxy);
        try {
            Method m = socks.getClass().getDeclaredMethod("getImpl");
            m.setAccessible(true);
            Object sd = m.invoke(socks);
            m = sd.getClass().getDeclaredMethod("setV4");
            m.setAccessible(true);
            m.invoke(sd);
            return new OioSocketChannel(socks);
        } catch (Exception ignored) {
        }
        return new OioSocketChannel(socks);
    }
}
