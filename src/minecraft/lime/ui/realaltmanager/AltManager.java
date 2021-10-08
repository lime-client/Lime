package lime.ui.realaltmanager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import lime.ui.realaltmanager.guis.AltManagerScreen;
import net.minecraft.client.gui.GuiScreen;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class AltManager {
    private final ArrayList<Alt> alts;
    private final AltManagerScreen altManagerScreen;
    private GuiScreen lastScreen;

    public AltManager() {
        this.alts = new ArrayList<>();
        this.altManagerScreen = new AltManagerScreen(this);
        loadAlts();
    }

    public AltManagerScreen getAltManagerScreen() {
        return altManagerScreen;
    }

    public void setLastScreen(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
    }

    public GuiScreen getLastScreen() {
        return lastScreen;
    }

    public void saveAlts() {
        // TODO: SAVE JSON
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter("Lime/alts.json"));
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("alts");
            jsonWriter.beginArray();
            for (Alt alt : alts) {
                jsonWriter.beginObject();
                jsonWriter.name("mail");
                jsonWriter.value(alt.getMail());
                jsonWriter.name("password");
                jsonWriter.value(alt.getPassword());
                jsonWriter.name("username");
                jsonWriter.value(alt.getName());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
            jsonWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAlts() {
        alts.clear();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new FileReader("Lime/alts.json"));
            JsonArray alts = jsonElement.getAsJsonObject().getAsJsonArray("alts");
            for (JsonElement alt : alts) {
                this.alts.add(new Alt(alt.getAsJsonObject().get("mail").getAsString(), alt.getAsJsonObject().get("password").getAsString(), alt.getAsJsonObject().get("username").getAsString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Alt getRandomAlt() {
        if(alts.isEmpty()) return null;
        return alts.get(new Random().nextInt(alts.size() - 1));
    }

    public void addAlt(Alt alt) {
        boolean notIn = false;
        for (Alt alt1 : alts) {
            if(alt1.getMail().equals(alt.getMail())) {
                notIn = true;
            }
        }
        if(!notIn) {
            this.alts.add(alt);
        }
    }

    public void removeAlt(Alt alt) {
        this.alts.remove(alt);
    }

    public ArrayList<Alt> getAlts() {
        return alts;
    }
}
