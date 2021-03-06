package lime.file.impl;

import lime.file.LimeFile;

public class ApiKeySaver extends LimeFile {
    public ApiKeySaver(String path){
        super(path);
    }
    public String load(){
        return loadFile();
    }
    public void save(String apikey){
        saveFile(apikey);
    }
}
