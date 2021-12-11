package lime.features.command.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lime.core.Lime;
import lime.features.command.Command;
import lime.ui.notifications.Notification;
import lime.utils.other.ChatUtils;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;

public class Config extends Command {
    @Override
    public String getUsage() {
        return "config load/save/delete/list <name>";
    }

    @Override
    public String[] getPrefixes() {
        return new String[] { "config", "cfg" };
    }

    @Override
    public void onCommand(String[] args) throws Exception {
        if(args.length == 1 || (!args[1].equalsIgnoreCase("save") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("load") && !args[1].equalsIgnoreCase("delete"))) {
            ChatUtils.sendMessage("Invalid arguments detected!");
            return;
        }
        switch(args[1].toLowerCase()) {
            case "save":
                if(args.length == 4) {
                    Lime.getInstance().getFileSaver().saveModules("Lime" + File.separator + "configs" + File.separator + args[2] + ".json", args[3], false);
                } else if(args.length == 3) {
                    Lime.getInstance().getFileSaver().saveModules("Lime" + File.separator + "configs" + File.separator + args[2] + ".json", "unknown", false);
                }
                //ChatUtils.sendMessage("Saved config to §aLime" + File.separator + "configs" + File.separator + args[2] + ".json");
                Lime.getInstance().getNotificationManager().addNotification("Saved config to §aLime" + File.separator + "configs" + File.separator + args[2] + ".json", Notification.Type.SUCCESS);
                break;
            case "load":
                if(Lime.getInstance().getFileSaver().applyJson("Lime" + File.separator + "configs" + File.separator + args[2] + ".json", false)) {
                    Lime.getInstance().getNotificationManager().addNotification("Loaded §a" + args[2] + " §fconfig", Notification.Type.SUCCESS);
                } else {
                    Lime.getInstance().getNotificationManager().addNotification("Failed to load §c" + args[2] + " §fconfig", Notification.Type.FAIL);
                }
                break;
            case "delete":
                if(args[2].equalsIgnoreCase("*")) {
                    File configPath = new File("Lime" + File.separator + "configs");
                    for (String s : configPath.list()) {
                        File configFile = new File("Lime" + File.separator + "configs" + File.separator + s);
                        configFile.delete();
                    }
                    Lime.getInstance().getNotificationManager().addNotification("Deleted §a all§f configs", Notification.Type.SUCCESS);
                } else {
                    File configFile = new File("Lime" + File.separator + "configs" + File.separator + args[2] + ".json");
                    if(configFile.exists()) {
                        configFile.delete();
                        Lime.getInstance().getNotificationManager().addNotification("Deleted §a" + args[2] + " §fconfig", Notification.Type.SUCCESS);
                        return;
                    }
                }
                break;
            case "list":
                File configPath = new File("Lime" + File.separator + "configs");
                ChatUtils.sendMessage("Found §a" + configPath.list().length + " §7configs");
                for (String s : configPath.list()) {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(new FileReader("Lime" + File.separator + "configs" + File.separator + s));

                    String author = "unknown";
                    String time = "unknown";

                    JsonArray jsonArray = jsonElement.getAsJsonObject().getAsJsonArray("informations");
                    for(JsonElement jsonElement1 : jsonArray) {
                        author = jsonElement1.getAsJsonObject().get("author").getAsString();
                        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy '§7at§a' HH:mm");
                        time = formatter.format(jsonElement1.getAsJsonObject().get("time").getAsLong() * 1000);
                    }

                    ChatUtils.sendMessage("§a" + s.replace(".json", "") + " §7| Made by §a" + author + " §7the §a" + time);
                }
                break;
        }
    }
}
