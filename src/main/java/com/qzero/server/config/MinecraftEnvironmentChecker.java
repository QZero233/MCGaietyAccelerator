package com.qzero.server.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MinecraftEnvironmentChecker {

    private MinecraftServerConfiguration configuration;

    public MinecraftEnvironmentChecker(MinecraftServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void checkMinecraftServerEnvironment() throws IOException {
        String serverName=configuration.getServerName();

        File serverDir=new File(serverName+"/");
        File serverJarFile=new File(serverDir,configuration.getServerJarFileName());

        if(!serverJarFile.exists())
            throw new IllegalStateException(String.format("Server %s does not have jar file(expected with name %s)",
                    serverName,configuration.getServerJarFileName()));

        File eulaFile=new File(serverDir,GlobalConfigurationManager.EULA_FILE_NAME);
        if(!eulaFile.exists())
            throw new IllegalStateException(String.format("Server %s does not have eula.txt", serverName));
        Map<String,String> eulaConfig=ConfigurationUtils.readConfiguration(eulaFile);
        String eula=eulaConfig.get("eula");
        if(!eula.equals("true"))
            throw new IllegalArgumentException("Eula is false, please read license and accept and start server again");

        File propertiesFile=new File(serverDir,GlobalConfigurationManager.DEFAULT_SERVER_PROPERTIES_NAME);
        if(!propertiesFile.exists())
            throw new IllegalStateException(String.format("Server %s does not have server.properties", serverName));

        if(!new File(configuration.getJavaPath()).exists())
            throw new IllegalStateException(String.format("Server %s 's java does not exist(expected in %s)",
                    serverName,configuration.getJavaPath()));
    }


}
