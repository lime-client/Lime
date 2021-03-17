package lime.file.impl;

import lime.Lime;
import lime.settings.Setting;
import lime.file.LimeFile;

public class SettingsSaver extends LimeFile {
    public SettingsSaver(String path){
        super(path);
    }
    public void save(){
        String content = "";
        for(Setting set : Lime.setmgr.getSettings()){
            if(set.isCombo()) content += "C:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValString() + "\n";
            if(set.isSlider()) content += "S:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValDouble() + "\n";
            if(set.isCheck()) content += "B:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValBoolean() + "\n";
            if(set.isColor()) content += "CC:" + set.getName() + ":" + set.getParentMod().getName() + ":" + set.getValColor() + "\n";
        }
        saveFile(content);
    }
    public void load(){
        String content = loadFile();
        for(String str : content.split("\n")){
            try{
                String type = str.split(":")[0];
                switch(type){
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
                    case "CC":
                        Setting set4 = Lime.setmgr.getSettingByNameAndMod(str.split(":")[1], Lime.moduleManager.getModuleByName(str.split(":")[2]));
                        set4.setColor(Integer.parseInt(str.split(":")[3]));
                        break;
                }
            } catch (Exception ignored){
                System.out.println("Failed parse " + str);
            }
        }
    }
}
