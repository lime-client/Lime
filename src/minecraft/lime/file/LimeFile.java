package lime.file;

import java.io.*;

public class LimeFile {
    String path;
    public LimeFile(String path){
        this.path = path;
    }
    public void saveFile(String content){
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path)));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (Exception ignored){

        }
    }
    public String loadFile(){
        File file = new File(path);
        StringBuilder content = new StringBuilder();
        try{
            if(!file.exists()){
                file.createNewFile();
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
