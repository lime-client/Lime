package lime.ui.realaltmanager;

import lime.ui.realaltmanager.guis.AltManagerScreen;

import java.util.ArrayList;

public class AltManager {
    private final ArrayList<Alt> alts;
    private final AltManagerScreen altManagerScreen;

    public AltManager() {
        this.alts = new ArrayList<>();
        this.altManagerScreen = new AltManagerScreen(this);
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
}
