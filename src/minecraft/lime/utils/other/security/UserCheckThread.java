package lime.utils.other.security;

import lime.core.Lime;
import lime.utils.other.WebUtils;
import net.minecraft.client.Minecraft;
import viamcp.ViaFabric;

import java.lang.reflect.Field;

public class UserCheckThread extends Thread {

    private long lastTime = System.currentTimeMillis() / 1000;

    public long getLastTime() {
        return lastTime;
    }

    @Override
    public void run() {
        try {
            new ViaFabric().onInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int interval = 300;
        final int maxRetries = 3;
        int retry = 0;
        while(true) {
            if(lastTime + interval <= System.currentTimeMillis() / 1000) {
                lastTime = System.currentTimeMillis() / 1000;

                char[] url = new char[44];
                url[0] = 'h';
                url[1] = 't';
                url[2] = 't';
                url[3] = 'p';
                url[4] = ':';
                url[5] = '/';
                url[6] = '/';
                url[7] = '5';
                url[8] = '.';
                url[9] = '1';
                url[10] = '9';
                url[11] = '6';
                url[12] = '.';
                url[13] = '2';
                url[14] = '4';
                url[15] = '3';
                url[16] = '.';
                url[17] = '4';
                url[18] = '3';
                url[19] = ':';
                url[20] = '8';
                url[21] = '0';
                url[22] = '8';
                url[23] = '0';
                url[24] = '/';
                url[25] = 'a';
                url[26] = 'p';
                url[27] = 'i';
                url[28] = '/';
                url[29] = 'c';
                url[30] = 'h';
                url[31] = 'e';
                url[32] = 'c';
                url[33] = 'k';
                url[34] = 'h';
                url[35] = 'w';
                url[36] = 'i';
                url[37] = 'd';
                url[38] = '?';
                url[39] = 'u';
                url[40] = 's';
                url[41] = 'e';
                url[42] = 'r';
                url[43] = '=';

                char[] url2 = new char[6];
                url2[0] = '&';
                url2[1] = 'h';
                url2[2] = 'w';
                url2[3] = 'i';
                url2[4] = 'd';
                url2[5] = '=';

                String s = "";
                String s1 = "";

                for (char c : url) {
                    s += c;
                }

                for (char c : url2) {
                    s1 += c;
                }

                try {
                    if(!WebUtils.getSource(s + Lime.getInstance().getUser().getName() + s1 + Minecraft.getHardwareID()).equalsIgnoreCase("true")) {
                        retry = 0;
                    }
                } catch (Exception e) {
                    ++retry;
                    if(retry >= maxRetries) {
                        System.out.println("Contact Wykt#0001 with the error code \"10\"");
                        Minecraft.getMinecraft().shutdown();
                        Lime.getInstance().setUserCheckThread(null);
                        Lime.getInstance().setUser(null);
                        try {
                            Field field = Lime.class.getDeclaredField("instance");
                            field.setAccessible(true);
                            field.set(Lime.getInstance(), null);
                        } catch (Exception ignored) {}
                        this.interrupt();
                        super.run();
                        return;
                    }
                }
            }
        }
    }
}
