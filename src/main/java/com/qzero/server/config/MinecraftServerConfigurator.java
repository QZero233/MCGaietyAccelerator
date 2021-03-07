package com.qzero.server.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class MinecraftServerConfigurator {

    public static void configServer(String serverName) throws IOException {
        GlobalConfigurationManager configurationManager=GlobalConfigurationManager.getInstance();
        MinecraftServerConfiguration configuration=configurationManager.getMinecraftServerConfig(serverName);

        if(configuration==null)
            throw new IllegalArgumentException(String.format("Server named %s does not exist", serverName));

        File serverDir=new File(serverName+"/");

        //Prepare jar file
        File serverJarFile=new File(serverDir,configuration.getServerJarFileName());
        if(!serverJarFile.exists()){
            File defaultJarFile=new File(configuration.getServerJarFileName());
            if(!defaultJarFile.exists())
                defaultJarFile=new File(GlobalConfigurationManager.DEFAULT_SERVER_JAR_NAME);

            Files.copy(defaultJarFile.toPath(),serverJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        //Prepare eula
        File eulaFile=new File(serverDir,GlobalConfigurationManager.EULA_FILE_NAME);
        File defaultEula=new File(GlobalConfigurationManager.EULA_FILE_NAME);
        Files.copy(defaultEula.toPath(),eulaFile.toPath(),StandardCopyOption.REPLACE_EXISTING);

        //Prepare server.properties
        File propertiesFile=new File(serverDir,GlobalConfigurationManager.DEFAULT_SERVER_PROPERTIES_NAME);
        File defaultPropertiesFile=new File(GlobalConfigurationManager.DEFAULT_SERVER_PROPERTIES_NAME);
        Files.copy(defaultPropertiesFile.toPath(),propertiesFile.toPath(),StandardCopyOption.REPLACE_EXISTING);

        Map<String,String> properties= ConfigurationUtils.readConfiguration(propertiesFile);
        Map<String,String> customizedProperties=configuration.getCustomizedServerProperties();
        properties.putAll(customizedProperties);
        ConfigurationUtils.writeConfiguration(propertiesFile,properties);

        //Copy other specified files
        String needCopyFileNamesTotal=configuration.getAutoConfigCopy();
        if(needCopyFileNamesTotal!=null && !needCopyFileNamesTotal.equals("")){
            String[] needCopyFileNames=needCopyFileNamesTotal.split(",");
            for(String fileName:needCopyFileNames){
                File origin=new File(fileName);
                File dst=new File(serverDir,fileName);
                if(!dst.exists()){
                    if(!origin.isDirectory()){
                        Files.copy(origin.toPath(),dst.toPath(),StandardCopyOption.REPLACE_EXISTING);
                    }else{
                        copyDir(origin,dst);
                    }

                }

            }
        }
    }

    private static void copyDir(File srcDir,File dstDir) throws IOException {
        if(!srcDir.isDirectory()){
            Files.copy(srcDir.toPath(),dstDir.toPath(),StandardCopyOption.REPLACE_EXISTING);
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
