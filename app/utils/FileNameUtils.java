package utils;

public class FileNameUtils {

    public static String getExtension(String filename){
        String[] fileNameSplit = filename.split("\\.");
        String fileExtension = fileNameSplit[fileNameSplit.length-1];
        return fileExtension;
    }
}
