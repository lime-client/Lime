package lime.ui.realaltmanager;

import lime.ui.realaltmanager.guis.AltManagerScreen;

import java.util.ArrayList;

public class AltManager {
    private final ArrayList<Alt> alts;
    private final AltManagerScreen altManagerScreen;

    public AltManager() {
        this.alts = new ArrayList<>();
        this.altManagerScreen = new AltManagerScreen(this);

        this.alts.add(new Alt("ne2453gro1", "negro2pass"));
        this.alts.add(new Alt("neg4642ro1", "negro2pass"));
        this.alts.add(new Alt("negr52452o1", "negro2pass"));
        this.alts.add(new Alt("negr4534534o1", "negro2p2ass"));
    }

    public AltManagerScreen getAltManagerScreen() {
        return altManagerScreen;
    }

    public void saveAlts() {
        // TODO: SAVE JSON
    }

    public void loadAlts() {
        // TODO: LOAD JSON
    }

    public void addAlt(Alt alt) {
        this.alts.add(alt);
    }

    public void removeAlt(Alt alt) {
        this.alts.remove(alt);
    }

    public ArrayList<Alt> getAlts() {
        return alts;
    }
}
