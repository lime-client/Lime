package lime.commands.impl;

import lime.Lime;
import lime.cgui.settings.Setting;
import lime.commands.Command;
import lime.module.Module;
import lime.utils.ChatUtils;

import java.io.*;

public class Config extends Command {
    @Override
    public String getAlias() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "Load / Delete / Save Config.";
    }

    @Override
    public String getSyntax() {
        return ".config <load/delete/save> <config_name>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        String defaultPath = "Lime" + File.separator + "configs" + File.separator;
        String type = args[0];
        boolean no = false;
        switch(type.toLowerCase()){
            case "load":
                String content = loadFile(defaultPath + args[1] + ".lime");
                if(content.equals("")){
                    ChatUtils.sendMsg("§cFile is empty or doesn't exist.");
                    no = true;
                }
                for(String str : content.split("\n")){
                    if(str.split(":").length > 3){
                        try{
                            String type2 = str.split(":")[0];
                            switch(type2){
                                case "C":
                                    Setting set = Lime.setmgr.getSettingByNameAndMod(str.split(":")[1], Lime.moduleManager.getModuleByName(str.split(":")[2]));
                                    set.setValString(str.split(":")[3]);
                                    break;
                                case "S":
                                    Setting set2 = Lime.setmgr.getSettingByNameAndMod(str.split(":")[1], Lime.moduleManager.getModuleByName(str.split(":")[2]));
                                    set2.setValDouble(Double.parseDouble(str.split(":")[3]));
                                    break;
                                case "B":
                                    Setting set3 = Lime.setmgr.getSettingByNameAndMod(str.split(":")[1], Lime.moduleManager.getModuleByName(str.split(":")[2]));
                                    set3.setValBoolean(Boolean.parseBoolean(str.split(":")[3]));
                                    break;
                            }
                        } catch (Exception ignored){

                        }
                    } else {
                        try{
                            Module mod = Lime.moduleManager.getModuleByName(str.split(":")[0]);
                            if(str.split(":")[1].equals("true") && !mod.isToggled()) mod.toggle();
                            if(str.split(":")[1].equals("false") && mod.isToggled()) mod.toggle();
                        } catch (Exception ignored){

                        }
                    }
                }
                if(!no) ChatUtils.sendMsg("Loaded §c" + args[1] + ".lime §7config");
                break;
            case "save":
                try{
                    String contentToSend = "";
                    for(Module module : Lime.moduleManager.getModules()){
                        contentToSend += module.getName() + ":" + module.isToggled() + "\n";
                    }
                    for(Setting set : Lime.setmgr.getSettings()){
                        if(set.isCombo()) contentToSend += "C:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValString() + "\n";
                        if(set.isSlider()) contentToSend += "S:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValDouble() + "\n";
                        if(set.isCheck()) contentToSend += "B:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValBoolean() + "\n";
                    }
                    saveFile(contentToSend, defaultPath + args[1] + ".lime");
                    ChatUtils.sendMsg("Saved config as §c" + args[1] + ".lime");
                } catch (Exception ignored){
                    ChatUtils.sendMsg("§4An exception occured when saving the config");
                }
                break;
            case "delete":
                File file = new File(defaultPath + args[1] + ".lime");
                if(file.exists()) file.delete();
                ChatUtils.sendMsg("Deleted §c" + args[1] + ".lime §7config");
                break;
        }
    }
    public void saveFile(String content, String path){
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path)));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (Exception ignored){

        }
    }
    public String loadFile(String path){
        File file = new File(path);
        StringBuilder content = new StringBuilder();
        try{
            if(!file.exists()){
                return "";
            }
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                content.append(line.concat("\n"));
            }
            reader.close();
        } catch (Exception ignored){

        }
        return content.toString();
    }
}
