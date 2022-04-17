package com.qzero.server.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {

    public static void copyDir(File srcDir, File dstDir) throws IOException {
        if(!srcDir.isDirectory()){
            Files.copy(srcDir.toPath(),dstDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return;
        }

        if(!dstDir.exists())
            dstDir.mkdirs();

        File[] files=srcDir.listFiles();
        for(File file:files){
            if(!file.isDirectory()){
                Files.copy(file.toPath(),new File(dstDir,file.getName()).toPath(),StandardCopyOption.REPLACE_EXISTING);
            }else{
                copyDir(file,new File(dstDir,file.getName()));
            }
        }
    }

}
