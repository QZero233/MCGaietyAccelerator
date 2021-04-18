package com.qzero.server.config.environment;

import com.qzero.server.config.minecraft.MinecraftServerConfigurationManager;
import com.qzero.server.utils.ConfigurationUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ServerEnvironmentChecker {

    private ServerEnvironment environment;

    public ServerEnvironmentChecker(ServerEnvironment environment) {
        this.environment = environment;
    }

    public void checkEnvironment() throws IOException {
        File serverFile=new File(MinecraftServerConfigurationManager.DEFAULT_SERVER_JAR_NAME);
        File eulaFile=new File(MinecraftServerConfigurationManager.EULA_FILE_NAME);

        if(!serverFile.exists())
            throw new IllegalStateException("Server jar file does not exists");
        if(!eulaFile.exists())
            throw new IllegalStateException("Eula file does not exists");

        if(!new File(environment.getCommandFilePath()).exists())
            throw new IllegalStateException("Command file does not exist");
        if(!new File(environment.getJavaPath()).exists())
            throw new IllegalStateException("Java does not exist");

        Map<String,String> eulaConfig= ConfigurationUtils.readConfiguration(eulaFile);
        String eula=eulaConfig.get("eula");
        if(!eula.equals("true"))
            throw new IllegalArgumentException("Eula is false, please read license and accept and start server again");
    }

}
