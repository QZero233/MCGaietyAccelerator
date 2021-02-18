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
            File defaultJarFile=new File(GlobalConfigurationManager.DEFAULT_SERVER_JAR_NAME);
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
    }

}
