package lime.file.impl;

import lime.Lime;
import lime.file.LimeFile;
import lime.module.Module;

public class ModuleSaver extends LimeFile {
    public ModuleSaver(String path){
        super(path);
    }
    public void save(){
        // FORMAT: MODULE:BIND:ENABLED(BOOLEAN)
        String content = "";
        for(Module module : Lime.moduleManager.getModules()){
            content += module.getName() + ":" + module.getKey() + ":" + module.isToggled() + "\n";
        }
        saveFile(content);
    }
    public void load(){
        String content = loadFile();
        for(String str : content.split("\n")){
            try{
                Module module = Lime.moduleManager.getModuleByName(str.split(":")[0]);
                int keyCode = Integer.parseInt(str.split(":")[1]);
                boolean toggled = Boolean.parseBoolean(str.split(":")[2]);
                if(!module.isToggled() && toggled) module.toggle();
                module.setKey(keyCode);
            } catch (Exception ignored){
                System.out.println("Failed to parse " + str);
            }

        }
    }
}
