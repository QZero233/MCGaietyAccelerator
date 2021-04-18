package com.qzero.server.config.minecraft;

import com.qzero.server.config.IConfigurationManager;
import com.qzero.server.config.environment.ServerEnvironment;
import com.qzero.server.utils.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MinecraftServerConfigurationManager implements IConfigurationManager {

    private Logger log = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_SERVER_JAR_NAME = "server.jar";
    public static final String EULA_FILE_NAME = "eula.txt";
    public static final String DEFAULT_SERVER_PROPERTIES_NAME = "server.properties";

    public static final String SERVER_CONFIG_FILE_NAME = "serverConfig.config";

    private Map<String, MinecraftServerConfiguration> mcServers = new HashMap<>();

    private ServerEnvironment environment;

    public MinecraftServerConfigurationManager(ServerEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void loadConfig() throws Exception {
        //Load minecraft servers
        File currentDir = new File("");
        currentDir = currentDir.getAbsoluteFile();
        File[] fileArray = currentDir.listFiles();
        for (File file : fileArray) {
            if (!file.isDirectory())
                continue;

            String[] serverConfigFileName = file.list((File dir, String name) -> name.equals("serverConfig.config"));
            if (serverConfigFileName == null || serverConfigFileName.length == 0)
                continue;


            String serverName = file.getName();
            log.debug("Found Minecraft server " + serverName);

            File configurationFile = new File(serverName + "/" + SERVER_CONFIG_FILE_NAME);

            Map<String, String> config = ConfigurationUtils.readConfiguration(configurationFile);
            MinecraftServerConfiguration configuration = ConfigurationUtils.configToJavaBeanWithOnlyStringFields(config, MinecraftServerConfiguration.class);

            configuration.setServerName(serverName);

            if (!config.containsKey("serverJarFileName"))
                configuration.setServerJarFileName(DEFAULT_SERVER_JAR_NAME);
            if (!config.containsKey("javaPath"))
                configuration.setJavaPath(environment.getJavaPath());
            if (!config.containsKey("javaParameter"))
                configuration.setJavaParameter(environment.getJavaParameter());

            config.remove("serverJarFileName");
            config.remove("javaPath");
            config.remove("javaParameter");
            config.remove("autoConfigCopy");

            configuration.setCustomizedServerProperties(config);

            mcServers.put(serverName, configuration);
        }
    }

    public Map<String,MinecraftServerConfiguration> getMcServers(){
        return mcServers;
    }

    public void updateMinecraftServerConfig(String serverName, String key, String value) throws IOException {
        File configurationFile = new File(serverName + "/" + SERVER_CONFIG_FILE_NAME);
        ConfigurationUtils.updateConfiguration(configurationFile, key, value);
    }
    public MinecraftServerConfiguration getMinecraftServerConfig(String serverName){
        return mcServers.get(serverName);
    }

}
