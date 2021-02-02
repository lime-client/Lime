package viamcp.exemple;

import viamcp.ViaFabric;

public class MainExemple {

    public static MainExemple instance = new MainExemple();

/*-----------------------------------------------------------------*/
// You need to initialize viaFabric this way in your main class.
/*-----------------------------------------------------------------*/

    public void startClient() {

        /* ViaVersion */
        try {
            new ViaFabric().onInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* ---------- */

    }

    public void stopClient() {

    }

}
