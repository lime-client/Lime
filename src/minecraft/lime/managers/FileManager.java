package lime.managers;

import lime.file.LimeFile;
import lime.file.impl.ApiKeySaver;
import lime.file.impl.ModuleSaver;
import lime.file.impl.SettingsSaver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public String limeFolder(){
        return "Lime";
    }
    private List<LimeFile> clientFiles;
    public FileManager(){
        this.clientFiles = new ArrayList<>();
        final String folder = limeFolder();
        clientFiles.add(new ModuleSaver(folder + File.separator + "modules.txt"));
        clientFiles.add(new SettingsSaver(folder + File.separator + "settings.txt"));
        clientFiles.add(new ApiKeySaver(folder + File.separator + "apikey.txt"));
    }
    public LimeFile getFileByClass(final Class<? extends LimeFile> classe){
        return clientFiles.stream().filter(file -> file.getClass() == classe).findFirst().orElse(null);
    }
}
