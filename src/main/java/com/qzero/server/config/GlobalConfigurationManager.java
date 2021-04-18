package com.qzero.server.config;

import com.qzero.server.config.authorize.AuthorizeConfigurationManager;
import com.qzero.server.config.environment.ServerEnvironmentConfigurationManager;
import com.qzero.server.config.manager.ServerManagerConfigurationManager;
import com.qzero.server.config.minecraft.MinecraftServerConfigurationManager;

/**
 * It will load config when start
 * It won't interact with file system unless loadConfig is called
 */
public class GlobalConfigurationManager {

    private static GlobalConfigurationManager instance;

    private AuthorizeConfigurationManager authorizeConfigurationManager;
    private ServerEnvironmentConfigurationManager environmentConfigurationManager;
    private ServerManagerConfigurationManager managerConfigurationManager;
    private MinecraftServerConfigurationManager serverConfigurationManager;

    private GlobalConfigurationManager(){

    }

    public static GlobalConfigurationManager getInstance() {
        if(instance==null)
            instance=new GlobalConfigurationManager();
        return instance;
    }

    public void loadConfig() throws Exception {
        authorizeConfigurationManager=new AuthorizeConfigurationManager();
        authorizeConfigurationManager.loadConfig();

        environmentConfigurationManager=new ServerEnvironmentConfigurationManager();
        environmentConfigurationManager.loadConfig();

        managerConfigurationManager=new ServerManagerConfigurationManager();
        managerConfigurationManager.loadConfig();

        serverConfigurationManager=new MinecraftServerConfigurationManager(environmentConfigurationManager.getServerEnvironment());
        serverConfigurationManager.loadConfig();
    }

    public AuthorizeConfigurationManager getAuthorizeConfigurationManager() {
        return authorizeConfigurationManager;
    }

    public ServerEnvironmentConfigurationManager getEnvironmentConfigurationManager() {
        return environmentConfigurationManager;
    }

    public ServerManagerConfigurationManager getManagerConfigurationManager() {
        return managerConfigurationManager;
    }

    public MinecraftServerConfigurationManager getServerConfigurationManager() {
        return serverConfigurationManager;
    }
}
